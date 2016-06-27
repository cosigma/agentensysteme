package de.hsb.ants.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell {

	static final Logger LOG = LoggerFactory.getLogger(Cell.class);
	
	private final Point position;
	private CellType type = null;
	private int food = 0;
	private int smell = 0;
	private int stench = 0;

	public Cell(Point position) {
		this.position = position;
	}
	
	public Point getPosition(){
		return this.position;
	}

	public CellType getType() {
		if(type == null){
			LOG.warn("accessing null-type tile at {}", position);
		}
		return type;
	}

	public void setType(CellType type) {
		if(this.type != null && this.type != CellType.UNKNOWN){
			LOG.error("cell type at {} is {}: null or unknown expected", position, this.type);
			throw new IllegalStateException("present cell type is not null or unknown at position " + position);
		}
		this.type = type;
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

}
