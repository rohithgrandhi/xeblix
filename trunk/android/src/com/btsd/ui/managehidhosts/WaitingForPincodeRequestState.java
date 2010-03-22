package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

public final class WaitingForPincodeRequestState extends AbstractHIDRemoteState {

	private static WaitingForPincodeRequestState instance = null;
	
	private WaitingForPincodeRequestState(){}
	
	public static synchronized WaitingForPincodeRequestState getInstance(){
		
		if(instance == null){
			instance = new WaitingForPincodeRequestState();
		}
		
		return instance;
	}

	
	@Override
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, int selectedButton,
			CallbackActivity callbackActivity) {
		
		return ExitingPairModeState.getInstance().transitionTo(remoteCache, 
			remoteConfiguration, callbackActivity);
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.WAITING_FOR_PIN_REQUEST);
		return null;
	}
	
}
