package de.hsb.ants.agent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.Action;
import de.hsb.ants.gui.AgentListener;
import de.hsb.ants.map.BFSRunner;
import de.hsb.ants.map.Cell;
import de.hsb.ants.map.CellType;
import de.hsb.ants.map.Point;
import de.hsb.ants.msg.CellMessage;
import de.hsb.ants.msg.MessageUtil;
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

/**
 * An agent for the game AntWorld.
 * 
 * @author Daniel
 *
 */
public class Ant extends Agent {

	private static final long serialVersionUID = -5620493012637529572L;

	static final Logger LOG = LoggerFactory.getLogger(Ant.class);

	private Collection<AgentListener> listeners;
	private MessageUtil msgUtil = null;

	private Map<Point, Cell> map = new HashMap<Point, Cell>(2048);

	private PerceptionMessage currentPerception = null;
	private Cell currentCell = null;
	private Deque<String> actionQueue = new ArrayDeque<String>();
	private ACLMessage reply = null;

	@Override
	protected void setup() {
		// grab and use config
		Object[] args = getArguments();
		if (args == null || args.length == 0 || !(args[0] instanceof AntConfig)) {
			LOG.error("no agent config found");
			doDelete();
			return;
		}
		AntConfig config = (AntConfig) args[0];
		msgUtil = config.getMessageUtil();
		listeners = config.getListeners();
		String service = config.getService();

		// login
		DFAgentDescription[] results = findService(service, service);
		if (results == null || results.length == 0) {
			LOG.error("game service {} not found", service);
			doDelete();
			return;
		}
		if (results.length > 1) {
			LOG.warn("more than once instance of service {}:{} found, defaulting to first occurrence", service,
					service);
		}
		AID antWorld = results[0].getName();
		sendLogin(antWorld);
		LOG.info("login message sent: {}", getLocalName());

		// add behaviour for NOT_UNDERSTOOD message from antworld
		MessageTemplate mtAWNotUnderstood = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
		addBehaviour(new ReceiveMessageBehaviour(mtAWNotUnderstood, this::onAWNotUnderstood));

		// add behaviour for INFORM message from antworld
		MessageTemplate mtAWInform = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		addBehaviour(new ReceiveMessageBehaviour(mtAWInform, this::onAWInform));

		// add behaviour for REFUSE messag from antworld
		MessageTemplate mtAWRefuse = MessageTemplate.and(MessageTemplate.MatchSender(antWorld),
				MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
		addBehaviour(new ReceiveMessageBehaviour(mtAWRefuse, this::onAWRefuse));

		// add behaviour for any other message
		MessageTemplate mtOther = MessageTemplate
				.not(MessageTemplate.or(mtAWNotUnderstood, MessageTemplate.or(mtAWInform, mtAWRefuse)));
		addBehaviour(new ReceiveMessageBehaviour(mtOther, this::onUnknownMessage));

		// add behaviour to decide and send the next action
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = -4875267971659894785L;

			@Override
			public void action() {
				decideNextAction();
			}
		});
	}

	/**
	 * Decides which action to do next and sends a matching message to the game.
	 * service.
	 */
	private void decideNextAction() {
		if (reply == null) {
			return;
		}

		// send queued action
		if (!actionQueue.isEmpty()) {
			String action = actionQueue.poll();
			LOG.trace("sending action {}, {} actions pending", action, actionQueue.size());
			sendReply(action);
			return;
		}

		// is carrying food
		if (currentPerception.getCurrentFood() > 0) {

			// drop if on start cell
			if (currentCell.getType() == CellType.START) {
				LOG.debug("dropping food at {}", currentCell.getPosition());
				sendReply(msgUtil.DROP);
				return;

			}

			// otherwise, find path to start cell
			LOG.debug("searching start cell");
			BFSRunner startSearcher = new BFSRunner(map, msgUtil, currentCell, Cell::isSafeAccessible, Cell::isStart,
					actionQueue);
			Cell startCell = startSearcher.search();
			// TODO replace with bidirectional search
			if (startCell != null) {
				LOG.debug("found start from {} to {}", currentCell.getPosition(), startCell.getPosition());
			} else {
				LOG.error("could not find start from {}", currentCell.getPosition());
				doDelete();
			}
			return;
		}

		// current cell has food
		if (currentCell.hasFood()) {
			LOG.debug("collecting food at {}", currentCell.getPosition());
			sendReply(msgUtil.COLLECT);
			return;
		}

		// search a path to a useful cell
		int[] ranges = { 1, 2, 3, 5, 8, 13, 20 };
		BFSRunner foodSearcher = getSearcher(Cell::isSafeAccessible, Cell::hasFood);
		BFSRunner safeUnknownSearcher = getSearcher(Cell::isSafeAccessible, Cell::isSafeUnknown);

		// search within progressively growing ranges first
		for (Integer range : ranges) {
			// search for a cell with food
			LOG.trace("searching for path to food within range of {}", range);
			Cell foodCell = foodSearcher.search(range);
			if (foodCell != null) {
				LOG.debug("found path to food from {} to {}", currentCell.getPosition(), foodCell.getPosition());
				return;
			}

			// search for a safe, unexplored cell
			LOG.trace("searching for path to safe, unexplored cell within range of {}", range);
			Cell safeUnknownCell = safeUnknownSearcher.search(range);
			if (safeUnknownCell != null) {
				LOG.debug("found path to safe unexplored cell from {} to {}", currentCell.getPosition(),
						safeUnknownCell.getPosition());
				return;
			}
		}

		// search for a cell with food
		LOG.trace("searching for path to food within max range");
		Cell foodCell = foodSearcher.search();
		if (foodCell != null) {
			LOG.debug("found path to food from {} to {}", currentCell.getPosition(), foodCell.getPosition());
			return;
		}

		// search for a safe, unexplored cell
		LOG.trace("searching for path to safe, unexplored cell within max range");
		Cell safeUnknownCell = safeUnknownSearcher.search();
		if (safeUnknownCell != null) {
			LOG.debug("found path to safe unexplored cell from {} to {}", currentCell.getPosition(),
					safeUnknownCell.getPosition());
			return;
		}

		// search for an unsafe, unexplored cell
		LOG.trace("searching for path to unsafe, unexplored cell");
		BFSRunner unsafeUnknownSearcher = getSearcher(Cell::isAccessible, Cell::isUnsafeUnknown);
		Cell unsafeUnknownCell = unsafeUnknownSearcher.search();
		if (unsafeUnknownCell != null) {
			LOG.debug("found path to unsafe unexplored cell from {} to {}", currentCell.getPosition(),
					unsafeUnknownCell.getPosition());
			return;
		}

		LOG.debug("found no path to anywhere useful");
		doDelete();

	}

	/**
	 * A convenience method that returns a BFSRunner using this agent's map,
	 * message utils, current cell and action queue.
	 * 
	 * @param isAccessible
	 *            a predicate which defines which adjacent nodes should be
	 *            visited
	 * @param isGoal
	 *            a predicate which defines the goal condition
	 * @return
	 */
	private BFSRunner getSearcher(Predicate<Cell> isAccessible, Predicate<Cell> isGoal) {
		return new BFSRunner(map, msgUtil, currentCell, isAccessible, isGoal, actionQueue);
	}

	/**
	 * Logs and silently discards messages not caught by any message template.
	 * 
	 * @param msg
	 */
	private void onUnknownMessage(ACLMessage msg) {
		LOG.warn("received unknown message: {}", msg);
	}

	/**
	 * Deletes the agent on receiving a message with performative
	 * "NOT_UNDERSTOOD" from the game service. This makes it easy to check if
	 * any malformed messages were sent to the game service by this agent.
	 * 
	 * @param msg
	 */
	private void onAWNotUnderstood(ACLMessage msg) {
		LOG.error("service returned NOT_UNDERSTOOD: {}", msg);
		doDelete();
	}

	/**
	 * Process a message with performative "INFORM", i.e. a perception message.
	 * 
	 * @param msg
	 */
	private void onAWInform(ACLMessage msg) {
		String content = msg.getContent();
		currentPerception = MessageUtil.getPerception(content);
		CellMessage cellMsg = currentPerception.getCell();

		Point currentPos = new Point(cellMsg.getCol(), cellMsg.getRow());
		currentCell = map.get(currentPos);

		// on entering an unknown cell
		if (currentCell == null || currentCell.isTypeIn(CellType.UNSAFE_UNKNOWN, CellType.SAFE_UNKNOWN)) {
			LOG.debug("entered new cell at {}", currentPos);
			// on entering the very first cell
			if (currentCell == null) {
				LOG.info("entered start cell at {}", currentPos);
				currentCell = createCell(currentPos, cellMsg.getType());
				map.put(currentPos, currentCell);
			} else {
				// update permanent values
				setCellType(currentCell, cellMsg.getType());
				currentCell.setStench(cellMsg.getStench());
			}
			updateCellFood(currentCell, cellMsg);
			setAdjacentUnknown(currentCell);
			propagatedSafetyResolve(currentCell);

		} else {
			updateCellFood(currentCell, cellMsg);
		}

		prepareReply(msg);
	}

	/**
	 * Processes a message with performative "REFUSE". Refuse-messages are sent
	 * to an ant when it tries to move onto a cell with a rock, if the ant is
	 * dead, if it tries to pick up food where none exists and possibly in other
	 * cases as well.
	 * 
	 * @param msg
	 */
	private void onAWRefuse(ACLMessage msg) {
		String content = msg.getContent();
		PerceptionMessage perceptionMsg = MessageUtil.getPerception(content);
		CellMessage cellMsg = perceptionMsg.getCell();

		Point currentPos = new Point(cellMsg.getCol(), cellMsg.getRow());
		Point oldPos = currentCell.getPosition();

		updateCellFood(currentCell, cellMsg);

		// remove all pending actions
		actionQueue.clear();

		if (!"ALIVE".equals(perceptionMsg.getState())) {
			LOG.info("is dead at {}", currentPos);
			doDelete();
			return;
		}

		if (currentPos.equals(oldPos)) {
			checkMovementBlocked(perceptionMsg.getAction(), currentPos);
			prepareReply(msg);
			return;
		}

		LOG.error("unchecked refuse message: {}", msg);
		doDelete();
	}

	/**
	 * Called upon entering a previously unknown cell, this method adds cells of
	 * type UNKNOWN adjacent to the entered cell where no cells previously
	 * existed.
	 * 
	 * @param pos
	 */
	private void setAdjacentUnknown(Cell cell) {
		int count = 0;
		Point pos = cell.getPosition();
		for (Point adjPos : pos.allAdjacent()) {
			if (!map.containsKey(adjPos)) {
				Cell adjacent = createCell(adjPos, CellType.UNSAFE_UNKNOWN);
				map.put(adjPos, adjacent);
				++count;
			}
		}
		LOG.trace("added {} cell(s) of type around cell at {}", count, CellType.UNSAFE_UNKNOWN, pos);
	}

	/**
	 * Tries to resolve cells of type UNSAFE_UNKNOWN to SAFE_UNKNOWN around the given cell.
	 * or PIT.
	 * 
	 * @param cell
	 */
	private void propagatedSafetyResolve(Cell cell) {
		immediateSafetyResolve(cell, true);
	}

	/**
	 * Tries to resolve adjacent cells of type UNSAFE_UNKNOWN to either
	 * SAFE_UNKNOWN or PIT. If the propagate is set and no UNSAFE_UNKNOWN cells
	 * were resolved, it will call this method for all not-null cells of types
	 * START or FREE around those cells.
	 * 
	 * @param cell
	 * @param propagate
	 */
	private void immediateSafetyResolve(Cell cell, boolean propagate) {
		if (cell == null) {
			LOG.warn("called immediateSafetyResolve on cell NULL");
			return;
		}

		Point pos = cell.getPosition();
		Point[] adjacents = pos.allAdjacent();
		if (!cell.isTypeIn(CellType.FREE, CellType.START)) {
			LOG.warn("called immediateSafetyResolve on invalid cell of type {} at {}", pos, cell.getType());
			return;
		}

		// count adjacent pits and unsafe cells
		int pits = 0;
		Set<Cell> unsafes = new HashSet<Cell>();
		for (Point adjPos : adjacents) {
			Cell adjacent = map.get(adjPos);
			switch (adjacent.getType()) {
			case PIT:
				++pits;
				break;
			case UNSAFE_UNKNOWN:
				unsafes.add(adjacent);
				break;
			default:
				//
			}
		}

		// change cells, if possible
		int stench = cell.getStench();
		if (pits == stench) {
			for (Cell unsafe : unsafes) {
				LOG.debug("changed cell type at {} from {} to {}", unsafe.getPosition(), CellType.UNSAFE_UNKNOWN,
						CellType.SAFE_UNKNOWN);
				setCellType(unsafe, CellType.SAFE_UNKNOWN);
			}
		} else if (pits + unsafes.size() == stench) {
			for (Cell unsafe : unsafes) {
				LOG.debug("changed cell type at {} from {} to {}", unsafe.getPosition(), CellType.PIT,
						CellType.SAFE_UNKNOWN);
				setCellType(unsafe, CellType.PIT);
			}
		} else if (propagate) {
			for (Cell unsafe : unsafes) {
				pos = unsafe.getPosition();
				adjacents = pos.allAdjacent();
				LOG.debug("calling immediateSafetyResolve for adjacents of {}", pos);
				for (Point adjPos : adjacents) {
					Cell adjacent = map.get(adjPos);
					if (adjacent != null && adjacent.isTypeIn(CellType.FREE, CellType.START)) {
						LOG.debug("propagating safety resolve to {}", adjPos);
						immediateSafetyResolve(adjacent, false);
					}
				}
			}
		}
	}

	/**
	 * Updates the values of a cell according to the values contained in the
	 * given message.
	 * 
	 * @param cell
	 *            the cell that should be updated
	 * @param cellMsg
	 *            a message with new values for the cell
	 */
	private void updateCellFood(Cell cell, CellMessage cellMsg) {
		cell.setFood(cellMsg.getFood());
		cell.setSmell(cellMsg.getSmell());
		LOG.trace("updated cell at {}", cell.getPosition());
	}

	/**
	 * Check if an action was refused because it caused the ant to try to enter
	 * a blocked cell. Does nothing if the given action is not a movement
	 * action, i.e. an action other than UP, DOWN, LEFT or RIGHT.
	 * 
	 * @param action
	 *            an action
	 * @param position
	 *            the position on which the action was called
	 */
	private void checkMovementBlocked(Action action, Point position) {
		Point blockedPos = position.adjacent(action);
		if (blockedPos == null) {
			return;
		}
		LOG.debug("movement blocked on cell at {} : {}", blockedPos, action);
		Cell blockedCell = map.get(blockedPos);
		setCellType(blockedCell, CellType.BLOCKED);
	}

	/**
	 * Searches for a service of the given name and type
	 * 
	 * @param name
	 * @param type
	 * @return the results of the search
	 */
	private DFAgentDescription[] findService(String name, String type) {
		try {
			ServiceDescription filter = new ServiceDescription();
			filter.setName(name);
			filter.setType(type);

			DFAgentDescription dfd = new DFAgentDescription();
			dfd.addServices(filter);

			DFAgentDescription[] results = DFService.search(this, dfd);
			return results;

		} catch (FIPAException e) {
			LOG.error("exception while searching service {}:{} : {}", name, type, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sends a login message to the antworld service
	 * 
	 * @param receiver
	 *            the AID of the antworld service
	 */
	private void sendLogin(AID receiver) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.setLanguage("json");
		msg.setContent(msgUtil.LOGIN);
		msg.addReceiver(receiver);
		send(msg);
	}

	/**
	 * Prepares a reply to a message form the antworld service.
	 * 
	 * @param msg
	 */
	private void prepareReply(ACLMessage msg) {
		reply = msg.createReply();
		reply.setPerformative(ACLMessage.REQUEST);
		reply.setSender(getAID());
	}

	/**
	 * Sends a reply to the antworld service with the given content.
	 * 
	 * @param content
	 */
	private void sendReply(String content) {
		reply.setContent(content);
		send(reply);
		reply = null;
	}

	/**
	 * Creates a new cell with the given position and type.
	 * 
	 * @param position
	 * @param type
	 * @return
	 */
	private Cell createCell(Point position, CellType type) {
		for (AgentListener listener : listeners) {
			listener.changeCellType(position, type);
		}
		return new Cell(position, type);
	}

	/**
	 * Changes the cell type of a cell, notifying all registered listeners.
	 * 
	 * @param cell
	 * @param type
	 */
	private void setCellType(Cell cell, CellType type) {
		for (AgentListener listener : listeners) {
			listener.changeCellType(cell.getPosition(), type);
		}
		cell.setType(type);
	}

	/**
	 * Logs the deletion and deletes the agent as usual.
	 */
	@Override
	public void doDelete() {
		LOG.info("deleting agent");
		super.doDelete();
	};

}
