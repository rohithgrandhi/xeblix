package com.btsd;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public final class ServerMessages {

	private static JSONObject pairMode;
	private static JSONObject connectToHostCancel;
	private static JSONObject hidStatus;
	private static JSONObject hidHosts;
	private static JSONObject pairModeCancel;
	private static JSONObject pincodeCancel;
	private static JSONObject versionRequest;

	public static JSONObject getConnectToHostCancel() {
		if(connectToHostCancel == null){
			connectToHostCancel = createHIDJSONObject("CONNECT_TO_HOST_CANCEL");
		}
		return connectToHostCancel;
	}

	public static JSONObject getPairMode() {
		if(pairMode == null){
			pairMode = createHIDJSONObject("PAIR_MODE");
		}
		return pairMode;
	}
	
	public static JSONObject getConnectToHost(String address){
		JSONObject toReturn = createHIDJSONObject("CONNECT_TO_HOST");
		try{
			toReturn.put(Main.HOST_ADDRESS, address);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	public static JSONObject getDisconnectFromHost(){
		return createHIDJSONObject("DISCONNECT_FROM_HOST");
	}
	
	public static JSONObject getKeyCode(Integer keyCode){
		JSONObject toReturn = createHIDJSONObject("KEYCODE");
		try{
			JSONArray keyCodesArray = new JSONArray();
			keyCodesArray.put(keyCode);
			toReturn.put(Main.KEY_CODES, keyCodesArray);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	public static JSONObject getKeycodes(List<Integer> keycodes){
		JSONObject toReturn = createHIDJSONObject("KEYCODE");
		try{
			JSONArray keyCodesArray = new JSONArray();
			for(Integer keyCode: keycodes){
				keyCodesArray.put(keyCode);
			}
			toReturn.put(Main.KEY_CODES, keyCodesArray);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	public static JSONObject getKeycodes(int[] keycodes){
		JSONObject toReturn = createHIDJSONObject("KEYCODE");
		try{
			JSONArray keyCodesArray = new JSONArray();
			for(Integer keyCode: keycodes){
				keyCodesArray.put(keyCode);
			}
			toReturn.put(Main.KEY_CODES, keyCodesArray);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	public static JSONObject getLIRCCommand(String remote, String command, 
		Integer count){
		
		JSONObject toReturn = createJSONObject("LIRCCommand");
		try{
			toReturn.put(Main.REMOTE, remote);
			toReturn.put(Main.KEY_CODES, command);
			toReturn.put(Main.SEND_COUNT, count.toString());
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
		
	}
	
	public static JSONObject getHidStatus() {
		if(hidStatus == null){
			hidStatus = createHIDJSONObject("HID_STATUS");
		}
		return hidStatus;
	}

	public static JSONObject getHidHosts() {
		if(hidHosts == null){
			hidHosts = createHIDJSONObject("HID_HOSTS");
		}
		return hidHosts;
	}

	public static JSONObject getPairModeCancel() {
		if(pairModeCancel == null){
			pairModeCancel = createHIDJSONObject("PAIR_MODE_CANCEL");
		}
		return pairModeCancel;
	}

	public static JSONObject getPincodeResponse(String pincodeResponse) {
		JSONObject toReturn = createHIDJSONObject("PINCODE_RESPONSE");
		try{
			toReturn.put(Main.PINCODE, pincodeResponse);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	public static JSONObject getPincodeCancel() {
		if(pincodeCancel == null){
			pincodeCancel = createHIDJSONObject("PINCODE_CANCEL");
		}
		return pincodeCancel;
	}

	public static JSONObject getVersionRequest() {
		if(versionRequest == null){
			versionRequest = createJSONObject("VersionRequest");
		}
		return versionRequest;
	}

	private static JSONObject createHIDJSONObject(String value){
		JSONObject toReturn = new JSONObject();
		try{
			toReturn.put(Main.TYPE, "HIDCommand");
			toReturn.put(Main.VALUE, value);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
	private static JSONObject createJSONObject(String type){
		JSONObject toReturn = new JSONObject();
		try{
			toReturn.put(Main.TYPE, type);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return toReturn;
	}
	
}
