package de.hsb.ants.map;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMap<E> {

	static final Logger LOG = LoggerFactory.getLogger(GameMap.class);

	private final Map<Point, CardinalPoint<E>> map;

	private CardinalPoint<E> root = null;

	public GameMap() {
		this(4096);
	}

	public GameMap(int initialSize) {
		this.map = new HashMap<Point, CardinalPoint<E>>(initialSize);
	}
	
	public void setRoot(E element, Point point){
		if(root != null){
			LOG.error("root is already set");
			throw new IllegalStateException("root is already set");
		}
		root = new CardinalPoint<E>(element, point);
		map.put(point, root);
	}
	
	public E getRoot(){
		return root.element;
	}

	/**
	 * Returns the map element at a given position.
	 * @param x
	 * @param y
	 * @return
	 */
	public E getElement(int x, int y) {
		Point point = new Point(x, y);
		CardinalPoint<E> cardinalPoint = map.get(point);
		if (cardinalPoint == null) {
			return null;
		}
		E element = cardinalPoint.element;
		return element;
	}

	/**
	 * Sets the map element at the given coordinates.
	 * @param x
	 * @param y
	 * @param element
	 */
	public void setElement(int x, int y, E element) {
		Point point = new Point(x, y);
		CardinalPoint<E> cardinalPoint = map.get(point);
		if (cardinalPoint != null) {
			LOG.error("there already is an element at point {}", point);
			throw new IllegalArgumentException("there already is an element at point " + point);
		}

		cardinalPoint = new CardinalPoint<E>(element, point);
		linkPointsAround(cardinalPoint);
		map.put(point, cardinalPoint);

	}

	/**
	 * Builds up the linked-list structure around the given point.
	 * @param cardinalPoint
	 */
	private void linkPointsAround(CardinalPoint<E> cardinalPoint) {
		Point point = cardinalPoint.point;

		Point north = new Point(point.x, point.y + 1);
		CardinalPoint<E> cardinalNorth = map.get(north);
		if (cardinalNorth != null) {
			cardinalPoint.setNorth(cardinalNorth);
			cardinalNorth.setSouth(cardinalPoint);
		}

		Point east = new Point(point.x + 1, point.y);
		CardinalPoint<E> cardinalEast = map.get(east);
		if (cardinalEast != null) {
			cardinalPoint.setEast(cardinalEast);
			cardinalEast.setWest(cardinalPoint);
		}

		Point south = new Point(point.x, point.y - 1);
		CardinalPoint<E> cardinalSouth = map.get(south);
		if (cardinalSouth != null) {
			cardinalPoint.setSouth(cardinalSouth);
			cardinalSouth.setNorth(cardinalPoint);
		}

		Point west = new Point(point.x - 1, point.y);
		CardinalPoint<E> cardinalWest = map.get(west);
		if (cardinalWest != null) {
			cardinalPoint.setWest(cardinalWest);
			cardinalWest.setEast(cardinalPoint);
		}
	}

}
