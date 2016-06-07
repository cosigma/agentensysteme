package de.hsb.ants.map;

public class Point{
	
	public final int x;
	public final int y;
	
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return x ^ y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)){
			return false;
		}
		Point other = (Point) obj;
		return this.x == other.x && this.y == other.y;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
