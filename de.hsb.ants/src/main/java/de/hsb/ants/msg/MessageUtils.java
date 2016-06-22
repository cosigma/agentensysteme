package de.hsb.ants.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class MessageUtils {

	static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);
	
	private static final Gson GSON = new Gson();

	public static final String LOGIN, UP, DOWN, LEFT, RIGHT, COLLECT, DROP;
	
	static{
		//initialize json constants
		AntMessage msg = new AntMessage();
		msg.setColor("ANT_COLOR_RED");
		
		LOGIN = asJsonWithType(msg, "ANT_ACTION_LOGIN");
		UP = asJsonWithType(msg, "ANT_ACTION_UP");
		DOWN = asJsonWithType(msg, "ANT_ACTION_DOWN");
		LEFT = asJsonWithType(msg, "ANT_ACTION_LEFT");
		RIGHT = asJsonWithType(msg, "ANT_ACTION_RIGHT");
		COLLECT = asJsonWithType(msg, "ANT_ACTION_COLLECT");
		DROP = asJsonWithType(msg, "ANT_ACTION_DROP");
	}
	
	private static String asJsonWithType(AntMessage msg, String type){
		msg.setType(type);
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
