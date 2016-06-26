package de.hsb.ants.agent;

import de.hsb.ants.Config;
import de.hsb.ants.msg.MessageUtils;

/**
 * The AntConfig class represents a number of configurations for an Ant agent.
 * An instance of this class needs to be passed as an argument on agent creation when creating an Ant.
 * The same instance may be used for multiple agents.
 * @author Daniel
 *
 */
public class AntConfig {

	private String service;
	
	private final MessageUtils messageUtils;
	
	public AntConfig(Config config){
		this.messageUtils = new MessageUtils(config.getColor());
		this.service = config.getService();
	}

	public MessageUtils getMessageUtils() {
		return messageUtils;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
}
