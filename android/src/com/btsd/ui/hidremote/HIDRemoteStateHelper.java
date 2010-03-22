package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.ui.RemoteConfiguration;

public final class HIDRemoteStateHelper {

	public static JSONObject handleStatus(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		Log.i(HIDRemoteStateHelper.class.getSimpleName(), "Server is in State: " + status);
		
		callbackActivity.hideDialog();
		
		if(Main.STATUS_DISCONNECTED.equalsIgnoreCase(status)){
			//connect to the remoteConfiguration's host
			//check if there is a connect_uuid and that it matches the session uuid
			String connectId = (String)remoteCache.get(HIDRemoteConfiguration.CONNECT_ID);
			String sessionId = remoteCache.get(HIDRemoteConfiguration.SESSION_ID).toString();
			if(sessionId.equals(connectId)){
				//have already tried to connect and failed, don't want to get into a loop
				//by trying again
				return ConnectFailedState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			}else{
				remoteCache.put(HIDRemoteConfiguration.CONNECT_ID, sessionId);
				return ConnectingToHostState.getInstance().transitionTo(remoteCache, 
						remoteConfiguration, callbackActivity);
			}
			
		}else if(Main.STATUS_CONNECTED.equalsIgnoreCase(status)){
			//ok we are connected, are we connected to the correct host?
			
			String connectedAddress = null;
			try{
				connectedAddress = serverMessage.getString(Main.HOST_ADDRESS);
			}catch(JSONException ex){
				throw new RuntimeException(ex);
			}
			
			HIDRemoteConfiguration hidRemoteConfig = (HIDRemoteConfiguration)remoteConfiguration;
			if(hidRemoteConfig.getHostAddress().equalsIgnoreCase(connectedAddress)){
				//great already connected
				return ConnectedState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			}else{
				//need to disconnect from the current host and connect to the 
				//correct one
				return DisconnectingState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			}
			
		}else if(Main.STATUS_PAIR_MODE.equalsIgnoreCase(status)){
			return PairModeState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
		}else if(Main.STATUS_PROBATIONALLY_CONNECTED.equalsIgnoreCase(status)){
			//connecting to a host, need to wait until either connected or disconnected
			return WaitingForConnectOrDisconnectState.getInstance().transitionTo(
				remoteCache, remoteConfiguration, callbackActivity);
		}else{
			throw new RuntimeException("Unknown status: " + status);
		}
	} 
	
}
