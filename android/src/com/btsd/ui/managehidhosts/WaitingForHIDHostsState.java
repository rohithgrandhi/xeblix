package com.btsd.ui.managehidhosts;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.ServerMessages;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.RemoteConfiguration;

public class WaitingForHIDHostsState extends AbstractHIDRemoteState {

	private static WaitingForHIDHostsState instance = null;
	
	private WaitingForHIDHostsState(){}
	
	public static synchronized WaitingForHIDHostsState getInstance(){
		
		if(instance == null){
			instance = new WaitingForHIDHostsState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		try{
			remoteCache.put(AddHIDHostConfiguration.ADDED_HOST_ADDRESS, 
					serverMessage.getString(Main.HOST_ADDRESS));
			remoteCache.put(AddHIDHostConfiguration.ADDED_HOST_NAME, 
					serverMessage.getString(Main.HOST_NAME));
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		return ServerMessages.getHidHosts();
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		return null;
	}
	
	@Override
	public void remoteConfigurationsRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		//verify the added hid host is in the list
		String addedAddress = remoteCache.get(
			AddHIDHostConfiguration.ADDED_HOST_ADDRESS).toString();
		String addedName = remoteCache.get(
			AddHIDHostConfiguration.ADDED_HOST_NAME).toString();
		boolean foundAddedHost = false;
		for(ButtonConfiguration buttonConfig: remoteConfigNames){
			String hostAddress = buttonConfig.getCommand().toString();
			if(addedAddress.equalsIgnoreCase(hostAddress)){
				foundAddedHost = true;
			}
		}
		
		if(foundAddedHost){
			callbackActivity.selectConfiguredRemote(addedName);
		}else{
			//probably a bug somewhere if we get here
			Log.i(getClass().getSimpleName(), "POSSIBLE BUG! Expected to find host Address: " + 
				addedAddress +  " in list of RemoteConfigurations but did not. Continuing to wait " +
				"RemoteConfigurations refresh.");
		}
		
	}
	
}
