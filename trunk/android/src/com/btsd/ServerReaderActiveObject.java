package com.btsd;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.btsd.util.ActiveThread;

public class ServerReaderActiveObject extends ActiveThread {

	private static final String TAG = ServerReaderActiveObject.class.getSimpleName();
	
	private final BTScrewDriverStateMachine stateMachine;
	private final InputStream inputStream;
	
	private static final int INIT = 0;
	
	public ServerReaderActiveObject(BTScrewDriverStateMachine statemachine, InputStream inputStream){
		this.stateMachine = statemachine;
		this.inputStream = inputStream;
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		switch (msg.arg1) {
		case INIT:
			
			while(true){
				
				byte[] buffer = new byte[1024];
				int read = -1;
				try{
					read = inputStream.read(buffer);
				}catch(IOException ex){
					stateMachine.serverDisconnect();
					//server is dead, kill the thread
					throw new ShutdownException();
				}
				
				if(read == -1){
					Log.e(TAG, "Failed to read from the server");
					throw new RuntimeException("Failed to read from the server");
				}
				
				String message = new String(buffer, 0, read);
				JSONObject serverMessage = null;
				//the message is either JSONObject or Array. Most likely an object
				try{
					serverMessage = new JSONObject(message);
				}catch(JSONException ex){}
				
				if(message != null){
					stateMachine.messageFromServer( serverMessage);
				}else{
					Log.e(TAG, "Unrecognized server message: " + message);
				}
			}
			
			//break;
		default:
			break;
		}

	}

	
	public void initMessage(){
		
		Message message = Message.obtain();
		message.arg1 = INIT;
		addMessage(message);
		
	}
}
