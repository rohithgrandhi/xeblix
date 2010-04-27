package com.btsd.ui.managehidhosts;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.ButtonConfiguration;

import android.content.DialogInterface;
import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public class ShowingInvalidPinRequstState extends AbstractHIDRemoteState {
	
	public static final String TAG = ShowingInvalidPinRequstState.class.getSimpleName();
	
	private static ShowingInvalidPinRequstState instance = null;
	
	private ShowingInvalidPinRequstState(){}
	
	public static synchronized ShowingInvalidPinRequstState getInstance(){
		
		if(instance == null){
			instance = new ShowingInvalidPinRequstState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//ignore until we get a response from the user
		return null;
	}
	
	@Override
	public JSONObject invalidHidHostPinRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		String message = callbackActivity.getActivity().getString(R.string.INVALID_PIN_REQUEST_MSG1);
		
		try{
			//store the host_address in case the user selects unpair
			remoteCache.put(AddHIDHostConfiguration.HOST_ADDRESS_TO_UNPAIR, 
					serverMessage.getString(Main.HOST_ADDRESS));
			
			String host = serverMessage.getString(Main.HOST_NAME);
			message += " " + host + " " + callbackActivity.getActivity().
				getString(R.string.INVALID_PIN_REQUEST_MSG2);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		callbackActivity.hideDialog();
		callbackActivity.showCancelableDialog(R.string.INVALID_PIN_REQUEST, message, 
				R.string.INVALID_PIN_REQUEST_BTN1, null);
		return null;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		return null;
	}
	
	@Override
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, int selectedButton,
			CallbackActivity callbackActivity) {
		
		JSONObject toReturn = null;
		if(selectedButton == DialogInterface.BUTTON1){
			
			String address = (String)remoteCache.get(
				AddHIDHostConfiguration.HOST_ADDRESS_TO_UNPAIR);
			
			Log.i(TAG, "User selected to unpair host: " + address);
			
			toReturn = ServerMessages.getRemovePairedHost(address);
			
			callbackActivity.showCancelableDialog(R.string.INFO, R.string.REMOVING_HID_HOST);
		}else if(selectedButton == DialogInterface.BUTTON2){
			Log.i(TAG, "User canceled INVALID_PIN_REQUEST dialog.");
			WaitingForStatusState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			toReturn = ServerMessages.getHidStatus();
		}
		
		return toReturn;
	}
	
	/*@Override
	public JSONObject successResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//this method will be called after a Paired host has been removed
		WaitingForStatusState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
		return ServerMessages.getHidStatus();
	}*/
	
	@Override
	public JSONObject remoteConfigurationsRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		//verify address has been removed from remoteConfigurations
		String address = (String)remoteCache.get(
				AddHIDHostConfiguration.HOST_ADDRESS_TO_UNPAIR);
		
		boolean foundRemovedHost= false;
		for(ButtonConfiguration buttonConfig: remoteConfigNames){
			String hostAddress = buttonConfig.getCommand().toString();
			if(address.equalsIgnoreCase(hostAddress)){
				foundRemovedHost = true;
			}
		}
		
		if(foundRemovedHost){
			Log.e(getClass().getSimpleName(), "Address: " + address + " was suppose to " +
				"have been removed but it is still part of the remote configuration.");
		}
		
		//have ignored most messages in the state so need to re-sync with the server
		WaitingForStatusState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
		return ServerMessages.getHidStatus();
	}
}
