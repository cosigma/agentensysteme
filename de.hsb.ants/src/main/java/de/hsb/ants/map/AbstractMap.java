package de.hsb.ants.map;

import java.util.HashMap;
import java.util.Map;

public class AbstractMap<E> {

	private final Map<Point, E> map;
	
	public AbstractMap(){
		this.map = new HashMap<Point, E>(4096);
	}
	
	public E get(int x, int y){
		Point point = new Point(x, y);
		E element = map.get(point);
		return element;
	}
	
	public E set(int x, int y, E element){
		Point point = new Point(x, y);
		E res = map.get(point);
		if(res == null){
			res = element;
			map.put(point, element);
		}
		return res;
	}
	
}
