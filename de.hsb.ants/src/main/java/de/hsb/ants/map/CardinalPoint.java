package de.hsb.ants.map;

public class CardinalPoint<E> extends Wrapper<E> {

	private CardinalPoint<E> north = null;
	private CardinalPoint<E> east = null;
	private CardinalPoint<E> south = null;
	private CardinalPoint<E> west = null;
	
	public CardinalPoint(E element){
		super(element);
	}

	public CardinalPoint<E> getNorth() {
		return north;
	}

	public void setNorth(CardinalPoint<E> north) {
		this.north = north;
	}

	public CardinalPoint<E> getEast() {
		return east;
	}

	public void setEast(CardinalPoint<E> east) {
		this.east = east;
	}

	public CardinalPoint<E> getSouth() {
		return south;
	}

	public void setSouth(CardinalPoint<E> south) {
		this.south = south;
	}

	public CardinalPoint<E> getWest() {
		return west;
	}

	public void setWest(CardinalPoint<E> west) {
		this.west = west;
	}
	
}
