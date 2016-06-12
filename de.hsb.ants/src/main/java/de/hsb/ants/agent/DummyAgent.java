package de.hsb.ants.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.msg.MessageUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class DummyAgent extends Agent {
	
	private static final long serialVersionUID = -5620493012637529572L;
	
	static final Logger LOG = LoggerFactory.getLogger(DummyAgent.class);
	
	static final String antWorldStr = "antWorld2016";
	
	@Override
	protected void setup() {
		//login
		try {
			AID antWorld = findServiceAID(antWorldStr, antWorldStr);
			if(antWorld == null){
				LOG.error("antWorld2016 service not found, deleting agent {}", getLocalName());
				doDelete();
				return;
			}
			sendLogin(antWorld);
			LOG.info("login message sent: {}", getLocalName());
		} catch (FIPAException e) {
			LOG.error("login failed: {}", e.getMessage());
			e.printStackTrace();
		}
		
		addBehaviour(new SimpleBehaviour(this) {
			@Override
			public void action() {
				// receive message in a blocking way and print
				ACLMessage msg = blockingReceive(5000);
				if(msg == null){
					return;
				}
			}
			@Override
			public boolean done() {
				return false;
			}
		});
	}
	
	private AID findServiceAID(String name, String type) throws FIPAException {
		ServiceDescription filter = new ServiceDescription();
		filter.setName(name);
		filter.setType(type);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(filter);
		
		DFAgentDescription[] results = DFService.search(this, dfd);
		if(results == null || results.length == 0){
			return null;
		}
		if(results.length > 1){
			LOG.warn("more than once instance of service {}:{} found, defaulting to first occurrence", name, type);
		}
		return results[0].getName();
	}
	
	private void sendLogin(AID receiver){
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.setLanguage("json");
		msg.setContent(MessageUtils.getLoginMsg());
		msg.addReceiver(receiver);
		send(msg);
	}
}
