package com.btsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.btsd.bluetooth.BluetoothAccessor;
import com.btsd.bluetooth.BluetoothAdapter;
import com.btsd.bluetooth.BluetoothDevice;
import com.btsd.bluetooth.BluetoothSocket;
import com.btsd.util.ActiveThread;
import com.btsd.util.MessagesEnum;
import com.btsd.util.Pair;

public class BTScrewDriverStateMachine extends ActiveThread{

	private static long id = 0;
	
	public static synchronized long getid(){
		return id++;
	}
	
	private static final String TAG = "BTScrewdriverStateMachine";
	
	
	//messages
	
	//will be null when the 
	private States currentState = States.DISCONNECTED;
	private CallbackActivity currentActivity;
	private BluetoothAdapter device;
	private BluetoothDevice remoteDevice;
	//private BTScrewDriverAlert cachedAlert;
	
	private ServerWriterActiveObject serverWriter;
	private ServerReaderActiveObject serverReader;
	private BluetoothSocket socket;
	private InputStream inputStream;

	//00:02:72:A0:BD:E5 (new)
	//00:02:72:15:9B:71 (original)
	//00:1B:DC:00:00:3F (marco sheeva)
	private static final String address = "00:02:72:A0:BD:E5";
	//private static final int port = 1;
	private static final UUID uuid = UUID.fromString("0006164b-0000-1000-8000-00805f9b34fb");
	
	@Override
	public void handleMessage(Message msg) {
		
		Log.d(TAG, "Received message with id: " + msg.arg1);
		
		if(msg.arg1 == MessagesEnum.REGISTER_ACTIVITY.getId()){
			this.currentActivity = (CallbackActivity) msg.obj;
		}else if(msg.arg1 == MessagesEnum.SERVER_DISCONNECT.getId()){
			disconnect();
		}else if(msg.arg1 == MessagesEnum.CONNECT_TO_SERVER.getId()){
			
			Pair<String,String> connectInfo = (Pair<String,String>)msg.obj;
			disconnect();
			handleOnStart(connectInfo.getRight());
			BTScrewDriverCallbackHandler.BTConnectionStatus(currentActivity, currentState);
		}else if(msg.arg1 == MessagesEnum.BT_CONNECTION_STATE.getId()){
			CallbackActivity callback = (CallbackActivity)msg.obj;
			//handleOnStart((Context)callback);
			BTScrewDriverCallbackHandler.BTConnectionStatus(callback, currentState);
		}else if(msg.arg1 == MessagesEnum.MESSAGE_FROM_SERVER.getId()){
			
			//forward to current activity (don't bother checking the state, we just
			//got a message from the server so must be connected
			BTScrewDriverCallbackHandler.messageFromServer(currentActivity, (JSONObject)msg.obj);
			
		}else if(msg.arg1 == MessagesEnum.MESSAGE_TO_SERVER.getId()){
			
			if(currentState == States.CONNECTED){
				serverWriter.sendMessageToServer((JSONObject)msg.obj);
			}
			
		}else if(msg.arg1 == MessagesEnum.SHUTDOWN.getId()){
			/*Context context = (Context)this.currentActivity;
			try{
				LocalBluetoothDevice device = LocalBluetoothDevice.initLocalDevice(context);
				device.close();
			}catch(Exception ex){
				//we are shutting down so ignore errors
			}*/
			throw new ShutdownException();
		}
		
	}

	private void disconnect() {
		
		if(socket != null){
			try{
				socket.close();
			}catch(IOException ex){
				Log.w(getClass().getSimpleName(), ex.getMessage());
			}
			socket = null;
		}
		
		if(serverWriter != null && serverWriter.isAlive()){
			serverWriter.serverDisconnect();
			serverWriter = null;
		}else{
			serverWriter = null;
		}
		
		//calling interrupt on serverReader while it is blocking has no effect.
		//closing the underlying inputStream allows the thread to stop
		if(inputStream != null){
			try{
				inputStream.close();
			}catch(Exception ex){
				Log.w(getClass().getSimpleName(), ex.getMessage());
			}
		}
		
		if(serverReader != null && serverReader.isAlive()){
			serverReader.interrupt();
			serverReader = null;
		}else{
			serverReader = null;
		}
		
		if(currentState == States.CONNECTED){
			//have the thread wait a short period of time. This gives the server some time
			//to clean up. This is useful when you disconnect from a server and immediately reconnect
			try{Thread.sleep(2000);}catch(Exception ex){}
		}
		currentState = States.DISCONNECTED;
		
		BTScrewDriverCallbackHandler.BTConnectionStatus(currentActivity, currentState);
	}
	
	private void handleOnStart(String address){
		
		Log.d(TAG, "Handling OnStart message. Current state: " + currentState.getName());
		if(currentState == States.DISCONNECTED || 
			currentState == States.CONNECTION_FAILED){
			//try to connect to bt server
			connectToBTServer(address);
		}else{
			Log.w(TAG, "Ignoring onStart event. Not in required " + States.DISCONNECTED.getName() + " state.");
		}
		/*validateBluetoothState();
		
		if(currentState == null){
			
		}else{
		
			switch(currentState){
			case BT_ENABLED_CHECK:
			case CONNECTING_TO_BT_SERVER:
			case ACCEPT_REMOTE_COMMANDS:
				validateBluetoothState();
				break;
			default:
				Log.e(TAG, "unknown state: " + currentState);
				break;
			}
		}*/
		
	}
	
	private void connectToBTServer(String address){
		
		Log.i(TAG, "Connecting to Remote BT Device with address: " + address);
		
		Log.d(TAG, "Validating BluetoothState");
		device = BluetoothAccessor.getInstance().getDefaultAdapter();
			
		if(!device.isEnabled()){
			currentState = States.BLUETOOTH_DISABLED;
			return;
		}
			
		try{
			remoteDevice = device.getRemoteDevice(address);
		}catch(Exception  ex){
			Log.e(TAG, ex.getMessage(), ex);
			currentState = States.CONNECTION_FAILED;
		}
		
		try{
			this.socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);
			BluetoothSocket socket = this.socket;
			try{
				socket.connect();
			}catch(IOException e){
				//not sure what the problem is but on android 2.1 the first connect fails
				//with a Connect refused error, but the second connect is successful
				if(e.getMessage().contains("Connection refused")){
					socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);
					socket.connect();
				}
			}
			InputStream input = socket.getInputStream();
			this.inputStream = input;
			OutputStream output = socket.getOutputStream();
			
			this.serverReader = new ServerReaderActiveObject(this, input);
			this.serverReader.start();
			this.serverReader.initMessage();
			
			this.serverWriter = new ServerWriterActiveObject(this, output);
			this.serverWriter.start();
			
			currentState = States.CONNECTED;
			
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage(), ex);
			currentState = States.CONNECTION_FAILED;
		}
	}
	
	public void registerActivity(CallbackActivity activity){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.REGISTER_ACTIVITY.getId();
		message.obj = activity;
		addMessage(message);
	}
	
	public void shutdownStateMachine(){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.SHUTDOWN.getId();
		addMessage(message);
	}
	
	public void serverDisconnect(){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.SERVER_DISCONNECT.getId();
		addMessage(message);
	}
	
	public void messageFromServer(JSONObject serverMessage){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.MESSAGE_FROM_SERVER.getId();
		message.obj = serverMessage;
		addMessage(message);
	}
	
	public void messageToServer(JSONObject serverMessage){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.MESSAGE_TO_SERVER.getId();
		message.obj = serverMessage;
		addMessage(message);
	}
	
	public void getBTConnectionState(CallbackActivity callback){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.BT_CONNECTION_STATE.getId();
		message.obj = callback;
		addMessage(message);
	}
	
	public void connectToServer(String name, String address){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.CONNECT_TO_SERVER.getId();
		message.obj = Pair.create(name, address);
		addMessage(message);
	}
	
	public enum States{
		
		//states
		DISCONNECTED("Disconnected"),
		CONNECTED("Connected"),
		BLUETOOTH_DISABLED("Bluetooth Disabled"),
		CONNECTION_FAILED("Connection failed");
		
		private String name;
		
		States(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		
	}
}
