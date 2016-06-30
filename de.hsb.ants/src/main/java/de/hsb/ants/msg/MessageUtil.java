package de.hsb.ants.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import de.hsb.ants.Action;
import de.hsb.ants.Color;

/**
 * Instances of this class serve as utility objects for message handling.
 * 
 * @author Daniel
 *
 */
public class MessageUtil {

	static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

	private static final Gson GSON = new Gson();

	public final String LOGIN, UP, DOWN, LEFT, RIGHT, COLLECT, DROP;

	/**
	 * Creates a message utils object which will use the given color for it's
	 * messages.
	 * 
	 * @param color
	 */
	public MessageUtil(Color color) {
		AntMessage msg = new AntMessage();
		msg.setColor(color);

		LOGIN = asJsonWithType(msg, Action.ANT_ACTION_LOGIN);
		UP = asJsonWithType(msg, Action.ANT_ACTION_UP);
		DOWN = asJsonWithType(msg, Action.ANT_ACTION_DOWN);
		LEFT = asJsonWithType(msg, Action.ANT_ACTION_LEFT);
		RIGHT = asJsonWithType(msg, Action.ANT_ACTION_RIGHT);
		COLLECT = asJsonWithType(msg, Action.ANT_ACTION_COLLECT);
		DROP = asJsonWithType(msg, Action.ANT_ACTION_DROP);
	}

	/**
	 * Creates a Json string according to the given AntMessage's color and the
	 * given action.
	 * 
	 * @param msg
	 * @param action
	 * @return
	 */
	private static String asJsonWithType(AntMessage msg, Action action) {
		msg.setType(action.toString());
		String json = GSON.toJson(msg, AntMessage.class);
		return json;
	}

	/**
	 * Parses a perception message from a Json string.
	 * 
	 * @param json
	 * @return
	 */
	public static PerceptionMessage getPerception(String json) {
		try {
			PerceptionMessage msg = GSON.fromJson(json, PerceptionMessage.class);
			return msg;
		} catch (JsonParseException e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
}
