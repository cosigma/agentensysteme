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
		// TODO check perception.state == alive

		CellMessage cellMsg = perceptionMsg.getCell();
		Point nextPos = new Point(cellMsg.getRow(), cellMsg.getCol());
		Cell nextCell = map.get(nextPos);

		if (nextCell == null) {
			nextCell = new Cell(nextPos);
			map.put(nextPos, nextCell);

			Point[] adjacents = { new Point(nextPos.x, nextPos.y - 1), new Point(nextPos.x, nextPos.y + 1),
					new Point(nextPos.x - 1, nextPos.y), new Point(nextPos.x + 1, nextPos.y) };
			for (Point adjacent : adjacents) {
				if (!map.containsKey(adjacent)) {
					Cell cell = new Cell(adjacent);
					map.put(adjacent, cell);
					cell.setType(CellType.UNKNOWN);
				}
			}
		}

		nextCell.setFood(cellMsg.getFood());
		nextCell.setSmell(cellMsg.getSmell());
		nextCell.setStench(cellMsg.getStench());
		nextCell.setType(cellMsg.getType());

		if (currentCell != null) {
			Point currentPos = currentCell.getPosition();
			if(currentPos.equals(nextPos)){
				onMovementBlocked(perceptionMsg.getAction(), nextPos);
			}
		}

		if (currentCell == null) {
			currentCell = nextCell;
		}

		// TODO unify message preparation
		reply = msg.createReply();
		reply.setPerformative(ACLMessage.REQUEST);
		reply.setSender(getAID());
	}

	private void onMovementBlocked(Action action, Point position){
		Point adjPos = null;
		switch(action){
		case ANT_ACTION_UP:
			adjPos = new Point(position.x, position.y - 1);
			break;
		case ANT_ACTION_DOWN:
			adjPos = new Point(position.x, position.y + 1);
			break;
		case ANT_ACTION_LEFT:
			adjPos = new Point(position.x, position.y - 1);
			break;
		case ANT_ACTION_RIGHT:
			adjPos = new Point(position.x, position.y + 1);
			break;
		default:
			LOG.error("unexpected action: {}", action);
			return;
		}
		
		Cell adjCell = map.get(adjPos);
		if(adjCell == null){
			adjCell = new Cell(adjPos);
			map.put(adjPos, adjCell);
		}
		adjCell.setType(CellType.BLOCKED);
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
