package de.hsb.ants.msg;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

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
	
	private static void fun(){
		
	}
	
}