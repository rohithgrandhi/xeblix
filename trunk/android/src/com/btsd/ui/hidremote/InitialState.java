package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.ui.HIDRemoteState;

public class InitialState implements HIDRemoteState {

	private static InitialState instance = null;
	
	private InitialState(){}
	
	public static synchronized InitialState getInstance(){
		
		if(instance == null){
			instance = new InitialState();
		}
		
		return instance;
	}

	@Override
	public JSONObject validateState(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		
		
		return null;
	}
	
	@Override
	public JSONObject failedResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject hidHostsResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject successResponse(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject unrecognizedCommand(Map<String, Object> remoteCache,
			HIDRemoteConfiguration remoteConfiguration, JSONObject serverMessage) {
		// TODO Auto-generated method stub
		return null;
	}

}
