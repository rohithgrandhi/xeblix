package com.btsd;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.btsd.util.MessagesEnum;

public class BluetoothHIDActivity extends AbstractRemoteActivity  implements DialogInterface.OnClickListener{

	private static final String TAG = "BTHID";
	
	public static HashMap<Integer, Integer> keyMapping;
	private AlertDialog hidHostsDialog;
	private AlertDialog pincodeDialog;
	private AlertDialog pairModeDialog;
	private AlertDialog connectingToHIDHostDialog;
	private int selectedHIDHostIndex = 0;
	private EditText pincodeView = null;
	private States state = States.INITIAL_STATE;
	private ArrayList<JSONObject> hidHosts = null;
	
	public static final int CONTROL_LEFT = -1;
	public static final int F8 = -4;
	public static final int F9 = -2;
	public static final int F10 = -3;
	
	static{
		keyMapping = new HashMap<Integer, Integer>();
		keyMapping.put(KeyEvent.KEYCODE_A, 4);
		keyMapping.put(KeyEvent.KEYCODE_B, 5);
		keyMapping.put(KeyEvent.KEYCODE_C, 6);
		keyMapping.put(KeyEvent.KEYCODE_D, 7);
		keyMapping.put(KeyEvent.KEYCODE_E, 8);
		keyMapping.put(KeyEvent.KEYCODE_F, 9);
		keyMapping.put(KeyEvent.KEYCODE_G, 10);
		keyMapping.put(KeyEvent.KEYCODE_H, 11);
		keyMapping.put(KeyEvent.KEYCODE_I, 12);
		keyMapping.put(KeyEvent.KEYCODE_J, 13);
		keyMapping.put(KeyEvent.KEYCODE_K, 14);
		keyMapping.put(KeyEvent.KEYCODE_L, 15);
		keyMapping.put(KeyEvent.KEYCODE_M, 16);
		keyMapping.put(KeyEvent.KEYCODE_N, 17);
		keyMapping.put(KeyEvent.KEYCODE_O, 18);
		keyMapping.put(KeyEvent.KEYCODE_P, 19);
		keyMapping.put(KeyEvent.KEYCODE_Q, 20);
		keyMapping.put(KeyEvent.KEYCODE_R, 21);
		keyMapping.put(KeyEvent.KEYCODE_S, 22);
		keyMapping.put(KeyEvent.KEYCODE_T, 23);
		keyMapping.put(KeyEvent.KEYCODE_U, 24);
		keyMapping.put(KeyEvent.KEYCODE_V, 25);
		keyMapping.put(KeyEvent.KEYCODE_W, 26);
		keyMapping.put(KeyEvent.KEYCODE_X, 27);
		keyMapping.put(KeyEvent.KEYCODE_Y, 28);
		keyMapping.put(KeyEvent.KEYCODE_Z, 29);
		
		keyMapping.put(KeyEvent.KEYCODE_1, 30);
		keyMapping.put(KeyEvent.KEYCODE_2, 31);
		keyMapping.put(KeyEvent.KEYCODE_3, 32);
		keyMapping.put(KeyEvent.KEYCODE_4, 33);
		keyMapping.put(KeyEvent.KEYCODE_5, 34);
		keyMapping.put(KeyEvent.KEYCODE_6, 35);
		keyMapping.put(KeyEvent.KEYCODE_7, 36);
		keyMapping.put(KeyEvent.KEYCODE_8, 37);
		keyMapping.put(KeyEvent.KEYCODE_9, 38);
		keyMapping.put(KeyEvent.KEYCODE_0, 39);
		
		keyMapping.put(KeyEvent.KEYCODE_ENTER, 40);
		keyMapping.put(KeyEvent.KEYCODE_TAB, 43);
		keyMapping.put(KeyEvent.KEYCODE_SPACE, 44);
		keyMapping.put(KeyEvent.KEYCODE_MINUS, 45);
		keyMapping.put(KeyEvent.KEYCODE_EQUALS, 46);
		keyMapping.put(KeyEvent.KEYCODE_LEFT_BRACKET, 47);
		keyMapping.put(KeyEvent.KEYCODE_RIGHT_BRACKET, 48);
		keyMapping.put(KeyEvent.KEYCODE_BACKSLASH, 49);
		keyMapping.put(KeyEvent.KEYCODE_SEMICOLON, 51);
		keyMapping.put(KeyEvent.KEYCODE_APOSTROPHE, 52);
		keyMapping.put(KeyEvent.KEYCODE_GRAVE, 53);
		keyMapping.put(KeyEvent.KEYCODE_COMMA, 54);
		keyMapping.put(KeyEvent.KEYCODE_PERIOD, 55);
		keyMapping.put(KeyEvent.KEYCODE_SLASH, 56);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_RIGHT, 79);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_LEFT, 80);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_DOWN, 81);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_UP, 82);
		keyMapping.put(KeyEvent.KEYCODE_DEL, 42);
		keyMapping.put(CONTROL_LEFT, 224); 
		keyMapping.put(KeyEvent.KEYCODE_SHIFT_LEFT, 225);
		keyMapping.put(KeyEvent.KEYCODE_ALT_LEFT, 226);
		keyMapping.put(KeyEvent.KEYCODE_SEARCH, 227);
		keyMapping.put(F8, 65);
		keyMapping.put(F9, 66);
		keyMapping.put(F10, 67);
		
	}
	
	public static Integer getKeycode(int keycode){
		return keyMapping.get(keycode);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bthid);
		
		int[] buttons = new int[]{/*R.id.bthidTest*/};
		for(int buttonId: buttons){
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}
		
		//only send status request if in initial state
		if(state == States.INITIAL_STATE){
			BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
					R.string.HID_DEVICE_STATUS, false));
			state = States.WAITING_FOR_STATUS_RESPONSE;
			getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getHidStatus());
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(hidHostsDialog != null && hidHostsDialog.isShowing()){
			hidHostsDialog.dismiss();
    	}
	}
	
	@Override
	public void onClick(View v) {
		Button button = (Button)v;
		//Main.statemachine.sendCommand("bthid","test", 2, -1);
	}

	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		if(messagesEnum  == MessagesEnum.MESSAGE_FROM_SERVER){
			try{
				handleMessageFromServer((JSONObject)message);
			}catch(JSONException ex){
				BTScrewDriverAlert alert = new BTScrewDriverAlert(
					R.string.BT_SERVER_COMM_FAILURE , true);
				alert.showAlert(this);
			}
		}
	}
	
	private void handleMessageFromServer(JSONObject message) throws JSONException{
		
		Log.i(TAG, "Client message: " + message.toString());
		
		if(state == States.WAITING_FOR_STATUS_RESPONSE){
			
			BTScrewDriverCallbackHandler.cancelAlert(this);
			
			String status = (String)message.get(Main.STATUS);
			if("disconnected".equalsIgnoreCase(status)){
				//ok not connected, get a list of possible hid hosts
				BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.HID_HOSTS, false));
				getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getHidHosts());
				state = States.WAITING_FOR_HID_HOST_RESPONSE;
				
			}else if("connected".equalsIgnoreCase(status)){
				BTScrewDriverCallbackHandler.cancelAlert(this);
				state = States.CONNECTED;
			}else if("PAIR_MODE".equalsIgnoreCase(status)){
				
				pairModeDialog =  new AlertDialog.Builder(this).
					setTitle(R.string.WAITING_FOR_PIN_REQUEST).
					setNegativeButton(android.R.string.cancel, this).
					setMessage(R.string.WAITING_FOR_PIN_REQUEST).
					create();
    	   
				pairModeDialog.show();
				state= States.WAITING_FOR_PIN_REQUEST;
			}else{
				throw new IllegalArgumentException("Unknown response");
			}
			
		}else if(state == States.WAITING_FOR_HID_HOST_RESPONSE){
			
			BTScrewDriverCallbackHandler.cancelAlert(this);
			
			JSONArray serverMessage = message.getJSONArray(Main.VALUE);
			createHIDHostsDialog(serverMessage);
			
		}else if(state == States.WAITING_FOR_PIN_REQUEST){
			
			if("result".equalsIgnoreCase(message.getString(Main.TYPE))){
				if("success".equalsIgnoreCase(message.getString(Main.VALUE))){
					//Enter PAIR_MODE response, can ignore it
				}else{
					throw new IllegalArgumentException("Failed to enter PAIR_MODE");
				}
			}else if("PINCODE_REQUEST".equalsIgnoreCase(message.getString(Main.TYPE))){
				pairModeDialog.dismiss();
				createPinCodeDialog();
				state = States.PIN_DIALOG_SHOWN;
			}
		}else if(state == States.PIN_DIALOG_SHOWN){
			
			if("HIDHostPinCancel".equalsIgnoreCase(message.getString(Main.TYPE))){
				pincodeDialog.dismiss();
				pincodeView = null;
				pincodeDialog = null;
				
				pairModeDialog =  new AlertDialog.Builder(this).
					setTitle(R.string.WAITING_FOR_PIN_REQUEST).
					setNegativeButton(android.R.string.cancel, this).
					setMessage(R.string.WAITING_FOR_PIN_REQUEST).
					create();
	    	   
				pairModeDialog.show();
				
				/*BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.WAITING_FOR_PIN_REQUEST, false));*/
				state= States.WAITING_FOR_PIN_REQUEST;
			}
		}else if(state == States.WAITING_FOR_PIN_VALIDATION){
			
			if("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"Connected".equalsIgnoreCase(message.getString(Main.STATUS))){
				
				BTScrewDriverCallbackHandler.cancelAlert(this);
				state = States.CONNECTED;
			}else if( ("result".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"FAILED".equalsIgnoreCase(message.getString(Main.VALUE))) || 
				("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
					"PAIR_MODE".equalsIgnoreCase(message.getString(Main.STATUS)))){
				//TODO: alert user request canceled
				BTScrewDriverCallbackHandler.cancelAlert(this);
				pairModeDialog =  new AlertDialog.Builder(this).
					setTitle(R.string.WAITING_FOR_PIN_REQUEST).
					setNegativeButton(android.R.string.cancel, this).
					setMessage(R.string.WAITING_FOR_PIN_REQUEST).
					create();
	    	   
				pairModeDialog.show();
				state= States.WAITING_FOR_PIN_REQUEST;
			}else{
				
				Log.w(TAG, "implement me");
			}
		}else if(state == States.CANCELING_PAIR_MODE){
			
			//only care about received message, ignore all others
			if("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"disconnected".equalsIgnoreCase(message.getString(Main.STATUS))){
				
				//go back to the original state, after a pair_mode cancel the server
				//will attempt to reconnect to primary HID, so need to get a new status
				BTScrewDriverCallbackHandler.cancelAlert(this);
				BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.HID_DEVICE_STATUS, false));
				
				getBTSDApplication().getStateMachine().messageToServer(ServerMessages.getHidStatus());
				state = States.WAITING_FOR_STATUS_RESPONSE;
			}
		}else if(state == States.CONNECTED){
			
			if("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"disconnected".equalsIgnoreCase(message.getString(Main.STATUS))){
				//disconnected, need to go back to the initial state
				BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.HID_DEVICE_STATUS, false));
				
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getHidStatus());
				state = States.WAITING_FOR_STATUS_RESPONSE;
			}/*else if("FAILED".equalsIgnoreCase(message)){
				Log.w(TAG, "Last message failed to reach HID Host");
			}*/
		}else if(state == States.CONNECTING_TO_HOST){
			
			Log.i(TAG, "Recieved message: " + message.toString() + 
				" while waiting for host to connect.");
			
			if("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"connected".equalsIgnoreCase(message.getString(Main.STATUS))){
				
				connectingToHIDHostDialog.dismiss();
				connectingToHIDHostDialog = null;
				state = States.CONNECTED;
			}else if("status".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"disconnected".equalsIgnoreCase(message.getString(Main.STATUS))){
				
				BTScrewDriverCallbackHandler.cancelAlert(this);
				BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.HID_DEVICE_STATUS, false));
				
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getHidStatus());
				state = States.WAITING_FOR_STATUS_RESPONSE;
				
			}else{
				Log.i(TAG, "Recieved message: " + message.toString() + 
					" while waiting for host to connect.");
			}
			
		}else if(state == States.CONNECTING_TO_HOST_CANCEL){
			
			//TODO:possible for this "Received" could be a response for the CONECTING_TO_HOST message
			//really need ids for every message so they can be distinguished
			if("result".equalsIgnoreCase(message.getString(Main.TYPE)) && 
				"success".equalsIgnoreCase(message.getString(Main.VALUE))){
				//go back to the original state, after a pair_mode cancel the server
				//will attempt to reconnect to primary HID, so need to get a new status
				BTScrewDriverCallbackHandler.cancelAlert(this);
				BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
						R.string.HID_DEVICE_STATUS, false));
				
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getHidStatus());
				state = States.WAITING_FOR_STATUS_RESPONSE;
			}
			
		}else{
			Log.w(TAG, "Unexpected message from server: " + message);
		}
		
	}
	
	private void createPinCodeDialog(){
    	
    	pincodeView = new EditText(this);

		pincodeDialog = new AlertDialog.Builder(this)
			.setCancelable(false)
	        .setTitle(R.string.PINCODE_DIALOG_TITLE)
	        .setPositiveButton(android.R.string.ok, this)
	        .setNegativeButton(android.R.string.cancel, this).
	        setView(pincodeView).create();
		pincodeDialog.show();
    }
	
	private void createHIDHostsDialog(JSONArray hidHosts){
		
		ArrayList<CharSequence> hidHostsToDisplay = new ArrayList<CharSequence>();
		hidHostsToDisplay.add(0, getString(R.string.ENTER_PAIR_MODE));
		this.hidHosts = new ArrayList<JSONObject>();
		for(int i=0; i < hidHosts.length(); i++){
			//sdfg;
			//throw new UnsupportedOperationException("Implement me");
			try{
				JSONObject jsonObject = hidHosts.getJSONObject(i);
				
				this.hidHosts.add(jsonObject);
				
				String address = jsonObject.getString("address");
				String hostName = jsonObject.getString("name");
				//boolean primary = jsonObject.getBoolean("primary");
				boolean primary = false;
				
				if(hostName != null && primary){
					hidHostsToDisplay.add(i + 1, hostName + " (Primary)");
				}else if(hostName != null && !primary){
					hidHostsToDisplay.add(i + 1, hostName);
				}else if(hostName == null && primary){
					hidHostsToDisplay.add(i + 1, address + " (Primary)");
				}else if(hostName == null && primary){
					hidHostsToDisplay.add(i + 1, address);
				}
			}catch(JSONException ex){
				//if we get here then the server is not sending the expected
				//info
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
		
		CharSequence[] hidHostsToDisplayArray = hidHostsToDisplay.toArray(
			new CharSequence[hidHostsToDisplay.size()]);
		
		//the default is Enter Pair Mode
		selectedHIDHostIndex = 0;
		
		hidHostsDialog = new AlertDialog.Builder(this)
	        .setTitle(R.string.HID_HOST_DIALOG_TITLE)
	        .setPositiveButton(android.R.string.ok, this)
	        .setNegativeButton(android.R.string.cancel, null).
	        setSingleChoiceItems(hidHostsToDisplayArray,
	                0, this).create();
		hidHostsDialog.show();
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if(dialog == this.hidHostsDialog){
			handleHIDHostDialogOnClick(which);
		}else if(dialog == this.pincodeDialog){
			handlePincodeDialogOnClick(which);
		}else if(dialog == this.pairModeDialog){
			handlePairModeDialogOnClick(which);
		}else if(dialog == this.connectingToHIDHostDialog){
			handleConnectingToHIDHostOnClick(which);
		}
	}
	
	private void handlePairModeDialogOnClick(int which){
		
		if(which == DialogInterface.BUTTON2){
			getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getPairModeCancel());
			this.state = States.CANCELING_PAIR_MODE;
			
			BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
					R.string.PAIR_MODE_CANCEL, false));
			
		}else{
			throw new IllegalStateException("Unexpected button clicked");
		}
		
	}
	
	private void handlePincodeDialogOnClick(int which){
		
		if(which == DialogInterface.BUTTON1){
			String pinCode = pincodeView.getText().toString();
			getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getPincodeResponse(pinCode));
			pincodeView = null;
			pincodeDialog = null;
			BTScrewDriverCallbackHandler.sendPauseAlert(this, new BTScrewDriverAlert(
					R.string.HID_HOST_VALIDATING_PIN, false));
			state = States.WAITING_FOR_PIN_VALIDATION;
		}else if(which == DialogInterface.BUTTON2){
			pincodeView = null;
			pincodeDialog = null;
			pairModeDialog =  new AlertDialog.Builder(this).
				setTitle(R.string.WAITING_FOR_PIN_REQUEST).
				setNegativeButton(android.R.string.cancel, this).
				setMessage(R.string.WAITING_FOR_PIN_REQUEST).
				create();
		   
			pairModeDialog.show();
			getBTSDApplication().getStateMachine().messageToServer(ServerMessages.getPincodeCancel());
			state = States.WAITING_FOR_PIN_REQUEST;
		}
	}
	
	private void handleConnectingToHIDHostOnClick(int which){
		
		if(which == DialogInterface.BUTTON2){
			
			getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getConnectToHostCancel());
			state = States.CONNECTING_TO_HOST_CANCEL;
			BTScrewDriverCallbackHandler.sendPauseAlert(this, 
				new BTScrewDriverAlert(R.string.HID_HOST_CONNECT_CANCEL, 
				false));
		}
	}
	
	private void handleHIDHostDialogOnClick(int which){
		if(which == DialogInterface.BUTTON1){
			
			if(selectedHIDHostIndex == 0){
				//user just selected to enter pair mode
				pairModeDialog =  new AlertDialog.Builder(this).
					setTitle(R.string.WAITING_FOR_PIN_REQUEST).
					setNegativeButton(android.R.string.cancel, this).
					setMessage(R.string.WAITING_FOR_PIN_REQUEST).
					create();
	    	   
				pairModeDialog.show();
				getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getPairMode());
				state = States.WAITING_FOR_PIN_REQUEST;
			}else{
				
				JSONObject hidHost = this.hidHosts.get(selectedHIDHostIndex - 1);
				
				String address = null;
				String displayName = null;
				try{
					address = hidHost.getString("address");
					if(hidHost.getString("name") != null){
						displayName = hidHost.getString("name"); 
					}else{
						displayName = hidHost.getString("address");
					}
				}catch(JSONException ex){
					//if we get here then the server is not sending the expected
					//info
					ex.printStackTrace();
					throw new RuntimeException(ex.getMessage(), ex);
				}
				
				connectingToHIDHostDialog = new AlertDialog.Builder(this).
					setTitle(R.string.HID_HOST_CONNECTING_TITLE).
					setNegativeButton(android.R.string.cancel, this).
					setMessage(getString(R.string.HID_HOST_CONNECT_MESSAGE) + 
						" " + displayName).
					create();
				connectingToHIDHostDialog.show();
				
				getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getConnectToHost(address));
				state = States.CONNECTING_TO_HOST;
			}
		}else{
			//user just selected an option
			selectedHIDHostIndex = which;
		}
	}
	
	@Override
	public void showCancelableDialog(int title, int message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Integer hidKeyCode = keyMapping.get(keyCode);
		
		if(hidKeyCode == null){
			//not a key we care about
			return super.onKeyDown(keyCode, event);
		}
		//Log.e(TAG, "keyCode: " + keyCode + " hidKeyCode: " + keyCode);
		getBTSDApplication().getStateMachine().messageToServer(
			ServerMessages.getKeyCode(hidKeyCode));
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.e(TAG, "onKeyUp keyCode: " + keyCode);
		//if(keyCode == KeyEvent.KEYCODE_BACK){
			return super.onKeyUp(keyCode, event);
		//}
		
		
		//return true;
	}

	private static enum States{
		
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
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	public void hideDialog() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showCancelableDialog(int title, String message) {
		throw new IllegalArgumentException("Implement me");
	}
	
	@Override
	public void showPinCodeDialog() {
		throw new IllegalArgumentException("Implement me");
	}
	
	@Override
    public String getPincode() {
    	throw new IllegalArgumentException("Implement me");
    }
	
	@Override
	public void refreshConfiguredRemotes() {
		throw new IllegalArgumentException("Implement me");
	}
	
	@Override
	public void returnToPreviousRemoteConfiguration() {
		throw new IllegalArgumentException("Implement me");
	}
	
	@Override
	public void selectConfiguredRemote(String name) {
		throw new IllegalArgumentException("Implement me");
	}
}
