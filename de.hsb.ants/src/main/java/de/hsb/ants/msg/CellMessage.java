package de.hsb.ants.msg;

import de.hsb.ants.map.CellType;

/**
 * Instances of this class represent the "cell" element within the perception messages sent by the game service.
 * @author Daniel
 *
 */
public class CellMessage {

	private int row;
	private int col;
	private CellType type;
	private int food;
	private int smell;
	private int stench;
	private String[] ants;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public CellType getType() {
		return type;
	}
	public void setType(CellType type) {
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
	public String[] getAnts() {
		return ants;
	}
	public void setAnts(String[] ants) {
		this.ants = ants;
	}
	
}
