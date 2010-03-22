package com.btsd.ui.managehidhosts;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ui.RemoteConfiguration;

public class ShowingPinConfirmationState extends AbstractHIDRemoteState {

	private static ShowingPinConfirmationState instance = null;
	
	private ShowingPinConfirmationState(){}
	
	public static synchronized ShowingPinConfirmationState getInstance(){
		
		if(instance == null){
			instance = new ShowingPinConfirmationState();
		}
		
		return instance;
	}
	
	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		super.transitionTo(remoteCache, remoteConfiguration, callbackActivity);
		
		return null;
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//look for provisionally connected and connected state, verify we are 
		//connected/connecting to the correct server
		String status = null;
		try{
			status = serverMessage.getString(Main.STATUS);
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
		
		if(Main.STATUS_CONNECTED.equalsIgnoreCase(status)){
			callbackActivity.hideDialog();
			//looks like we have a new host, need to 
			WaitingForHIDHostsState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
			//forward the status to the waitingForHIDHostsState
			return WaitingForHIDHostsState.getInstance().statusResponse(remoteCache, 
				remoteConfiguration, serverMessage, callbackActivity);
		}else if((Main.STATUS_PROBATIONALLY_CONNECTED.equalsIgnoreCase(status))){
			//last think we know we sent a pin code, now server is probationally connected, 
			//stay in this state until we are connected or go back to pair mode
			return null;
		}else{
			return AddHIDHostStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
		}
	}
	
	@Override
	public JSONObject pinconfirmationRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		String pincode = null;
		try{
			pincode = (String)serverMessage.get(Main.PINCODE);
		}catch(JSONException ex){}
		
		String pinConfirmation = callbackActivity.getActivity().getString(
			R.string.PIN_CONFIRMATION);
		
		callbackActivity.showCancelableDialog(R.string.INFO, pinConfirmation + " " + pincode);
		
		return null;
	}
	
}
