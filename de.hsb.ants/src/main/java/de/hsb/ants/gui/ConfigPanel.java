package de.hsb.ants.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.hsb.ants.Color;
import de.hsb.ants.Config;

/**
 * A swing panel that grants the user access to some configuration.
 * @author Daniel
 *
 */
public class ConfigPanel extends JPanel {

	private static final long serialVersionUID = -6099522900609512148L;
	
	private final JTextField service = new JTextField(5);
	private final JTextField host = new JTextField(5);
	private final JTextField port = new JTextField(5);
	private final JTextField antNumber = new JTextField(5);
	private final JComboBox<Color> color = new JComboBox<Color>(Color.values());
	
	public ConfigPanel(){
		//set defaults
		service.setText("antWorld2016");
		host.setText("127.0.0.1");
		port.setText("1099");
		antNumber.setText("3");
		color.setSelectedItem(Color.ANT_COLOR_RED);
		
		//add components
		setLayout(new GridLayout(5, 2));
		addWithLabel(service, "service");
		addWithLabel(host, "host");
		addWithLabel(port, "port");
		addWithLabel(antNumber, "ants");
		addWithLabel(color, "color");
	}
	
	/**
	 * Adds a component with a label
	 * @param comp
	 * @param label
	 */
	private void addWithLabel(Component comp, String label){
		add(new JLabel(label));
		add(comp);
	}
	
	/**
	 * Returns the values set within this panel as a Config object
	 * @return
	 */
	public Config getConfig(){
		Config config = new Config();
		config.setHost(host.getText());
		config.setPort(Integer.valueOf(port.getText()));
		config.setNumberOfAnts(Integer.valueOf(antNumber.getText()));
		config.setColor((Color) color.getSelectedItem());
		return config;
	}
	
}
