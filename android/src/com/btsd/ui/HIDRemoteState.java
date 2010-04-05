package com.btsd.ui;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;

public interface HIDRemoteState {

	/**
	 * This method should be called to transition into the subclasses state. 
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject transitionTo(Map<String, Object> remoteCache, 
			RemoteConfiguration remoteConfiguration, 
			CallbackActivity callbackActivity);
	
	/**
	 * Response from server indicating the last request was successful
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject successResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Response from server indicating the last request failed.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject failedResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Response to a status request. A Status indicates what state the server is in
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when connecting to a new host and a pincode has been requested.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when connecting to a new host and a pincode confirmation has been requested
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param serverMessage
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject pinconfirmationRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when a user cancels the pin request from the HID Host.
	 * TODO: this will probably be removed, the server isn't handling the hidhost pincode cancel
	 * correctly, the server should return to pair mode and send out a status
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param serverMessage
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject hidHostPincodeCancel(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * Called when the server had problems interpreting the last request
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject unrecognizedCommand(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, 
			JSONObject serverMessage, CallbackActivity callbackActivity);
	
	/**
	 * When a user clicks a button on a dialog a RemoteConfiguration created, 
	 * then this method will be called. This method is useful to stop the server 
	 * from connecting to a hid host, or canceling PairMode,etc.
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param selectedButton
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache, 
			RemoteConfiguration remoteConfiguration, int selectedButton, 
			CallbackActivity callbackActivity);
	
	/**
	 * Called when a user interacts with a HID remote. This method checks if the 
	 * remote configuration is ready to send messages to the server.
	 * @param remoteConfiguration
	 * @param serverMessage
	 */
	public JSONObject validateState(Map<String, Object> remoteCache, 
			RemoteConfiguration remoteConfiguration, 
			CallbackActivity callbackActivity);
	
	/**
	 * Called when the remoteConfigurations have been refreshed. This can be used to
	 * notify HIDRemotes that new HID Remotes have been added or that this hidRemote
	 * has been removed.  
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param callbackActivity
	 */
	public JSONObject remoteConfigurationsRefreshed(List<ButtonConfiguration> remoteConfigNames, 
			Map<String, Object> remoteCache, RemoteConfiguration remoteConfiguration, 
			CallbackActivity callbackActivity);
	
	/**
	 * Called when a HID Host that is already paired with the Xeblix server requests
	 * a pin code like it is Pairing. This method will only be called while in pair mode. 
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param callbackActivity
	 */
	public JSONObject invalidHidHostPinRequest(Map<String, Object> remoteCache, 
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage, 
			CallbackActivity callbackActivity);
	
	/**
	 * Called when the user selects a HID Host to remove.
	 * @param remoteCache
	 * @param remoteConfiguration
	 * @param hidHostAddress
	 * @param callbackActivity
	 * @return
	 */
	public JSONObject unpairHIDHost(Map<String, Object> remoteCache, 
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage, 
			CallbackActivity callbackActivity);
}
