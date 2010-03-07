package com.btsd.ui.hidremote;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.btsd.BTScrewDriverAlert;
import com.btsd.BTScrewDriverCallbackHandler;
import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.HIDRemoteState;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;

public class HIDRemoteConfiguration extends RemoteConfiguration {

	private static final String CURRENT_STATE_KEY = "HID_REMOTE_CURRENT_STATE_KEY";
	private static final String CACHED_HID_COMMAND_KEY = "CACHED_HID_COMMAND_KEY";
	private static final String CACHED_HID_ALERT = "CACHED_HID_ALERT";
	
	@Override
	public JSONObject getCommand(ButtonConfiguration buttonConfiguration,
		Map<String, Object> remoteCache, CallbackActivity activity) {
		
		validateState(remoteCache, activity);
		
		int[] keycodes = (int[])buttonConfiguration.getCommand();
		
		return ServerMessages.getKeycodes(keycodes);
	}
	
	@Override
	public JSONObject getCommand(ScreensEnum screen, UserInputTargetEnum target,
		Map<String, Object> remoteCache, CallbackActivity activity) {
		
		validateState(remoteCache, activity);
		
		ButtonConfiguration buttonConfiguration = getButtonConfiguration(screen, target);
		
		int[] keycodes = (int[])buttonConfiguration.getCommand();
		
		return ServerMessages.getKeycodes(keycodes);
	}
	
	private JSONObject validateState(Map<String, Object> remoteCache, 
			CallbackActivity activity){
		
		HIDRemoteState hidState = (HIDRemoteState)remoteCache.get(CURRENT_STATE_KEY);
		if(hidState == null){
			remoteCache.put(CURRENT_STATE_KEY, InitialState.getInstance());
		}
		
		hidState.validateState(this, activity);
		
		/*	BTScrewDriverCallbackHandler.sendPauseAlert(activity, new BTScrewDriverAlert(
					R.string.HID_DEVICE_STATUS, false));
			return ServerMessages.getHidStatus();
		}else if(hidState == WaitingForStatusResponse.getInstance()){
			throw new IllegalStateException("Should not have received any messages " +
				"while in state: WAITING_FOR_STATUS_RESPONSE");
		}*/
		
		return null;
	}
	
	@Override
	public JSONObject serverInteraction(JSONObject messageFromServer,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		try{
			States hidState = (States)remoteCache.get(CURRENT_STATE_KEY);
			if(hidState == null){
				//not expecting any messages
				return null;
			}else if(hidState == States.WAITING_FOR_STATUS_RESPONSE){
				BTScrewDriverCallbackHandler.cancelAlert(activity);
				String status = (String)messageFromServer.get(Main.STATUS);
				if("disconnected".equalsIgnoreCase(status)){
					
					ButtonConfiguration remoteName = getButtonConfiguration(ScreensEnum.ROOT, 
							UserInputTargetEnum.REMOTE_NAME);
					String address = remoteName.getCommand().toString();
					
					Activity currentActivity = (Activity)activity;
					activity.showCancelableDialog(R.string.HID_HOST_CONNECTING_TITLE, 
							currentActivity.getString(R.string.HID_HOST_CONNECT_MESSAGE) + 
							" " + remoteName.getLabel());
					remoteCache.put(CURRENT_STATE_KEY, States.CONNECTING_TO_HOST);
					return ServerMessages.getConnectToHost(address);
					
				}else if("connected".equalsIgnoreCase(status)){
					/*BTScrewDriverCallbackHandler.cancelAlert(this);
					state = States.CONNECTED;*/
					return null;
				}else if("PAIR_MODE".equalsIgnoreCase(status)){
					
					/*pairModeDialog =  new AlertDialog.Builder(this).
						setTitle(R.string.WAITING_FOR_PIN_REQUEST).
						setNegativeButton(android.R.string.cancel, this).
						setMessage(R.string.WAITING_FOR_PIN_REQUEST).
						create();
	    	   
					pairModeDialog.show();
					state= States.WAITING_FOR_PIN_REQUEST;*/
					return null;
				}else{
					throw new IllegalArgumentException("Unknown response");
				}
			}
			return null;
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}
	
	@Override
	public void alertCanceled(Map<String, Object> remoteCache,
			CallbackActivity activity) {
		
		
		
	}
	
	/*private static enum States{
		
		INITIAL_STATE(),
		WAITING_FOR_STATUS_RESPONSE(),
		WAITING_FOR_HID_HOST_RESPONSE(),
		PIN_DIALOG_SHOWN(),
		CONNECTED(),
		CONNECTING_TO_HOST(),
		CONNECTING_TO_HOST_CANCEL(),
		CANCELING_PAIR_MODE(),
		WAITING_FOR_PIN_VALIDATION(),
		WAITING_FOR_PIN_REQUEST();
	}*/
}
