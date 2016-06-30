package de.hsb.ants.msg;

import de.hsb.ants.Color;

/**
 * Instances of this class represent Json messages which may be sent as the
 * content of an ACLMessage to the antworld service.
 * 
 * @author Daniel
 *
 */
public class AntMessage {

	private String type;
	private Color color;

	// getters and setters ...

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
