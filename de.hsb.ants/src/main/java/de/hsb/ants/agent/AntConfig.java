package de.hsb.ants.agent;

import java.util.ArrayList;
import java.util.Collection;

import de.hsb.ants.Config;
import de.hsb.ants.gui.AgentListener;
import de.hsb.ants.msg.MessageUtil;

/**
 * The AntConfig class represents a number of configurations for an single Ant
 * agent. The same instance of this class should not be used with multiple
 * agents.
 * 
 * @author Daniel
 *
 */
public class AntConfig {

	private final MessageUtil messageUtil;
	private String service;
	private Collection<AgentListener> listener = new ArrayList<AgentListener>();

	/**
	 * Creates an AntConfig object which takes some parameters from the program config object.
	 * @param config
	 */
	public AntConfig(Config config) {
		this.messageUtil = new MessageUtil(config.getColor());
		this.service = config.getService();
	}

	// getters and setters...
	
	public MessageUtil getMessageUtil() {
		return messageUtil;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Collection<AgentListener> getListeners() {
		return listener;
	}

	public void addListener(AgentListener listener) {
		this.listener.add(listener);
	}

}
