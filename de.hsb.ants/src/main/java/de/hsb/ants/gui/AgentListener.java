package de.hsb.ants.gui;

import de.hsb.ants.map.CellType;
import de.hsb.ants.map.Point;

/**
 * Interface to which an agent may report changes of it's internal state.
 * @author Daniel
 *
 */
public interface AgentListener {

	/**
	 * Notifies the listener that the cell at the given position has changed it's type.
	 * @param position
	 * @param type
	 */
	public void changeCellType(Point position, CellType type);
	
}
