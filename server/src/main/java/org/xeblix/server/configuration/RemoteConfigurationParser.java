package org.xeblix.server.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.RemoteConfigurationContainer;

public final class RemoteConfigurationParser {

	private static final String REMOTE_TYPE = "remoteType";
	private static final String LIRC_NAME = "name";
	private static final String HID_ADDRESS = "address";
	private static final String REMOTE_LABEL = "label";
	private static final String LIRC_REPEAT_COUNT = "repeatCount";
	
	
	public static void parseRemoteConfiguration(JSONObject jsonObject){
		
		try{
			String remoteType = jsonObject.getString(REMOTE_TYPE);
			if("LIRC".equalsIgnoreCase(remoteType)){
				parseLIRCConfiguration(jsonObject);
			}else if("HID".equalsIgnoreCase(remoteType)){
				parseHIDConfiguration(jsonObject);
			}
		}catch(JSONException ex){
			throw new RuntimeException("Failed to parse RemoteConfiguration: " + jsonObject.toString() + 
				". Unable to find property: " + REMOTE_TYPE + ".",ex);
		}
	}
	
	public static void parseHIDConfiguration(JSONObject jsonObject){
		
		String remoteLabel = null;
		try{
			jsonObject.getString(HID_ADDRESS);
			remoteLabel = jsonObject.getString(REMOTE_LABEL);
		}catch(JSONException ex){
			throw new RuntimeException("A HID Configuration requires the following properties: " +
				HID_ADDRESS + "(String) and " + REMOTE_LABEL + "(String) " +
				". One or more of these properties is missing from the configuration: " + 
				jsonObject.toString(), ex);
		}
		
		RemoteConfigurationContainer.parseButtonConfiguration(jsonObject, remoteLabel);
		
	}
	
	public static void parseLIRCConfiguration(JSONObject jsonObject){
		
		String remoteLabel = null;
		try{
			jsonObject.getString(LIRC_NAME);
			remoteLabel = jsonObject.getString(REMOTE_LABEL);
			jsonObject.getInt(LIRC_REPEAT_COUNT);
		}catch(JSONException ex){
			throw new RuntimeException("A LIRC Configuration requires the following properties: " +
				LIRC_NAME + "(String) " + REMOTE_LABEL + "(String) " + " and " + LIRC_REPEAT_COUNT + "(int). " +
				"One or more of these properties is missing or the wrong type. ButtonConfiguration:  " + 
				jsonObject.toString(), ex);
		}
		
		RemoteConfigurationContainer.parseButtonConfiguration(jsonObject, remoteLabel);
	}
	
}
