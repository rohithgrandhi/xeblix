package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.ServerMessages;
import com.btsd.ui.RemoteConfiguration;

public final class WaitingForUserResponseState extends AbstractHIDRemoteState {

	public static final String TAG = WaitingForUserResponseState.class.getSimpleName();
	
	private static WaitingForUserResponseState instance = null;
	
	private WaitingForUserResponseState(){}
	
	public static synchronized WaitingForUserResponseState getInstance(){
		
		if(instance == null){
			instance = new WaitingForUserResponseState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		callbackActivity.hideDialog();
		callbackActivity.showPinCodeDialog();
		return null;
	}

	@Override
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, int selectedButton,
			CallbackActivity callbackActivity) {
		
		if(selectedButton == DialogInterface.BUTTON1){
			Log.i(TAG, "Sending server pincode.");
			ServerValidatingPincodeState.getInstance().transitionTo(remoteCache, remoteConfiguration, 
				callbackActivity);
			
			return ServerMessages.getPincodeResponse(callbackActivity.getPincode());
			
		}else if(selectedButton == DialogInterface.BUTTON2){
			Log.i(TAG, "User canceled Pincode Dialog.");
			WaitingForPincodeRequestState.getInstance().transitionTo(remoteCache, 
					remoteConfiguration, callbackActivity);
			
			return ServerMessages.getPincodeCancel();
		}else{
			throw new IllegalArgumentException("Unexpected button: " + selectedButton + 
				" selected.");
		}
		
	}
	
}
