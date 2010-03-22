package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class WaitingForDisconnectState extends AbstractHIDRemoteState {

	private static WaitingForDisconnectState instance = null;
	
	private WaitingForDisconnectState(){}
	
	public static synchronized WaitingForDisconnectState getInstance(){
		
		if(instance == null){
			instance = new WaitingForDisconnectState();
		}
		
		return instance;
	}

	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.DISCONNECTING_FROM_HID_HOST);
		return ServerMessages.getDisconnectFromHost();
	}
	
}
