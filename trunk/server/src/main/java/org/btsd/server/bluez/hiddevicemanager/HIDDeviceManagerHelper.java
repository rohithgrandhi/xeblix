package org.btsd.server.bluez.hiddevicemanager;

import java.util.ArrayList;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.Message;
import org.btsd.server.messages.ShutdownMessage;
import org.btsd.server.util.ActiveThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class HIDDeviceManagerHelper {

	public static void ignoringMessage(Message message, HIDDeviceManagerState state){
		System.out.println("Ignoring client request " + message.getType().getDescription() +  
			". Currently in state: " + state.getClass().getSimpleName());
	}
	
	public static void ignoringMessage(String message, HIDDeviceManagerState state){
		System.out.println("Ignoring client request " + message +  
			". Currently in state: " + state.getClass().getSimpleName());
	}
	
	public static void disconnectFromHost(HIDDeviceManager deviceManager){
		
		if(deviceManager.getBtHIDWriterActiveObject() != null){
			deviceManager.getBtHIDWriterActiveObject().addMessage(new ShutdownMessage());
			//if still alive probably connected to a host, force kill it 
			if(deviceManager.getBtHIDWriterActiveObject().isAlive()){
				deviceManager.getBtHIDWriterActiveObject().interrupt();
			}
			deviceManager.setBtHIDWriterActiveObject(null);
		}
		
		if(deviceManager.getControlHIDSocketActiveObject() != null){
			deviceManager.getControlHIDSocketActiveObject().addMessage(new ShutdownMessage());
			try{Thread.sleep(15);}catch(InterruptedException ex){}
			
			//if still alive probably connected to a host, force kill it 
			if(deviceManager.getControlHIDSocketActiveObject().isAlive()){
				deviceManager.getControlHIDSocketActiveObject().interrupt();
			}
			deviceManager.setControlHIDSocketActiveObject(null);
		}
		
		if(deviceManager.getInputHIDSocketActiveObject() != null){
			deviceManager.getInputHIDSocketActiveObject().addMessage(new ShutdownMessage());
			
			try{Thread.sleep(15);}catch(InterruptedException ex){}
			
			//if still alive probably connected to a host, force kill it
			if(deviceManager.getInputHIDSocketActiveObject().isAlive()){
				deviceManager.getInputHIDSocketActiveObject().interrupt();
			}
			deviceManager.setInputHIDSocketActiveObject(null);
		}
		
	}
	
	public static void sendHIDHosts(ArrayList<HIDHostInfo> hidHosts, ActiveThread ao, 
			String remoteDeviceAddress){
		
		JSONArray hidHostsArray = new JSONArray();
		for(HIDHostInfo hostInfo: hidHosts){
			JSONObject hidHost = new JSONObject();
			try{
				hidHost.put("address", hostInfo.getAddress());
				hidHost.put("name", hostInfo.getName());
				hidHost.put("primary", hostInfo.isPrimary());
			}catch(JSONException ex){
				//will never get here
				throw new IllegalStateException(ex.getMessage(), ex);
			}
			hidHostsArray.put(hidHost);
		}
		
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, "HIDHosts");
			response.put(FromClientResponseMessage.VALUE, hidHostsArray);
		}catch(JSONException ex){
			
		}
		
		ao.addMessage(new FromClientResponseMessage(
			remoteDeviceAddress, response));
		
	}
	
	public static JSONObject getStatus(String status){
		
		JSONObject result = new JSONObject();
		try{
			result.put(FromClientResponseMessage.TYPE, "status");
			result.put(FromClientResponseMessage.STATUS, status);
		}catch(JSONException ex){
			//will never get here
			throw new IllegalStateException(ex.getMessage(), ex);
		}
		
		return result;
	}
	
	public static JSONObject getFailedResponse(String status) {
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, "result");
			response.put(FromClientResponseMessage.VALUE, "FAILED");
			response.put(FromClientResponseMessage.STATUS, status);
			
		}catch(JSONException ex){}
		return response;
	}
	
	public static JSONObject getSuccessResponse(){
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, "result");
			response.put(FromClientResponseMessage.VALUE, "SUCCESS");
		}catch(JSONException ex){}
		return response;
	}
	
	public static JSONObject getVersionRequest(){
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, "VersionRequest");
			response.put(FromClientResponseMessage.VALUE, "1.0");
		}catch(JSONException ex){}
		return response;
	}
	
	public static JSONObject getUnrecognizedCommand(){
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, "UnrecognizedCommand");
		}catch(JSONException ex){}
		return response;
	}
	
	public static JSONObject getResponse(String type){
		JSONObject response = new JSONObject();
		try{
			response.put(FromClientResponseMessage.TYPE, type);
		}catch(JSONException ex){}
		return response;
	}
	
}
