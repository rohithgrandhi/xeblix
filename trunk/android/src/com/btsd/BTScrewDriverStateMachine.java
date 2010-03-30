package com.btsd;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;

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
import com.btsd.util.ActiveThread;
import com.btsd.util.MessagesEnum;

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

	//00:02:72:A0:BD:E5 (new)
	//00:02:72:15:9B:71 (original)
	//00:1B:DC:00:00:3F (marco sheeva)
	private static final String address = "00:02:72:A0:BD:E5";
	//private static final int port = 1;
	private static final UUID uuid = UUID.fromString("0006164b-0000-1000-8000-00805f9b34fb");
	
	@Override
	public void handleMessage(Message msg) {
		
		Log.d(TAG, "Received message with id: " + msg.arg1);
		
		if(msg.arg1 == MessagesEnum.ON_START.getId()){
			Context context = (Context)msg.obj;
			handleOnStart(context);
			
		}else if(msg.arg1 == MessagesEnum.REGISTER_ACTIVITY.getId()){
			this.currentActivity = (CallbackActivity) msg.obj;
		}else if(msg.arg1 == MessagesEnum.SERVER_DISCONNECT.getId()){
			disconnect();
		}else if(msg.arg1 == MessagesEnum.BT_CONNECTION_STATE.getId()){
			CallbackActivity callback = (CallbackActivity)msg.obj;
			handleOnStart((Context)callback);
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
			Context context = (Context)this.currentActivity;
			try{
				LocalBluetoothDevice device = LocalBluetoothDevice.initLocalDevice(context);
				device.close();
			}catch(Exception ex){
				//we are shutting down so ignore errors
			}
			throw new ShutdownException();
		}
		
	}

	private void disconnect() {
		if(serverWriter != null && serverWriter.isAlive()){
			serverWriter.serverDisconnect();
			serverWriter = null;
		}
		
		if(serverReader != null && serverReader.isAlive()){
			serverReader.interrupt();
			serverReader = null;
		}
		
		currentState = States.DISCONNECTED;
		
		BTScrewDriverCallbackHandler.BTConnectionStatus(currentActivity, currentState);
	}
	
	private void handleOnStart(Context context){
		
		Log.d(TAG, "Handling OnStart message. Current state: " + currentState.getName());
		if(currentState == States.DISCONNECTED || 
			currentState == States.CONNECTION_FAILED){
			//try to connect to bt server
			connectToBTServer(context);
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
	
	private void connectToBTServer(Context context){
		
		Log.i(TAG, "Connecting to Remote BT Device with address: " + address);
		
		Log.d(TAG, "Validating BluetoothState");
		device = BluetoothAccessor.getInstance().getBluetoothAdapter(context);
			
		if(!device.isEnabled()){
			currentState = States.BLUETOOTH_DISABLED;
			return;
		}
			
		
		remoteDevice = device.getRemoteDevice(address);
		remoteDevice.pair();
		
		try{
			BluetoothSocket socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);
			InputStream input = socket.getInputStream();
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
	
	
	/*private String sendMessageToRemoteDevice(String toSend) throws IOException {
		
		BluetoothSocket socket = null;
		InputStream input = null;
		OutputStream output = null;
		
		try{
			socket = remoteDevice.openSocket(port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
		}catch(Exception ex){
			throw new IOException(ex.getMessage());
		}
			
		//try{Thread.sleep(1000);}catch(Exception ex){}
	
		output.write(toSend.getBytes());
		
		//try{Thread.sleep(1000);}catch(Exception ex){}
		
		//make sure we get a response
		byte[] buffer = new byte[256];
		int read = input.read(buffer);
		
		if(read == -1){
			throw new RuntimeException("Failed to read from the server");
		}
		
		return  new String(buffer, 0, read);
	}
	
	private void sendMessageToRemoteDevice(String toSend, String expected) throws IOException {
		
		String serverResponse = sendMessageToRemoteDevice(toSend);
		if(!expected.equalsIgnoreCase(serverResponse)){
			throw new RuntimeException("Invalid version. Expecting " +
				expected + " got: " + serverResponse);
		}
	}*/
	
	public void onStart(Context context){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.ON_START.getId();
		message.obj = context;
		addMessage(message);
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
	
	/*public void sendCommand(String remote, String button, int count, long id){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.SEND_COMMAND.getId();
		message.obj = new Object[]{remote,button, count, id};
		addMessage(message);
	}
	
	public void sendCommand(String remote, int keyCode){
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.SEND_COMMAND.getId();
		message.obj = new Object[]{remote,keyCode, -1, -1};
		addMessage(message);
	}
	
	public void sendCommandForResult(CallbackActivity callbackActivity, String remote, 
		String serverCommand){
		
		Message message = Message.obtain();
		message.arg1 = MessagesEnum.SEND_COMMAND.getId();
		message.obj = new Object[]{remote,serverCommand, -1, -1, callbackActivity};
		addMessage(message);
	}
	*/
	
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
	
	public enum States{
		
		//states
		DISCONNECTED("Disconnected"),
		CONNECTED("Connected"),
		BLUETOOTH_DISABLED("Bluetooth Disabled"),
		CONNECTION_FAILED("Connection failed");
		/*private static final int BT_ENABLED_CHECK = 1;
		private static final int BT_NOT_ENABLED_ALERT = 2;
		private static final int CONNECTING_TO_BT_SERVER = 3;
		private static final int CONNECTING_TO_BT_SERVER_FAILED = 4;
		private static final int ACCEPT_REMOTE_COMMANDS = 5;
		*/
		
		private String name;
		
		States(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		
	}
}
