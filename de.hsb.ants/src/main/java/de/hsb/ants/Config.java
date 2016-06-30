package de.hsb.ants;

/**
 * The Config class represents configurations for a number of program
 * parameters.
 * 
 * @author Daniel
 *
 */
public class Config {

	private String service;
	private String host;
	private Integer port;
	private Integer numberOfAnts;
	private Color color;
	private Integer showGuiFor;

	// getters and setters ...
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getNumberOfAnts() {
		return numberOfAnts;
	}

	public void setNumberOfAnts(Integer numberOfAnts) {
		this.numberOfAnts = numberOfAnts;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Integer getShowGuiFor() {
		return showGuiFor;
	}

	public void setShowGuiFor(Integer showGuiFor) {
		this.showGuiFor = showGuiFor;
	}

}
