package de.hsb.ants.map;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMap<E> {

	static final Logger LOG = LoggerFactory.getLogger(GameMap.class);

	private final Map<Point, E> map;

	public GameMap() {
		this(4096);
	}

	public GameMap(int initialSize) {
		this.map = new HashMap<Point, E>(initialSize);
	}

	/**
	 * Returns the map element at the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public E getElement(int x, int y) {
		Point position = new Point(x, y);
		E tile = map.get(position);
		return tile;
	}

	/**
	 * Sets the map element at the given position.
	 * 
	 * @param x
	 * @param y
	 * @param element
	 */
	public void setElement(int x, int y, E element) {
		Point position = new Point(x, y);
		E elementPresent = map.get(position);
		if (elementPresent != null) {
			LOG.error("there already is an element at point {}", position);
			throw new IllegalArgumentException("there already is an element at position " + position);
		}
		map.put(position, element);
	}
	
}
