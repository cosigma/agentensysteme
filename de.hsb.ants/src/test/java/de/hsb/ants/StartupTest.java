package de.hsb.ants;

import de.hsb.ants.map.Ant;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class StartupTest {
	
	

	public static void main(String[] args){
		try{
			String host = "localhost";
			int port = -1;
			String platform = null;
			boolean main = false;
			
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(host, port, platform, main);
			AgentContainer container = runtime.createAgentContainer(profile);
			
			AgentController agent = container.createNewAgent("caribbean", Ant.class.getName(), args);
			agent.start();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
