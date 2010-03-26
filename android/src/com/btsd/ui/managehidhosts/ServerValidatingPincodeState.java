package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

public class ServerValidatingPincodeState extends AbstractHIDRemoteState {
	
	private static ServerValidatingPincodeState instance = null;
	
	private ServerValidatingPincodeState(){}
	
	public static synchronized ServerValidatingPincodeState getInstance(){
		
		if(instance == null){
			instance = new ServerValidatingPincodeState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//look for provisionally connected and connected state, verify we are 
		//connected/connecting to the correct server
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		if(Main.STATUS_CONNECTED.equalsIgnoreCase(status)){
			callbackActivity.hideDialog();
			//looks like we have a new host
			WaitingForHIDHostsState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
			//forward the status to the waitingForHIDHostsState
			return WaitingForHIDHostsState.getInstance().statusResponse(remoteCache, 
				remoteConfiguration, serverMessage, callbackActivity);
		}else if((Main.STATUS_PROBATIONALLY_CONNECTED.equalsIgnoreCase(status))){
			//last think we know we sent a pin code, now server is probationally connected, 
			//stay in this state until we are connected or go back to pair mode
			return null;
		}else{
			return AddHIDHostStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
		}
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.HID_HOST_VALIDATING_PIN);
		
		return null;
	}
}
