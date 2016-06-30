package de.hsb.ants.map;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.msg.MessageUtil;

/**
 * A class which handles depth-limited breadth-first searches.
 * 
 * @author Daniel
 *
 */
public class BFSRunner {

	static final Logger LOG = LoggerFactory.getLogger(BFSRunner.class);

	private final Map<Point, Cell> map;
	private final MessageUtil msgUtil;

	private final Map<Point, Point> discoveredFromPos = new HashMap<Point, Point>(512);
	private final Map<Point, String> discoveredWithAction = new HashMap<Point, String>(512);
	private final Set<Point> visited = new HashSet<Point>(512);

	private int depth = 0;
	private Queue<Cell> thisDepth = new ArrayDeque<Cell>(96);
	private Queue<Cell> nextDepth = new ArrayDeque<Cell>(96);

	private final Predicate<Cell> isAccessible;
	private final Predicate<Cell> isGoal;
	private final Deque<String> solution;

	/**
	 * Creates a new BFSRunner.
	 * 
	 * @param map
	 *            The map on which the search is performed.
	 * @param msgUtil
	 *            a MessageUtil object, from which the Json-string constants
	 *            will be taken
	 * @param start
	 *            the start node
	 * @param isAccessible
	 *            a predicate which defines which adjacent nodes should be
	 *            visited
	 * @param isGoal
	 *            a predicate which defines the goal condition
	 * @param solution
	 *            the output deque into which the path to a cell matching the
	 *            goal condition will be pushed
	 */
	public BFSRunner(Map<Point, Cell> map, MessageUtil msgUtil, Cell start, Predicate<Cell> isAccessible,
			Predicate<Cell> isGoal, Deque<String> solution) {
		this.map = map;
		this.msgUtil = msgUtil;
		this.isAccessible = isAccessible;
		this.isGoal = isGoal;
		this.solution = solution;
		thisDepth.add(start);
	}

	/**
	 * Performs a standard, max-depth breadth-first search.
	 * 
	 * @return
	 */
	public Cell search() {
		return search(Integer.MAX_VALUE);
	}

	/**
	 * Performs a breadth-first search up to the given depth. The of the object
	 * will be saved between calls, i.e. it is is possible to call
	 * bfsr.search(10) and then bfsr.search(11) without having to calculate up
	 * to depth 10 again
	 * 
	 * @param maxDepth
	 * @return
	 */
	public Cell search(int maxDepth) {

		while (depth <= maxDepth && !thisDepth.isEmpty()) {
			Queue<Cell> fringe = thisDepth;
			while (!fringe.isEmpty()) {
				Cell cell = fringe.poll();

				// check if a valid target has been found
				if (isGoal.test(cell)) {
					// find trace to start
					// many thanks to
					// http://stackoverflow.com/questions/9590299/
					Point pos = cell.getPosition();
					String action = discoveredWithAction.get(pos);
					LOG.debug("found a solution at {}", pos);
					while (action != null) {
						solution.push(action);
						pos = discoveredFromPos.get(pos);
						LOG.debug("adding action to solution : {}", action);
						action = discoveredWithAction.get(pos);
					}
					return cell;
				} else {
					Point pos = cell.getPosition();
					visited.add(pos);

					// add applicable adjacent points
					BiConsumer<Point, String> next = (adjPos, action) -> {
						if (!visited.contains(adjPos)) {
							Cell adjacent = map.get(adjPos);
							if (adjacent != null && isAccessible.test(adjacent)) {
								discoveredWithAction.put(adjPos, action);
								discoveredFromPos.put(adjPos, pos);
								fringe.add(adjacent);
							}
						}
					};
					next.accept(pos.up(), msgUtil.UP);
					next.accept(pos.down(), msgUtil.DOWN);
					next.accept(pos.left(), msgUtil.LEFT);
					next.accept(pos.right(), msgUtil.RIGHT);
				}
			}
			Queue<Cell> temp = thisDepth;
			thisDepth = nextDepth;
			nextDepth = temp;
			++depth;
		}

		return null;
	}
}
