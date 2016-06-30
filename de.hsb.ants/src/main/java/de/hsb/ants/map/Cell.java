package de.hsb.ants.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a basic cell in the game AntWorld. It is equivalent to
 * the "cell" part of the perception messages sent by the game service.
 * 
 * @author Daniel
 *
 */
public class Cell {

	static final Logger LOG = LoggerFactory.getLogger(Cell.class);

	protected final Point position;
	protected CellType type;
	protected int food = 0;
	protected int smell = 0;
	protected int stench = 0;

	/**
	 * Creates a cell object which uses the given position
	 * 
	 * @param position
	 */
	public Cell(Point position, CellType type) {
		this.position = position;
		this.type = type;
	}

	public Point getPosition() {
		return this.position;
	}

	public CellType getType() {
		if (type == null) {
			LOG.warn("accessing null-type tile at {}", position);
		}
		return type;
	}

	/**
	 * Sets the type of the cell. Checks if the type change is legal, throws an
	 * IllegalArgumentException otherwise
	 * 
	 * @param type
	 */
	public void setType(CellType type) {
		switch (this.type) {
		case SAFE_UNKNOWN:
			if (type == CellType.UNSAFE_UNKNOWN) {
				LOG.error("invalid cell type change at {} from {} to {}", this.position, this.type, type);
				throw new IllegalArgumentException(
						"invalid cell type change at " + position + " from " + this.type + " to " + type);
			}
			this.type = type;
			break;
		case UNSAFE_UNKNOWN:
			this.type = type;
			break;
		default:
			LOG.error("invalid cell type change at {} from {} to {}", this.position, this.type, type);
			throw new IllegalArgumentException(
					"invalid cell type change at " + position + " from " + this.type + " to " + type);
		}
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public int getSmell() {
		return smell;
	}

	public void setSmell(int smell) {
		this.smell = smell;
	}

	public int getStench() {
		return stench;
	}

	public void setStench(int stench) {
		this.stench = stench;
	}

	public boolean hasFood() {
		return food > 0;
	}

	public boolean isStart() {
		return type == CellType.START;
	}

	public boolean isAccessible() {
		return !isTypeIn(CellType.BLOCKED, CellType.PIT);
	}

	public boolean isSafeAccessible() {
		return isTypeIn(CellType.START, CellType.FREE, CellType.SAFE_UNKNOWN);
	}

	public boolean isSafeUnknown() {
		return type == CellType.SAFE_UNKNOWN;
	}

	public boolean isUnsafeUnknown() {
		return type == CellType.UNSAFE_UNKNOWN;
	}

	/**
	 * Checks if the cell's type is in the given list of types.
	 * @param types
	 * @return true if it is in the list, false otherwise
	 */
	public boolean isTypeIn(CellType... types) {
		for (CellType type : types) {
			if (this.type == type) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return position.hashCode();
	}
}
