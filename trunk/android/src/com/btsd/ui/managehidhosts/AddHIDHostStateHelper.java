package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.ui.RemoteConfiguration;

public class AddHIDHostStateHelper {

	public static JSONObject handleStatus(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		Log.i(AddHIDHostStateHelper.class.getSimpleName(), "Server is in State: " + status);
		
		callbackActivity.hideDialog();
		
		if(Main.STATUS_DISCONNECTED.equalsIgnoreCase(status)){
			//if in disconnected state, go straight to pair_mode
			return WaitingForPairModeState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			
		}else if(Main.STATUS_CONNECTED.equalsIgnoreCase(status) || 
			(Main.STATUS_PROBATIONALLY_CONNECTED.equalsIgnoreCase(status))){
			
			//need to disconnect
			return WaitingForDisconnectState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
		}else if(Main.STATUS_PAIR_MODE.equalsIgnoreCase(status)){
			return WaitingForPincodeRequestState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
		}else{
			throw new RuntimeException("Unknown status: " + status);
		}

	}
	
}
