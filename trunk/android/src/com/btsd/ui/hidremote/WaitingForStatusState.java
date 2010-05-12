package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class WaitingForStatusState extends AbstractHIDRemoteState{

	private static WaitingForStatusState instance = null;
	
	private WaitingForStatusState(){}
	
	public static synchronized WaitingForStatusState getInstance(){
		
		if(instance == null){
			instance = new WaitingForStatusState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.CONNECTING_TO_BT_SERVER_TITLE, 
				R.string.HID_DEVICE_STATUS);
		return ServerMessages.getHidStatus();
	}

}
