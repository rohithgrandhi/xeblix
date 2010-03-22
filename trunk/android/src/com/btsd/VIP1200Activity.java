package com.btsd;

import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.btsd.util.MessagesEnum;

public class VIP1200Activity extends AbstractRemoteActivity {

	private static final String TAG = "VIP1200";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vip1200);
		
		int[] buttons = new int[]{R.id.vip1200Back,R.id.vip1200Down,R.id.vip1200Enter,
				R.id.vip1200ExitToTV,R.id.vip1200FastForward,R.id.vip1200Info,
				R.id.vip1200Left,R.id.vip1200Pause,R.id.vip1200Play,
				R.id.vip1200Power,R.id.vip1200RecordedTV,R.id.vip1200Rewind,
				R.id.vip1200Right,R.id.vip1200Stop,R.id.vip1200Up,R.id.vip1200VideoOnDemand,
				R.id.vip1200Zoom};
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
		case R.id.vip1200Back:
			command = "BACK";
			break;
		case R.id.vip1200Down:
			command = "DOWN";
			break;
		case R.id.vip1200Enter:
			command = "OK";
			break;
		case R.id.vip1200ExitToTV:
			command = "EXIT";
			break;
		case R.id.vip1200FastForward:
			command = "FF";
			break;
		case R.id.vip1200Info:
			command = "INFO";
			break;
		case R.id.vip1200Left:
			command = "LEFT";
			break;
		case R.id.vip1200Pause:
			command = "PAUSE";
			break;
		case R.id.vip1200Play:
			command = "PLAY";
			break;
		case R.id.vip1200Power:
			command = "POWER";
			break;
		case R.id.vip1200RecordedTV:
			command = "RECORDEDTV";
			break;
		case R.id.vip1200Rewind:
			command = "REW";
			break;
		case R.id.vip1200Right:
			command = "RIGHT";
			break;
		case R.id.vip1200Stop:
			command = "STOP";
			break;
		case R.id.vip1200Up:
			command = "UP";
			break;
		case R.id.vip1200VideoOnDemand:
			command = "VIDEOONDEMAND";
			break;
		case R.id.vip1200Zoom:
			command = "ENTER";
			break;
		default:
			break;
		}
		getBTSDApplication().getStateMachine().messageToServer(
			ServerMessages.getLIRCCommand("vip1200", command, 2));
	}
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		// TODO Auto-generated method stub
		
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
