package de.hsb.ants.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.msg.MessageUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class DummyAgent extends Agent {
	
	private static final long serialVersionUID = -5620493012637529572L;
	
	static final Logger LOG = LoggerFactory.getLogger(DummyAgent.class);
	
	@Override
	protected void setup() {
		LOG.info("setting begin");
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.setLanguage("json");
		msg.setContent(MessageUtils.getLoginMsg());
		msg.addReceiver(new AID("antWorld2f10cbb6-537b-4f0f-bfb5-67fda17dde8e", AID.ISLOCALNAME));
		
		send(msg);
		
		LOG.info("setup complete");
		
		addBehaviour(new SimpleBehaviour(this) {
			@Override
			public void action() {
				// receive message in a blocking way and print
				ACLMessage msg = blockingReceive(5000);
				if(msg == null){
					return;
				}
				System.out.println("message from " + msg.getSender().getLocalName());
				System.out.println("with content " + msg.getContent());
				System.out.println("the complete message is \n" + msg + "\n");
			}
			@Override
			public boolean done() {
				return false;
			}
		});
	}
}
