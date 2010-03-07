package com.btsd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;

import com.btsd.util.ActiveThread;

public class ServerWriterActiveObject extends ActiveThread {

	private final BTScrewDriverStateMachine stateMachine;
	private final OutputStream outputStream;
	
	private static final int MESSAGE_TO_SERVER = 0;
	private static final int SERVER_DISCONNECT = 1;
	
	private static int MESSAGE_ID = 0;
	//private HashMap<Integer, MessageInfo> messagesWaitingForAck; 
	
	public ServerWriterActiveObject(BTScrewDriverStateMachine statemachine, OutputStream outputStream){
		this.stateMachine = statemachine;
		this.outputStream = outputStream;
		//this.messagesWaitingForAck = new HashMap<Integer, MessageInfo>();
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		switch (msg.arg1) {
		case MESSAGE_TO_SERVER:
			
			JSONObject toSend = (JSONObject)msg.obj;
			int messageId = MESSAGE_ID++;
			try{toSend.put(Main.MESSAGE_ID, messageId);}catch(JSONException ex){}
			try{
				outputStream.write(toSend.toString().getBytes());
			}catch(IOException ex){
				stateMachine.serverDisconnect();
			}
			
			/*this.messagesWaitingForAck.put(messageId, 
				new MessageInfo(toSend, new Date()));*/
			
			//wait a short bit before sending any more messages
			try{Thread.sleep(100);}catch(InterruptedException ex){}
			
			break;
		case SERVER_DISCONNECT:
			throw new ShutdownException();
		default:
			break;
		}
	}

	public void sendMessageToServer(JSONObject messageToServer){
		Message message = Message.obtain();
		message.arg1 = MESSAGE_TO_SERVER;
		message.obj = messageToServer;
		addMessage(message);
	}
	
	public void serverDisconnect(){
		Message message = Message.obtain();
		message.arg1 = SERVER_DISCONNECT;
		addMessage(message);
	}
	
	private static class MessageInfo{
		
		private JSONObject message;
		private Date timeSent;
		
		public MessageInfo(JSONObject message, Date timeSent){
			this.message = message;
			this.timeSent = timeSent;
		}

		public JSONObject getMessage() {
			return message;
		}

		public Date getTimeSent() {
			return timeSent;
		}
		
		
	}
}
