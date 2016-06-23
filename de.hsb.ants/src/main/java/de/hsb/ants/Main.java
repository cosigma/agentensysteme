package de.hsb.ants;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.agent.Ant;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {

	static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			String host = "192.168.1.233";
			int port = -1;
			String platform = null;
			boolean isMain = false;
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(host, port, platform, isMain);

			AgentContainer container = runtime.createAgentContainer(profile);

			int numberOfAnts = 1;
			for (int i = 0; i < numberOfAnts; ++i) {
				String antName = "carribean" + i;
				AgentController ant = container.createNewAgent(antName, Ant.class.getName(), args);
				ant.start();
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
