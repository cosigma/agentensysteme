package de.hsb.ants.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import de.hsb.ants.Action;
import de.hsb.ants.Color;

public class MessageUtils {

	static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);
	
	private static final Gson GSON = new Gson();

	public final String LOGIN, UP, DOWN, LEFT, RIGHT, COLLECT, DROP;
	
	public MessageUtils(Color color){
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
	
	private static String asJsonWithType(AntMessage msg, Action action){
		msg.setType(action.toString());
		String json = GSON.toJson(msg, AntMessage.class);
		return json;
	}
	
	public static PerceptionMessage getPerception(String json){
		LOG.debug("trying to build perception message object from json string: {}", json);
		try{
			PerceptionMessage msg = GSON.fromJson(json, PerceptionMessage.class);
			return msg;
		}catch(JsonParseException e){
			LOG.error(e.getMessage());
			return null;
		}
	}
}
