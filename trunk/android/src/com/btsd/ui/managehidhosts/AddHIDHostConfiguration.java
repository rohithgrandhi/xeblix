package com.btsd.ui.managehidhosts;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.HIDRemoteState;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;

public final class AddHIDHostConfiguration extends RemoteConfiguration {

	private static final String TAG = AddHIDHostConfiguration.class.getSimpleName();
	
	public static final String CURRENT_ADD_HID_STATE = "CURRENT_ADD_HID_STATE";
	public static final String ADDED_HOST_ADDRESS = "ADD_HID_STATE_ADDED_HOST_ADDRESS";
	public static final String ADDED_HOST_NAME = "ADD_HID_STATE_ADDED_HOST_NAME";
	public static final String HOST_ADDRESS_TO_UNPAIR = "HOST_ADDRESS_TO_UNPAIR";
	
	@Override
	public JSONObject alertClicked(int which, Map<String, Object> remoteCache,
			CallbackActivity activity) {
		
		HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
		return hidState.alertDialogClicked(remoteCache, this, which, activity);
	}

	@Override
	public JSONObject getCommand(ScreensEnum screen,
			UserInputTargetEnum target, Map<String, Object> remoteCache,
			CallbackActivity activity) {
		
		if(UserInputTargetEnum.ROOT_ADD_HID_HOST == target){
			HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
			return hidState.validateState(remoteCache, this, activity);
		}else{
			throw new IllegalStateException("Received unexpected command for UserInputTarget: " + 
					target.getName());
		}
	}

	@Override
	public JSONObject getCommand(ButtonConfiguration buttonConfiguration,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		//ignore any user commands, user interaction is handled through dialogs for
		//this configuration
		throw new IllegalStateException("Received unexpected command for ButtonConfiguration: " + 
				buttonConfiguration.getLabel());
	}

	private HIDRemoteState getCurrentRemoteState(Map<String, Object> remoteCache ,CallbackActivity activity) {
		HIDRemoteState hidState = (HIDRemoteState)remoteCache.get(CURRENT_ADD_HID_STATE);
		if(hidState == null){
			InitialState.getInstance().transitionTo(remoteCache, this, activity);
			hidState = (HIDRemoteState)remoteCache.get(CURRENT_ADD_HID_STATE);
		}
		return hidState;
	}
	
	@Override
	public JSONObject serverInteraction(JSONObject messageFromServer,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		try{
			HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
			
			
			String type = (String)messageFromServer.get(Main.TYPE);
			JSONObject toReturn = null;
			if(Main.TYPE_STATUS.equalsIgnoreCase(type)){
				toReturn = hidState.statusResponse(remoteCache, this, messageFromServer,activity);
			}else if(Main.TYPE_RESULT.equalsIgnoreCase(type)){
				String value = (String)messageFromServer.get(Main.VALUE);
				if(Main.RESULT_SUCCESS.equalsIgnoreCase(value)){
					toReturn = hidState.successResponse(remoteCache, this, messageFromServer,activity);
				}else if(Main.RESULT_FAILED.equalsIgnoreCase(value)){
					toReturn = hidState.failedResponse(remoteCache, this, messageFromServer,activity);
				}
			}else if(Main.TYPE_VERSION_REQUEST.equalsIgnoreCase(type)){
				//do nothing for now
				Log.i(TAG, "Received version request value: " + messageFromServer.get(Main.VALUE));
			}else if(Main.TYPE_UNRECOGNIZED_COMMAND.equalsIgnoreCase(type)){
				toReturn = hidState.unrecognizedCommand(remoteCache, this, messageFromServer,activity);
			}else if(Main.TYPE_PINCODE_REQUEST.equalsIgnoreCase(type)){
				toReturn = hidState.pincodeRequest(remoteCache, this, messageFromServer,activity);
			}else if(Main.TYPE_PINCONFIRMATION_REQUEST.equalsIgnoreCase(type)){
				toReturn = hidState.pinconfirmationRequest(remoteCache, this, messageFromServer,activity);
			}else if(Main.TYPE_HID_HOST_PIN_CANCEL.equalsIgnoreCase(type)){
				toReturn = hidState.hidHostPincodeCancel(remoteCache, this, messageFromServer, activity);
			}else if(Main.TYPE_INVALID_PIN_REQUEST.equalsIgnoreCase(type)){
				toReturn = hidState.invalidHidHostPinRequest(remoteCache, this,messageFromServer, activity);
			}else if(Main.TYPE_UNPAIR_HID_HOST.equalsIgnoreCase(type)){
				toReturn = hidState.unpairHIDHost(remoteCache, this, messageFromServer, activity);
			}else{
				throw new IllegalArgumentException("Unexpected message type: " + type);
			}

			return toReturn;
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public JSONObject remoteConfigurationRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
		return hidState.remoteConfigurationsRefreshed(remoteConfigNames, remoteCache, 
			this, activity);
	}
	
}
