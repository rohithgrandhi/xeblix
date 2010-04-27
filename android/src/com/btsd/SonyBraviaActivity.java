package com.btsd;

import java.util.Date;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.btsd.util.MessagesEnum;

public class SonyBraviaActivity extends AbstractRemoteActivity {

	private static final String TAG = "Sony";
	
	private GestureDetector gestureDetector = null;
	
	private float lastY;
	private float lastX;
	private boolean newGesture;
	
	
	private static final int MIN_VOL_THRESHOLD = 40;
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		Log.e(TAG, "onRestart");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.e(TAG, "onDestroy");
		
		/* If you click back this will get called
		 * if(isFinishing()){
			Main.statemachine.shutdownStateMachine();
			Main.statemachine = null;
		}*/
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.e(TAG, "onPause");
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Log.e(TAG, "onResume");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e(TAG, "onCreate");
		
		setContentView(R.layout.sony_bravia);
		//gestureDetector = new GestureDetector(this, this);
		int[] buttons = new int[]{R.id.sonyChannelDown, R.id.sonyChannelUp, R.id.sonyDown, 
				R.id.sonyEnter, R.id.sonyLeft, R.id.sonyMenu, R.id.sonyMute, R.id.sonyPower, 
				R.id.sonyRight, R.id.sonyUp, R.id.sonyVolumeDown, R.id.sonyVolumeUp};
		for(int buttonId: buttons){
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		
		long id = BTScrewDriverStateMachine.getid();
		Log.e(TAG, "id: " + id + " onClick date: " + new Date());
		
		Button button = (Button)v;
		String command = null;
		switch (button.getId()) {
		case R.id.sonyChannelDown:
			command = "BTN_CHANNEL_dOWN";
			break;
		case R.id.sonyChannelUp:
			command = "BTN_CHANNEL_UP";
			break;
		case R.id.sonyDown:
			command = "BTN_DOWN";
			break;
		case R.id.sonyEnter:
			command = "KEY_ENTER";
			break;
		case R.id.sonyLeft:
			command = "BTN_LEFT";
			break;
		case R.id.sonyMenu:
			command = "BTN_MENU";
			break;
		case R.id.sonyMute:
			command = "BTN_MUTING";
			break;
		case R.id.sonyPower:
			command = "BTN_POWER";
			break;
		case R.id.sonyRight:
			command = "BTN_RIGHT";
			break;
		case R.id.sonyUp:
			command = "BTN_UP";
			break;
		case R.id.sonyVolumeDown:
			command = "BTN_VOLUME_DOWN";
			break;
		case R.id.sonyVolumeUp:
			command = "BTN_VOLUME_UP";
			break;
		default:
			break;
		}
		//Main.statemachine.sendCommand("SONY_12_RM-YD010",command, 3, id);
		getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getLIRCCommand("SONY_12_RM-YD010", command, 3));
	}
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}*/
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		// TODO Auto-generated method stub
		
	}
	
	//@Override
	public boolean onDown(MotionEvent e) {
		Log.e(TAG, "onDown");
		newGesture = true;
		return false;
	}
	
	//@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//Log.e(TAG, "onFling velocityX: " + velocityX + " velocityY: " + velocityY);
		//Main.bluetoothBroker.sendCommand("SONY_12_RM-YD010","BTN_VOLUME_UP", 3);
		return false;
	}
	
	//@Override
	public void onLongPress(MotionEvent e) {
		Log.e(TAG, "onLongPress");
	}
	
	//@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		/*Log.e(TAG, "onScroll: e1.x=" + e1.getX() + " e1.y=" + e1.getY() + 
			" e2.x=" + e2.getX() + " e2.y=" + e2.getY() + " distanceX: " + distanceX + 
			" distanceY: " + distanceY);*/
		
		//are we going up or down?
		//Log.e(TAG, "e2.getY() - lastY = " + (e2.getY() - lastY));
		
		if(newGesture){
			lastY = e1.getY();
			newGesture = false;
			Log.e(TAG, "newGesture");
		}
		
		if(e1.getY() - e2.getY() > 0 && lastY - e2.getY() > MIN_VOL_THRESHOLD){
			//Main.bluetoothBroker.sendCommand("SONY_12_RM-YD010","BTN_VOLUME_UP", 3);
			lastY = e2.getY();
		}else if(e2.getY() - e1.getY() > 0 && e2.getY() - lastY > MIN_VOL_THRESHOLD){
			//Main.bluetoothBroker.sendCommand("SONY_12_RM-YD010","BTN_VOLUME_DOWN", 3);
			lastY = e2.getY();
		}
		
		
		
		return false;
	}
	
	//@Override
	public void onShowPress(MotionEvent e) {
		Log.e(TAG, "onShowPress");
	}
	
	//@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.e(TAG, "onSingleTapUp");
		return false;
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
