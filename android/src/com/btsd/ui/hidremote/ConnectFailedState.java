package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

public class ConnectFailedState extends AbstractHIDRemoteState {

	private static ConnectFailedState instance = null;
	
	private ConnectFailedState(){}
	
	public static synchronized ConnectFailedState getInstance(){
		
		if(instance == null){
			instance = new ConnectFailedState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		String message = callbackActivity.getActivity().getString(R.string.CONNECTION_FAILED);
		String hostName = ((HIDRemoteConfiguration)remoteConfiguration).getName();
		callbackActivity.showCancelableDialog(R.string.INFO, message + " " + hostName);
		
		return super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
	}
	
}
