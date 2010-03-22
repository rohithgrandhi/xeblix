package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public class ExitingPairModeState extends AbstractHIDRemoteState {

	private static ExitingPairModeState instance = null;
	
	private ExitingPairModeState(){}
	
	public static synchronized ExitingPairModeState getInstance(){
		
		if(instance == null){
			instance = new ExitingPairModeState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		if(Main.STATUS_DISCONNECTED.equalsIgnoreCase(status)){
			callbackActivity.hideDialog();
			callbackActivity.returnToPreviousRemoteConfiguration();
			return null;
		}else{
			return AddHIDHostStateHelper.handleStatus(remoteCache, remoteConfiguration, 
					serverMessage, callbackActivity);
		}
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.CANCELLING_PAIR_MODE);
		
		return ServerMessages.getPairModeCancel();
	}
	
}
