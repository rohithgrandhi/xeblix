package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

/**
 * Server is in ProbationallyConnected state, need to wait to get connected so we know
 * who the server is connected to
 * @author klewelling
 *
 */
public final class WaitingForConnectOrDisconnectState extends AbstractHIDRemoteState{

	private static WaitingForConnectOrDisconnectState instance = null;
	
	private WaitingForConnectOrDisconnectState(){}
	
	public static synchronized WaitingForConnectOrDisconnectState getInstance(){
		
		if(instance == null){
			instance = new WaitingForConnectOrDisconnectState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.CONNECTING_TO_BT_SERVER, 
				R.string.SERVER_IN_PROBATIONALLY_CONNECTED_MODE);
		return null;
	}

}
