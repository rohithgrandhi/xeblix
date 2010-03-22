package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class ConnectingToHostState extends AbstractHIDRemoteState{

	private static ConnectingToHostState instance = null;
	
	private ConnectingToHostState(){}
	
	public static synchronized ConnectingToHostState getInstance(){
		
		if(instance == null){
			instance = new ConnectingToHostState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		//if we get a status result of ProbationallyConnected its just 
		//confirmation of the server state change 
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		//if this line is uncommented, uncomment the showConnectingDialog
		//for STATUS_PROBATIONALLY_CONNECTED
		//callbackActivity.hideDialog();
		
		if(Main.STATUS_PROBATIONALLY_CONNECTED.equalsIgnoreCase(status)){
			//showConnectingDialog(remoteConfiguration, callbackActivity);
			return null;
		}
		
		//else do whatever is normally done
		return HIDRemoteStateHelper.handleStatus(remoteCache, remoteConfiguration, 
			serverMessage, callbackActivity);
	}

	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		HIDRemoteConfiguration hidRemoteConfig = showConnectingDialog(
				remoteConfiguration, callbackActivity);
		return ServerMessages.getConnectToHost(hidRemoteConfig.getHostAddress());
	}

	private HIDRemoteConfiguration showConnectingDialog(
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		HIDRemoteConfiguration hidRemoteConfig = (HIDRemoteConfiguration)remoteConfiguration;
		String dialog = callbackActivity.getActivity().getString(R.string.HID_HOST_CONNECT_MESSAGE);
		callbackActivity.showCancelableDialog(R.string.CONNECTING_TO_BT_SERVER, 
				dialog + " " + hidRemoteConfig.getName());
		return hidRemoteConfig;
	}
	
}
