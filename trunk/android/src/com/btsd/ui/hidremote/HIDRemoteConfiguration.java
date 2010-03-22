package com.btsd.ui.hidremote;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.ServerMessages;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.HIDRemoteState;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;

public final class HIDRemoteConfiguration extends RemoteConfiguration {

	private static final String TAG = HIDRemoteConfiguration.class.getSimpleName();
	
	public static final String CURRENT_STATE_KEY = "HID_REMOTE_CURRENT_STATE_KEY";
	public static final String CURRENT_HOST_ADDRESS = "HID_HOST_ADDRESS"; 
	public static final String CACHED_HID_COMMAND_KEY = "CACHED_HID_COMMAND_KEY";
	public static final String SESSION_ID = "HID_REMOTE_SESSION_ID";
	public static final String CONNECT_ID = "HID_REMOTE_CONNECT_ID";
	//public static final String CACHED_HID_ALERT = "CACHED_HID_ALERT";
	
	@Override
	public JSONObject getCommand(ButtonConfiguration buttonConfiguration,
		Map<String, Object> remoteCache, CallbackActivity activity) {
		
		int[] keycodes = (int[])buttonConfiguration.getCommand();
		
		JSONObject toReturn = ServerMessages.getKeycodes(keycodes);
		JSONObject serverMessage =  validateState(remoteCache, activity);
		//if not in the correct state, cache the command and send the command
		//from the validateState method 
		if(serverMessage != null){
			remoteCache.put(CACHED_HID_COMMAND_KEY, toReturn);
			return serverMessage;
		}
		
		return toReturn;
	}
	
	@Override
	public JSONObject getCommand(ScreensEnum screen, UserInputTargetEnum target,
		Map<String, Object> remoteCache, CallbackActivity activity) {
		
		ButtonConfiguration buttonConfiguration = getButtonConfiguration(screen, target);
		int[] keycodes = (int[])buttonConfiguration.getCommand();
		 
		JSONObject toReturn = ServerMessages.getKeycodes(keycodes);
		JSONObject serverMessage =  validateState(remoteCache, activity);
		
		//if not in the correct state, cache the command and send the command
		//from the validateState method 
		if(serverMessage != null){
			remoteCache.put(CACHED_HID_COMMAND_KEY, toReturn);
			return serverMessage;
		}
		
		return toReturn;
	}
	
	private JSONObject validateState(Map<String, Object> remoteCache, 
			CallbackActivity activity){
		
		HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
		remoteCache.put(SESSION_ID, UUID.randomUUID().toString());
		return hidState.validateState(remoteCache, this, activity);
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
			}else{
				throw new IllegalArgumentException("Unexpected message type: " + type);
			}

			return toReturn;
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}
	
	@Override
	public JSONObject alertClicked(int which, Map<String, Object> remoteCache,
			CallbackActivity activity) {
		
		HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
		
		return hidState.alertDialogClicked(remoteCache, this, which, activity);
	}
	
	public String getHostAddress(){
		ButtonConfiguration remoteName = getButtonConfiguration(ScreensEnum.ROOT,
			UserInputTargetEnum.REMOTE_NAME);
		return remoteName.getCommand().toString();
	}
	
	public String getName(){
		ButtonConfiguration remoteName = getButtonConfiguration(ScreensEnum.ROOT,
			UserInputTargetEnum.REMOTE_NAME);
		return remoteName.getLabel();
	}
	
	private HIDRemoteState getCurrentRemoteState(Map<String, Object> remoteCache ,
		CallbackActivity activity) {
		
		HIDRemoteState hidState = (HIDRemoteState)remoteCache.get(CURRENT_STATE_KEY);
		if(hidState == null){
			InitialState.getInstance().transitionTo(remoteCache, this, activity);
			hidState = (HIDRemoteState)remoteCache.get(CURRENT_STATE_KEY);
		}
		return hidState;
	}
	
	@Override
	public void remoteConfigurationRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		HIDRemoteState hidState = getCurrentRemoteState(remoteCache, activity);
		hidState.remoteConfigurationsRefreshed(remoteConfigNames, remoteCache, 
			this, activity);
		
	}
}
