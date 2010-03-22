package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.ui.RemoteConfiguration;

public final class ConnectedState extends AbstractHIDRemoteState {

	private static ConnectedState instance = null;
	
	private ConnectedState(){}
	
	public static synchronized ConnectedState getInstance(){
		
		if(instance == null){
			instance = new ConnectedState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		HIDRemoteConfiguration hidRemoteConfig = (HIDRemoteConfiguration)remoteConfiguration;
		remoteCache.put(HIDRemoteConfiguration.CURRENT_HOST_ADDRESS, 
			hidRemoteConfig.getHostAddress());
		
		//just in case there is an alert, clear it
		callbackActivity.hideDialog();
		
		JSONObject cachedCommand = (JSONObject)remoteCache.get(
			HIDRemoteConfiguration.CACHED_HID_COMMAND_KEY); 
		if(cachedCommand != null){
			remoteCache.remove(HIDRemoteConfiguration.CACHED_HID_COMMAND_KEY);
		}
		
		return cachedCommand;
	}

	@Override
	public JSONObject validateState(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		String currentHostAddress = (String)remoteCache.get(
			HIDRemoteConfiguration.CURRENT_HOST_ADDRESS) ;
		
		//if the user switched to another HID host on the root screen
		//then the expected hostAddress will be different from the 
		//currentHostAddress
		HIDRemoteConfiguration hidRemoteConfig = (HIDRemoteConfiguration)remoteConfiguration;
		if(!hidRemoteConfig.getHostAddress().equalsIgnoreCase(
			currentHostAddress)){
			
			return DisconnectingState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
		}
		
		return null;
	}
	
}
