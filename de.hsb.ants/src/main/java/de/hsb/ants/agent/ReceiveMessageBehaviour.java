package de.hsb.ants.agent;

import java.util.function.Consumer;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveMessageBehaviour extends CyclicBehaviour {
	
	private static final long serialVersionUID = 2371439474639562410L;
	
	private final MessageTemplate mt;
	private final Consumer<ACLMessage> onReceive;

	public ReceiveMessageBehaviour(MessageTemplate mt, Consumer<ACLMessage> onReceive) {
		this.mt = mt;
		this.onReceive = onReceive;
	}
	
	@Override
	public final void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg == null) {
			block();
			return;
		}
		onReceive.accept(msg);
	}

}
