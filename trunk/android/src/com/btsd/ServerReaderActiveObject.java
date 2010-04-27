package com.btsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

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
				
				StringBuilder builder = new StringBuilder();
				
				byte[] buffer = new byte[1024];
				int read = buffer.length;
				String temp = " ";
				try{
					//the last char read must always be a "}", so if its not then we got more
					//to read (not using inputStream.available b/c it is buggy on some android phones)
					while(temp.charAt(temp.length()-1) != '}'){
						read = inputStream.read(buffer);
						if(read == -1){
							break;
						}
						
						temp = new String(buffer, 0, read);
						builder.append(temp);
					}
					
				}catch(IOException ex){
					stateMachine.serverDisconnect();
					//server is dead, kill the thread
					throw new ShutdownException();
				}
				
				if(read == -1){
					Log.e(TAG, "Failed to read from the server");
					throw new RuntimeException("Failed to read from the server");
				}
				
				JSONObject serverMessage = null;
				//the message is either JSONObject or Array. Most likely an object
				try{
					serverMessage = new JSONObject(builder.toString());
				}catch(JSONException ex){}
				
				if(serverMessage != null){
					Log.i(TAG, "ServerMessage length: " + serverMessage.toString().length());
					stateMachine.messageFromServer( serverMessage);
				}else{
					Log.e(TAG, "Unrecognized server message: " + builder.toString());
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
