package com.btsd.ui.managehidhosts;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.ButtonConfiguration;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.HIDRemoteState;
import com.btsd.ui.RemoteConfiguration;

public abstract class AbstractHIDRemoteState implements HIDRemoteState {

	@Override
	public JSONObject alertDialogClicked(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, int selectedButton,
			CallbackActivity callbackActivity) {
		return null;
	}

	@Override
	public JSONObject failedResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		return AddHIDHostStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
	}

	@Override
	public JSONObject pincodeRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		return WaitingForUserResponseState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
	}

	@Override
	public JSONObject pinconfirmationRequest(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		ShowingPinConfirmationState.getInstance().transitionTo(
			remoteCache, remoteConfiguration, callbackActivity);
		
		return ShowingPinConfirmationState.getInstance().pinconfirmationRequest(
			remoteCache, remoteConfiguration, serverMessage, callbackActivity);
	}
	
	@Override
	public JSONObject statusResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		return AddHIDHostStateHelper.handleStatus(remoteCache, remoteConfiguration, 
				serverMessage, callbackActivity);
	}

	@Override
	public JSONObject successResponse(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//have not sent anything to warrant a response
		Log.i(getClass().getSimpleName(), "Unexpected call to successResponse");
		return null;
	}

	@Override
	public JSONObject transitionTo(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		remoteCache.put(AddHIDHostConfiguration.CURRENT_ADD_HID_STATE,this);
		return null;
	}

	@Override
	public JSONObject unrecognizedCommand(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		//have not sent anything to warrant a response
		Log.i(getClass().getSimpleName(), "Unexpected call to unrecognizedCommand");
		return null;
	}

	@Override
	public JSONObject validateState(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		return WaitingForStatusState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
	}

	@Override
	public JSONObject remoteConfigurationsRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration,
			CallbackActivity callbackActivity) {
		
		callbackActivity.returnToPreviousRemoteConfiguration();
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
		
		ShowingInvalidPinRequstState.getInstance().transitionTo(remoteCache, 
				remoteConfiguration, callbackActivity);
		
		return ShowingInvalidPinRequstState.getInstance().invalidHidHostPinRequest(
				remoteCache, remoteConfiguration, serverMessage, callbackActivity);
	}
	
	@Override
	public JSONObject unpairHIDHost(Map<String, Object> remoteCache,
			RemoteConfiguration remoteConfiguration, JSONObject serverMessage,
			CallbackActivity callbackActivity) {
		
		String address = null;
		try{
			address = serverMessage.getString(Main.HOST_ADDRESS);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		callbackActivity.showCancelableDialog(R.string.INFO, R.string.REMOVING_HID_HOST);
		return ServerMessages.getRemovePairedHost(address);
	}
}
