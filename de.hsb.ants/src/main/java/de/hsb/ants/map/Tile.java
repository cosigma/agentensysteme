package de.hsb.ants.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tile {

	static final Logger LOG = LoggerFactory.getLogger(Tile.class);
	
	private TileType type = null;

	private int food = 0;
	private int smell = 0;
	private int stench = 0;
	
	private final Point position;

	public Tile(Point position) {
		this.position = position;
	}

	public TileType getType() {
		if(type == null){
			LOG.warn("accessing null-type tile at {}", position);
		}
		return type;
	}

	public void setType(TileType type) {
		if(type != null && type != TileType.UNKNOWN){
			LOG.error("present tile type is not null or unknown at position", position);
			throw new IllegalStateException("present tile type is not null or unknown at position " + position);
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
