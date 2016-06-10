package de.hsb.ants;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.agent.DummyAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {

	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args){
		try {
			String host = "localhost";
			int port = -1;
			String plattform = null;
			boolean main = false;
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(host, port, plattform, main);
			AgentContainer container = runtime.createAgentContainer(profile);
			AgentController dummy = container.createNewAgent("DummyAgent01", DummyAgent.class.getName(), args);
			dummy.start();
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
