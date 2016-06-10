package de.hsb.ants.msg;

import javax.json.Json;
import javax.json.JsonObject;

public class MessageUtils {

	public static String getLoginMsg(){
		JsonObject jsonLogin = getLoginJson("ANT_COLOR_RED");
		String loginStr = jsonLogin.toString();
		return loginStr;
	}
	
	private static JsonObject getLoginJson(String color){
		JsonObject json = Json.createObjectBuilder()
			.add("type", "ANT_ACTION_LOGIN")
			.add("color", color)
		.build();
		return json;
	}
	
}
