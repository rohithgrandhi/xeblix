package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class WaitingForPairModeState extends AbstractHIDRemoteState {

	private static WaitingForPairModeState instance = null;
	
	private WaitingForPairModeState(){}
	
	public static synchronized WaitingForPairModeState getInstance(){
		
		if(instance == null){
			instance = new WaitingForPairModeState();
		}
		
		return instance;
	}

	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.ENTERING_PAIR_MODE);
		return ServerMessages.getPairMode();
	}

}
