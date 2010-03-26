package com.btsd.ui.hidremote;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.HIDRemoteState;
import com.btsd.ui.RemoteConfiguration;

public abstract class AbstractHIDRemoteState implements HIDRemoteState {

	@Override
	public JSONObject failedResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		return HIDRemoteStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
	}

	@Override
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		//server must be in PairMode
		return PairModeState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
	}

	@Override
	public JSONObject pinconfirmationRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//server must be in PairMode
		return PairModeState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		return HIDRemoteStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
	}

	@Override
	public JSONObject successResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		//have not sent anything to warrant a response
		Log.i(getClass().getSimpleName(), "Unexpected call to successResponse");
		return null;
	}

	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		remoteCache.put(HIDRemoteConfiguration.CURRENT_STATE_KEY,this);
		return null;
	}

	@Override
	public JSONObject unrecognizedCommand(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			JSONObject serverMessage, CallbackActivity callbackActivity) {
		
		//have not sent anything to warrant a response
		Log.i(getClass().getSimpleName(), "Unexpected call to unrecognizedCommand");
		return null;
	}

	@Override
	public JSONObject validateState(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		//most states always have a Dialog presented to the user so should not
		//get to this method unless the user "canceled" the dialog and potentially went to
		//another remote screen, so we may have missed any server responses. We have no idea
		//what state we are in, so send the sever a Status request
		return WaitingForStatusState.getInstance().transitionTo(remoteCache,
			remoteConfiguration, callbackActivity);
	}

	@Override
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, int selectedButton,
			CallbackActivity callbackActivity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public JSONObject remoteConfigurationsRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		//check if this remote has been removed
		HIDRemoteConfiguration hidRemote = (HIDRemoteConfiguration)remoteConfiguration;
		String connectedAddress = hidRemote.getHostAddress();
		
		boolean foundCurrentRemote = false;
		for(ButtonConfiguration buttonConfig: remoteConfigNames){
			String hostAddress = buttonConfig.getCommand().toString();
			if(connectedAddress.equalsIgnoreCase(hostAddress)){
				foundCurrentRemote = true;
			}
		}
		
		if(!foundCurrentRemote){
			callbackActivity.returnToPreviousRemoteConfiguration();
		}
		return null;
	}
	
	@Override
	public JSONObject hidHostPincodeCancel(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		callbackActivity.hideDialog();
		
		return WaitingForStatusState.getInstance().transitionTo(remoteCache, 
			remoteConfiguration, callbackActivity);
	}
	
	@Override
	public JSONObject invalidHidHostPinRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,JSONObject serverMessage, 
			CallbackActivity callbackActivity) {
		
		//this method is only called during pair mode.
		return null;
	}
}
