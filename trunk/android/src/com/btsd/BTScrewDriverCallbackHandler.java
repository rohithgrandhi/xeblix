package com.btsd;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.btsd.BTScrewDriverStateMachine.States;
import com.btsd.util.MessagesEnum;

public class BTScrewDriverCallbackHandler extends Handler{

	private static final String TAG = "BTScrewDriverCallbackHandler";
	
	private Activity context;
	//if we get an alert and the app hasn't finished starting then
	//hold onto it until we have focus again
	private BTScrewDriverAlert cachedAlert;
	private BTScrewDriverAlert pauseAlert;
	private boolean hasFocus = false;
	
	public BTScrewDriverCallbackHandler(Activity context){
		this.context = context;
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		Log.d(TAG, "Received message: " + msg.arg1);
		
		if(msg.arg1 == MessagesEnum.SHOW_ERROR_ALERT.getId()){
			Log.d(TAG, "Show Error Alert");
			
			if(pauseAlert != null){
				pauseAlert.dismisAlert();
			}
			
			cachedAlert = null;
			pauseAlert = null;
			
			if(hasFocus){
				BTScrewDriverAlert alert = (BTScrewDriverAlert)msg.obj;
				alert.showAlert(context);
			}else{
				cachedAlert= (BTScrewDriverAlert)msg.obj;
			}	
		}else if(msg.arg1 == MessagesEnum.SHOW_PAUSE_ALERT.getId()){
			Log.d(TAG, "Show Pause Alert");
			
			if(hasFocus){
				Log.d(TAG, "Show Pause Alert Now");
				BTScrewDriverAlert alert = (BTScrewDriverAlert)msg.obj;
				alert.showAlert(context);
			}else{
				Log.d(TAG, "Cache Pause Alert");
				cachedAlert= (BTScrewDriverAlert)msg.obj;
			}
			if(pauseAlert != null){
				pauseAlert.dismisAlert();
			}
			pauseAlert = (BTScrewDriverAlert)msg.obj;
		}else if(msg.arg1 == MessagesEnum.FOCUS_EVENT.getId()){
			hasFocus = (Boolean)msg.obj;
			Log.d(TAG, "changing hasFocus to: " + hasFocus);
			
			if(hasFocus && cachedAlert != null){
				cachedAlert.showAlert(context);
				cachedAlert = null;
			}
			
		}else if(msg.arg1 ==MessagesEnum.CANCEL_PAUSE_ALERT.getId()){
			Log.d(TAG, "Cancel Pause Alert");
			if(pauseAlert != null){
				pauseAlert.dismisAlert();
			}
			
			cachedAlert = null;
			pauseAlert = null;
		}else if(msg.arg1 == MessagesEnum.MESSAGE_FROM_SERVER.getId()){
			CallbackActivity callbackActivity = (CallbackActivity)context;
			callbackActivity.onMessage(MessagesEnum.MESSAGE_FROM_SERVER, msg.obj);
		}else if(msg.arg1 == MessagesEnum.BT_CONNECTION_STATE.getId()){
			
			States state = (States)msg.obj;
			CallbackActivity callbackActivity = (CallbackActivity)context;
			callbackActivity.onMessage(MessagesEnum.BT_CONNECTION_STATE, state);
			
		}else{
			Log.e(TAG, "Unknown message with arg1: " + msg.arg1);
		}
		
	}
	
	public static void sendErrorAlert(CallbackActivity callbackActivity,
			BTScrewDriverAlert alert){
		
		Message message = new Message();
		message.arg1 = MessagesEnum.SHOW_ERROR_ALERT.getId();
		message.obj = alert;
		callbackActivity.sendMessage(message);
	}
	
	public static void sendPauseAlert(CallbackActivity callbackActivity,
			BTScrewDriverAlert alert){
		
		Message message = new Message();
		message.arg1 = MessagesEnum.SHOW_PAUSE_ALERT.getId();
		message.obj = alert;
		callbackActivity.sendMessage(message);
	}
	
	public static void cancelAlert(CallbackActivity callbackActivity){
		Message message = new Message();
		message.arg1 = MessagesEnum.CANCEL_PAUSE_ALERT.getId();
		callbackActivity.sendMessage(message);
	}
	
	public static void focusChangedEvent(CallbackActivity callbackActivity, 
			boolean hasFocus){
		
		Message message = new Message();
		message.arg1 = MessagesEnum.FOCUS_EVENT.getId();
		message.obj = hasFocus;
		callbackActivity.sendMessage(message);
	}
	
	public static void messageFromServer(CallbackActivity callbackActivity, JSONObject serverMessage){
		Message message = new Message();
		message.arg1 = MessagesEnum.MESSAGE_FROM_SERVER.getId();
		message.obj = serverMessage;
		callbackActivity.sendMessage(message);
	}
	
	public static void BTConnectionStatus(CallbackActivity callbackActivity, States state){
		Message message = new Message();
		message.arg1 = MessagesEnum.BT_CONNECTION_STATE.getId();
		message.obj = state;
		callbackActivity.sendMessage(message);
	}
	
}
