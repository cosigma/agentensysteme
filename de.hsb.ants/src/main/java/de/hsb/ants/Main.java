package de.hsb.ants;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.UUID;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.agent.Ant;
import de.hsb.ants.agent.AntConfig;
import de.hsb.ants.gui.ConfigPanel;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {

	static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			ConfigPanel configPanel = new ConfigPanel();
			int configResult = JOptionPane.showConfirmDialog(null, configPanel, "Agent configuration panel",
					JOptionPane.OK_CANCEL_OPTION);
			if (configResult != JOptionPane.OK_OPTION) {
				LOG.info("canceled config, shutting down");
				return;
			}
			Config config = configPanel.getConfig();

			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(config.getHost(), config.getPort(), null, false);
			AgentContainer container = runtime.createAgentContainer(profile);

			AntConfig antConfig = new AntConfig(config);

			int numberOfAnts = config.getNumberOfAnts();
			for (int i = 0; i < numberOfAnts; ++i) {
				String antName = "carribean_" + UUID.randomUUID();
				AgentController ant = container.createNewAgent(antName, Ant.class.getName(),
						new Object[] { antConfig });
				ant.start();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
