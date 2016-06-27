package de.hsb.ants.map;

public class Point {

	public final int x;
	public final int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		return x ^ y;
	}

	public boolean equals(Point other){
		if(other == null){
			return false;
		}
		return this.x == other.x && this.y == other.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		return equals((Point) obj);
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
