package de.hsb.ants.map;

public abstract class Wrapper<E> {
	
	protected E element;
	
	public Wrapper(E element){
		this.element = element;
	}
	
	public E getElement(){
		return element;
	}
	
	public void setElement(E element){
		this.element = element;
	}
	
}
