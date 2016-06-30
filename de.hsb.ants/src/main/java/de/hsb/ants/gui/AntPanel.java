package de.hsb.ants.gui;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.hsb.ants.map.CellType;
import de.hsb.ants.map.Point;

/**
 * A class that supplies a swing component which represents an agents state. The
 * swing component is updated by calling the methods of the listener interface
 * implemented by this class.
 * 
 * @author Daniel
 *
 */
public class AntPanel extends JPanel implements AgentListener {

	private static final long serialVersionUID = 8017310195589078858L;

	private final JTextArea textArea;

	private final Map<Integer, String> textMap = new HashMap<Integer, String>(60);
	private final StringBuilder sb = new StringBuilder(2048);

	private int xMin;
	private int xMax;
	private int yMin;
	private int yMax;

	/**
	 * Creates a new AntPanel
	 */
	public AntPanel(String label) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		textArea = new JTextArea(20, 40);
		textArea.setLineWrap(false);
		textArea.setEditable(false);
		textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		textArea.setVisible(true);
		this.add(new JLabel(label));
		this.add(textArea);
	}

	/**
	 * Inserts or replaces a character for the given cell type at the given
	 * position. May also update the boundaries of the map if necessary.
	 */
	@Override
	public void changeCellType(Point pos, CellType type) {
		// update the string representation immediately
		if (type == CellType.START) {
			xMin = pos.x;
			xMax = pos.x;
			yMin = pos.y;
			yMax = pos.y;
			textMap.put(pos.y, "" + toChar(type));
		} else {
			updateXMin(pos.x);
			updateXMax(pos.x);
			updateYMin(pos.y);
			updateYMax(pos.y);
			String line = textMap.get(pos.y);
			line = line.substring(0, pos.x - xMin) + toChar(type) + line.substring(1 + pos.x - xMin);
			textMap.put(pos.y, line);
		}

		// update the swing component later
		SwingUtilities.invokeLater(() -> {
			update();
		});
	}

	/**
	 * Updates the swing components within this panel.
	 */
	private synchronized void update() {
		textArea.setColumns(Math.max(textArea.getColumns(), 1 + yMax - yMin));
		textArea.setRows(Math.max(textArea.getRows(), 1 + xMax - xMin));
		textArea.setText(null);
		String text = buildText();
		textArea.setText(text);
	}

	/**
	 * Builds a string using this object's textMap to represent the map as seen
	 * by the agent.
	 * 
	 * @return
	 */
	private String buildText() {
		sb.setLength(0);
		for (int i = yMin; i <= yMax; ++i) {
			String line = textMap.get(i);
			sb.append(line).append(System.lineSeparator());
		}
		sb.setLength(sb.length() - System.lineSeparator().length());
		return sb.toString();
	}

	/**
	 * Updates the left-hand boundary of the map, adding empty columns if
	 * necessary.
	 * 
	 * @param x
	 */
	private void updateXMin(int x) {
		if (x < xMin) {
			String prefix = "";
			for (int i = xMin; i > x; --i) {
				prefix += ' ';
			}
			for (Map.Entry<Integer, String> line : textMap.entrySet()) {
				line.setValue(prefix + line.getValue());
			}
			xMin = x;
		}
	}

	/**
	 * Updates the right-hand boundary of the map, adding empty columns if
	 * necessary.
	 * 
	 * @param x
	 */
	private void updateXMax(int x) {
		if (x > xMax) {
			String suffix = "";
			for (int i = xMax; i < x; ++i) {
				suffix += ' ';
			}
			for (Map.Entry<Integer, String> line : textMap.entrySet()) {
				line.setValue(line.getValue() + suffix);
			}
			xMax = x;
		}
	}

	/**
	 * Updates the top-side boundary of the map, adding empty rows if necessary.
	 * 
	 * @param y
	 */
	private void updateYMin(int y) {
		if (y < yMin) {
			String line = "";
			for (int i = xMin; i <= xMax; ++i) {
				line += ' ';
			}
			for (int i = yMin - 1; i >= y; --i) {
				textMap.put(i, line);
			}
			yMin = y;
		}
	}

	/**
	 * Updates the bottom-side boundary of the map, adding empty rows if
	 * necessary.
	 * 
	 * @param y
	 */
	private void updateYMax(int y) {
		if (y > yMax) {
			String line = "";
			for (int i = xMin; i <= xMax; ++i) {
				line += ' ';
			}
			for (int i = yMax + 1; i <= y; ++i) {
				textMap.put(i, line);
			}
			yMax = y;
		}
	}

	/**
	 * Converts a cell type into a single character.
	 * 
	 * @param type
	 * @return
	 */
	public static final char toChar(CellType type) {
		switch (type) {
		case START:
			return 'x';
		case BLOCKED:
			return '#';
		case PIT:
			return 'o';
		case FREE:
			return '.';
		case SAFE_UNKNOWN:
			return '-';
		case UNSAFE_UNKNOWN:
			return '~';
		default:
			return ' ';
		}
	}

}
