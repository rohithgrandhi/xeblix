package com.btsd;

import static com.btsd.BluetoothHIDActivity.getKeycode;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.btsd.util.MessagesEnum;

public class MCEActivity extends AbstractRemoteActivity {

	private static final String TAG = "MCE";
	
	private States state = States.INITIAL_STATE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mce);
		
		int[] buttons = new int[]{R.id.mceBack, R.id.mceChDown,R.id.mceChUp,
				R.id.mceDown,R.id.mceEnter,R.id.mceFF,R.id.mceLeft,
				R.id.mcePause,R.id.mcePlay,R.id.mceRight,R.id.mceRW,
				R.id.mceSKF,R.id.mceSKR,R.id.mceStart,R.id.mceStop,R.id.mceUp,
				R.id.mceVolDown,R.id.mceVolUp};
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
	public void onClick(View v) {
		
		long id = BTScrewDriverStateMachine.getid();
		Log.e(TAG, "id: " + id + " onClick date: " + new Date());
		
		Button button = (Button)v;
		ArrayList<Integer> commands = new ArrayList<Integer>(3);
		switch (button.getId()) {
		case R.id.mceBack:
			commands.add(getKeycode(KeyEvent.KEYCODE_DEL));
			break;
		case R.id.mceChDown:
			commands.add(getKeycode(KeyEvent.KEYCODE_EQUALS));
			break;
		case R.id.mceChUp:
			commands.add(getKeycode(KeyEvent.KEYCODE_MINUS));
			break;
		case R.id.mceDown:
			commands.add(getKeycode(KeyEvent.KEYCODE_DPAD_DOWN));
			break;
		case R.id.mceEnter:
			commands.add(getKeycode(KeyEvent.KEYCODE_ENTER));
			break;
		case R.id.mceFF:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_F));
			break;
		case R.id.mceLeft:
			commands.add(getKeycode(KeyEvent.KEYCODE_DPAD_LEFT));
			break;
		case R.id.mcePause:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_P));
			break;
		case R.id.mcePlay:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_P));
			break;
		case R.id.mceRight:
			commands.add(getKeycode(KeyEvent.KEYCODE_DPAD_RIGHT));
			break;
		case R.id.mceRW:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_B));
			break;
		case R.id.mceSKF:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_F));
			break;
		case R.id.mceSKR:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_B));
			break;
		case R.id.mceStart:
			commands.add(getKeycode(KeyEvent.KEYCODE_SEARCH));
			commands.add(getKeycode(KeyEvent.KEYCODE_ALT_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_ENTER));
			break;
		case R.id.mceStop:
			commands.add(getKeycode(BluetoothHIDActivity.CONTROL_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT));
			commands.add(getKeycode(KeyEvent.KEYCODE_S));
			break;
		case R.id.mceUp:
			commands.add(getKeycode(KeyEvent.KEYCODE_DPAD_UP));
			break;
		case R.id.mceVolDown:
			commands.add(getKeycode(BluetoothHIDActivity.F9));
			break;
		case R.id.mceVolUp:
			commands.add(getKeycode(BluetoothHIDActivity.F10));
			break;
		default:
			break;
		}
		
		getBTSDApplication().getStateMachine().messageToServer(
			ServerMessages.getKeycodes(commands));
		
	}

	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		
		if(messagesEnum  == MessagesEnum.MESSAGE_FROM_SERVER){
			try{
				handleMessageFromServer(message.toString());
			}catch(JSONException ex){
				BTScrewDriverAlert alert = new BTScrewDriverAlert(
					R.string.BT_SERVER_COMM_FAILURE , true);
				alert.showAlert(this);
			}
		}
	}
	
	private void handleMessageFromServer(String message) throws JSONException{
		
		if(state == States.WAITING_FOR_STATUS_RESPONSE){
			JSONObject serverMessage = new JSONObject(new JSONTokener(message));
			
			BTScrewDriverCallbackHandler.cancelAlert(this);
			
			String status = (String)serverMessage.get("status");
			if("disconnected".equals(status)){
				//ok not connected, get a list of possible hid hosts
				BTScrewDriverCallbackHandler.sendErrorAlert(this, new BTScrewDriverAlert(
						R.string.MCE_NOT_CONNECTED, true));
				state = States.INITIAL_STATE;
				
			}else if("connected".equalsIgnoreCase(status)){
				BTScrewDriverCallbackHandler.cancelAlert(this);
				state = States.CONNECTED;
			}else{
				throw new IllegalArgumentException("Unknow response");
			}
		}
	}
	
	private static enum States{
		
		INITIAL_STATE,
		WAITING_FOR_STATUS_RESPONSE,
		CONNECTED;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		throw new IllegalArgumentException("Implement me");
		
	}
	
	@Override
	public void showPinCodeDialog() {
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
