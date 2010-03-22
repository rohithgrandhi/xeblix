package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class DisconnectingState extends AbstractHIDRemoteState {

	private static DisconnectingState instance = null;
	
	private DisconnectingState(){}
	
	public static synchronized DisconnectingState getInstance(){
		
		if(instance == null){
			instance = new DisconnectingState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		HIDRemoteConfiguration hidRemoteConfig = (HIDRemoteConfiguration)remoteConfiguration;
		String dialog = callbackActivity.getActivity().getString(
				R.string.HID_HOST_CONNECT_MESSAGE);
		callbackActivity.showCancelableDialog(R.string.CONNECTING_TO_BT_SERVER, 
				dialog + hidRemoteConfig.getName());
		return ServerMessages.getDisconnectFromHost();
	}
	
}
