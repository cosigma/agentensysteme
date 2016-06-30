package de.hsb.ants;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.agent.Ant;
import de.hsb.ants.agent.AntConfig;
import de.hsb.ants.gui.AntPanel;
import de.hsb.ants.gui.ConfigPanel;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {

	static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			// get config
			Config configDefaults = new Config();
			configDefaults.setService("antWorld2016");
			configDefaults.setHost("127.0.0.1");
			configDefaults.setPort(1099);
			configDefaults.setNumberOfAnts(3);
			configDefaults.setColor(Color.ANT_COLOR_RED);
			ConfigPanel configPanel = new ConfigPanel(configDefaults);
			int configResult = JOptionPane.showConfirmDialog(null, configPanel, "Agent configuration panel",
					JOptionPane.OK_CANCEL_OPTION);
			if (configResult != JOptionPane.OK_OPTION) {
				LOG.info("canceled config, shutting down");
				return;
			}
			Config config = configPanel.getConfig();

			// start container
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(config.getHost(), config.getPort(), null, false);
			AgentContainer container = runtime.createAgentContainer(profile);

			// initialize agent gui
			JFrame frame = new JFrame("ants");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					try {
						container.kill();
					} catch (StaleProxyException e1) {
						LOG.error("could not kill container: {}", e1.getMessage());
					}
					super.windowClosing(e);
				}
			});
			JPanel mainPanel = new JPanel();

			// create ants
			int numberOfAnts = config.getNumberOfAnts();
			AgentController[] ants = new AgentController[numberOfAnts];
			for (int i = 0; i < numberOfAnts; ++i) {

				String antName = "carribean_" + UUID.randomUUID();

				AntPanel panel = new AntPanel(antName);
				mainPanel.add(panel);

				AntConfig antConfig = new AntConfig(config);
				antConfig.addListener(panel);
				ants[i] = container.createNewAgent(antName, Ant.class.getName(), new Object[] { antConfig });
			}

			// make agent gui visible
			frame.add(new JScrollPane(mainPanel));
			frame.pack();
			frame.setVisible(true);

			// start ants
			for (AgentController ant : ants) {
				ant.start();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
