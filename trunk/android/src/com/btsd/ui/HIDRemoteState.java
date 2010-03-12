package com.btsd.ui;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.ui.hidremote.HIDRemoteConfiguration;

public interface HIDRemoteState {

	/**
	 * This method should be called to transition into the subclasses state. 
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject transitionTo(Map<String, Object> remoteCache, 
			HIDRemoteConfiguration remoteConfiguration, 
			CallbackActivity callbackActivity);
	
	/**
	 * Response from server indicating the last request was successful
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject successResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Response from server indicating the last request failed.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject failedResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Response to a status request. A Status indicates what state the server is in
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when connecting to a new host and a pincode has been requested.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when the server had problems interpreting the last request
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject unrecognizedCommand(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when a user interacts with a HID remote. This method checks if the 
	 * remote configuration is ready to send messages to the server.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject validateState(Map<String, Object> remoteCache, 
			HIDRemoteConfiguration remoteConfiguration, 
			CallbackActivity callbackActivity);
}
