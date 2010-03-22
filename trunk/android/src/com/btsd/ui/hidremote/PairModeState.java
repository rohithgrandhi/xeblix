package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

/**
 * While in PairModeState, we are basically waiting for the server to
 * exit the PairMode state. May be it connected to a new host, or failed and
 * went to the disconnected state. 
 * @author klewelling
 *
 */
public final class PairModeState extends AbstractHIDRemoteState{

	private static PairModeState instance = null;
	
	private PairModeState(){}
	
	public static synchronized PairModeState getInstance(){
		
		if(instance == null){
			instance = new PairModeState();
		}
		
		return instance;
	}

	@Override
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		//pincode request is sent to all clients, didn't put the server into pairmode
		//so ignore the request
		Log.i(getClass().getSimpleName(), "Ignoring request for PinCode");
		return null;
	}

	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.showCancelableDialog(R.string.INFO, 
				R.string.SERVER_IN_PAIR_MODE);
		return null;
	}

}
