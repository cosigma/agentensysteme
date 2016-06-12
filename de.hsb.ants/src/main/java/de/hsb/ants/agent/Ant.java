package de.hsb.ants.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.Agent;

public class Ant extends Agent {

	private static final long serialVersionUID = 7861280006273301601L;
	
	static final Logger LOG = LoggerFactory.getLogger(Ant.class);
	
	@Override
	protected void setup() {
		LOG.info("Hello world, I am an ant.");
	}
	
	@Override
	protected void takeDown() {
		LOG.info("Goodbye world, I am an ant.");
	}
	
}
