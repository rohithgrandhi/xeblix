package org.xeblix.server.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.RemoteConfigurationContainer;

public final class RemoteConfigurationParser {

	public static final String REMOTE_TYPE = "remoteType";
	public static final String LIRC_NAME = "name";
	public static final String HID_ADDRESS = "address";
	public static final String REMOTE_LABEL = "label";
	public static final String LIRC_REPEAT_COUNT = "repeatCount";
	
	public static final String TYPE_LIRC = "LIRC";
	public static final String TYPE_HID = "HID";
	
	public static void parseRemoteConfiguration(JSONObject jsonObject){
		
		try{
			String remoteType = jsonObject.getString(REMOTE_TYPE);
			if(TYPE_LIRC.equalsIgnoreCase(remoteType)){
				parseLIRCConfiguration(jsonObject);
			}else if(TYPE_HID.equalsIgnoreCase(remoteType)){
				parseHIDConfiguration(jsonObject);
			}
		}catch(JSONException ex){
			throw new RuntimeException("Failed to parse RemoteConfiguration: " + jsonObject.toString() + 
				". Unable to find property: " + REMOTE_TYPE + ".",ex);
		}
	}
	
	public static void parseHIDConfiguration(JSONObject jsonObject){
		
		RemoteConfigurationContainer.parseButtonConfiguration(jsonObject, "HIDTemplate");
		
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
