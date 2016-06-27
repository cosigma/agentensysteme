package de.hsb.ants.agent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.Action;
import de.hsb.ants.map.Cell;
import de.hsb.ants.map.CellType;
import de.hsb.ants.map.Point;
import de.hsb.ants.msg.CellMessage;
import de.hsb.ants.msg.MessageUtils;
import de.hsb.ants.msg.PerceptionMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Ant extends Agent {

	private static final long serialVersionUID = -5620493012637529572L;

	static final Logger LOG = LoggerFactory.getLogger(Ant.class);

	private MessageUtils msgUtils;
	private Map<Point, Cell> map = new HashMap<Point, Cell>(2048);
	private Cell currentCell = null;
	private Queue<Action> actionQueue = new ArrayDeque<Action>();

	private ACLMessage reply = null;

	@Override
	protected void setup() {
		// grab and use config
		Object[] args = getArguments();
		if (args == null || args.length == 0 || !(args[0] instanceof AntConfig)) {
			LOG.error("no config found for agent {}", getLocalName());
			doDelete();
			return;
		}
		AntConfig config = (AntConfig) args[0];
		msgUtils = config.getMessageUtils();
		String service = config.getService();

		// login
		AID antWorld = findServiceAID(service, service);
		if (antWorld == null) {
			LOG.error("game service {} not found, deleting agent {}", service, getLocalName());
			doDelete();
			return;
		}
		sendLogin(antWorld);
		LOG.info("login message sent: {}", getLocalName());

		// add behaviour for NOT_UNDERSTOOD from antworld
		MessageTemplate mtAWNotUnderstood = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
		addBehaviour(new ReceiveMessageBehaviour(mtAWNotUnderstood, this::onAWNotUnderstood));

		// add behaviour for INFORM from antworld
		MessageTemplate mtAWInform = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		addBehaviour(new ReceiveMessageBehaviour(mtAWInform, this::onAWInform));

		// add behaviour for REFUSE from antworld
		MessageTemplate mtAWRefuse = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
		addBehaviour(new ReceiveMessageBehaviour(mtAWRefuse, this::onAWRefuse));

		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 7110665672290159055L;

			@Override
			public void action() {
				if (reply == null) {
					return;
				}

				// TODO add logic to decide next action
				reply.setContent(msgUtils.DOWN);

				send(reply);

				reply = null;
			}
		});
	}

	private void onAWNotUnderstood(ACLMessage msg) {
		LOG.error("service returned NOT_UNDERSTOOD: {}", msg);
		doDelete();
	}

	private void onAWInform(ACLMessage msg) {
		String content = msg.getContent();
		PerceptionMessage perceptionMsg = MessageUtils.getPerception(content);
		CellMessage cellMsg = perceptionMsg.getCell();

		Point currentPos = new Point(cellMsg.getRow(), cellMsg.getCol());
		currentCell = map.get(currentPos);

		// on first cell or on entering unknown cell
		if (currentCell == null || currentCell.getType() == CellType.UNKNOWN) {
			LOG.debug("entered new cell at {}", currentPos);
			currentCell = new Cell(currentPos);
			map.put(currentPos, currentCell);
			currentCell.setType(cellMsg.getType());
			setAdjacentUnknown(currentPos);
		}

		updateCell(currentCell, cellMsg);

		// put this or something similar into a method
		reply = msg.createReply();
		reply.setPerformative(ACLMessage.REQUEST);
		reply.setSender(getAID());
	}

	private void onAWRefuse(ACLMessage msg) {
		String content = msg.getContent();
		PerceptionMessage perceptionMsg = MessageUtils.getPerception(content);
		CellMessage cellMsg = perceptionMsg.getCell();
		
		Point currentPos = new Point(cellMsg.getRow(), cellMsg.getCol());
		Point oldPos = currentCell.getPosition();

		updateCell(currentCell, cellMsg);
		
		if (!"ALIVE".equals(perceptionMsg.getState())) {
			LOG.info("is dead at {}", currentPos);
			doDelete();
			return;
		}

		if (currentPos.equals(oldPos)) {
			checkMovementBlocked(perceptionMsg.getAction(), currentPos);
		}
	}

	private void setAdjacentUnknown(Point pos) {
		int count = 0;
		Point[] adjacents = { new Point(pos.x, pos.y - 1), new Point(pos.x, pos.y + 1), new Point(pos.x - 1, pos.y),
				new Point(pos.x + 1, pos.y) };
		for (Point adjacent : adjacents) {
			if (!map.containsKey(adjacent)) {
				Cell cell = new Cell(adjacent);
				map.put(adjacent, cell);
				cell.setType(CellType.UNKNOWN);
				++count;
			}
		}
		LOG.debug("added {} unknown cell(s) around cell at {}", count, pos);
	}

	private void updateCell(Cell cell, CellMessage cellMsg) {
		cell.setFood(cellMsg.getFood());
		cell.setSmell(cellMsg.getSmell());
		cell.setStench(cellMsg.getStench());
		LOG.debug("updated cell at {}", cell.getPosition());
	}

	private void checkMovementBlocked(Action action, Point position) {
		Point blockedPos = null;
		switch (action) {
		case ANT_ACTION_UP:
			blockedPos = new Point(position.x, position.y - 1);
			break;
		case ANT_ACTION_DOWN:
			blockedPos = new Point(position.x, position.y + 1);
			break;
		case ANT_ACTION_LEFT:
			blockedPos = new Point(position.x, position.y - 1);
			break;
		case ANT_ACTION_RIGHT:
			blockedPos = new Point(position.x, position.y + 1);
			break;
		default:
			// not a movement action
			return;
		}

		Cell blockedCell = map.get(blockedPos);
		blockedCell.setType(CellType.BLOCKED);
		LOG.debug("movement blocked on cell at {}", blockedPos);
	}

	private AID findServiceAID(String name, String type) {
		try {
			ServiceDescription filter = new ServiceDescription();
			filter.setName(name);
			filter.setType(type);

			DFAgentDescription dfd = new DFAgentDescription();
			dfd.addServices(filter);

			DFAgentDescription[] results = DFService.search(this, dfd);
			if (results == null || results.length == 0) {
				return null;
			}
			if (results.length > 1) {
				LOG.warn("more than once instance of service {}:{} found, defaulting to first occurrence", name, type);
			}
			return results[0].getName();

		} catch (FIPAException e) {
			LOG.error("exception while searching service {}:{} : {}", name, type, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private void sendLogin(AID receiver) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.setLanguage("json");
		msg.setContent(msgUtils.LOGIN);
		msg.addReceiver(receiver);
		send(msg);
	}
}
