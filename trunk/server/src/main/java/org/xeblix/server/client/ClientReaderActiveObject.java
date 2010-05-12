package org.xeblix.server.client;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.messages.ClientDisconnectMessage;
import org.xeblix.server.messages.ClientInitMessage;
import org.xeblix.server.messages.ConfigFromClientMessage;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.LIRCFromClientMessage;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.ServiceFailureMessage;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public class ClientReaderActiveObject extends ActiveThread {

	private ActiveThread btsdActiveObject;
	private String remoteDeviceAddress;
	private StreamConnection connection;
	private InputStream inStream=null;
	
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.CLIENT_INIT){
			
			ClientInitMessage clientInit = (ClientInitMessage)msg; 
			btsdActiveObject = clientInit.getActiveThread();
			remoteDeviceAddress = clientInit.getConnectionInfo().getAddress();
			connection = clientInit.getConnectionInfo().getConnection();
			
			System.out.println("ClientReader got CLIENT_INIT for Client: " + 
					remoteDeviceAddress);
			
			try{
				
				inStream = connection.openInputStream();
				
			}catch(IOException ex){
				System.out.println("Error getting input and/or output stream");
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ServiceFailureMessage(
					getClass().getSimpleName()));
			}
			
			try{
				handleClient();
			}catch(IOException ex){
				System.out.println("Error handling client");
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
			}
		}else if(msg.getType() == MessagesEnum.SHUTDOWN){
			if(inStream != null){
				try{
					inStream.close();
				}catch(IOException ex){}
			}
			
			if(connection !=null){
				try{connection.close();}catch(IOException ex){}
			}
		}else{
			System.out.println("Unknown message: " + msg.getType().getDescription());
		}
		
		//inStream.close();
        //outStream.close();
        //connection.close();
		
	}

	private void handleClient() throws IOException{
		
		while(true){
			//get the client's command
			byte[] bytes = new byte[256];
			int readBytes = -1;
			try{
				readBytes = inStream.read(bytes);
			}catch(Exception ex){
				System.out.println("Client disconnected");
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ClientDisconnectMessage(this.remoteDeviceAddress));
				throw new IllegalStateException("No data from client");
			}
			if(readBytes == -1){
				System.out.println("Client disconnected");
				//client disconnected
				btsdActiveObject.addMessage(new ClientDisconnectMessage(this.remoteDeviceAddress));
				throw new IllegalStateException("No data from client");
			}
			
			String clientString = new String(bytes, 0, readBytes);
			System.out.println("Client request:" + clientString);
			
			try{
				JSONObject jsonMessage = new JSONObject(clientString);
				handleMessage(jsonMessage);
			}catch(JSONException ex){
				//sometime will read more than one message at a time so check if 
				//the message can be read as a JSONArray
				try{
					JSONArray jsonMessages = new JSONArray("[" + clientString + "]");
					for(int i=0; i < jsonMessages.length(); i++){
						JSONObject jsonMessage = jsonMessages.getJSONObject(i);
						handleMessage(jsonMessage);
					}
				}catch(JSONException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	private void handleMessage(JSONObject clientString) {
		try{
			if("VersionRequest".equalsIgnoreCase(clientString.getString(FromClientResponseMessage.TYPE))){
				
				btsdActiveObject.addMessage(new FromClientResponseMessage(
					this.remoteDeviceAddress,HIDDeviceManagerHelper.getVersionRequest()));
			}else if("LIRCCommand".equalsIgnoreCase(clientString.getString(FromClientResponseMessage.TYPE))){
				
				try{
					btsdActiveObject.addMessage(new LIRCFromClientMessage(btsdActiveObject,
						this.remoteDeviceAddress, clientString));
				}catch(IllegalArgumentException ex){
					btsdActiveObject.addMessage(new FromClientResponseMessage(
							this.remoteDeviceAddress, HIDDeviceManagerHelper.getUnrecognizedCommand()));
				}
				
			}else if("HIDCommand".equalsIgnoreCase(clientString.getString(FromClientResponseMessage.TYPE))){
				
				try{
					btsdActiveObject.addMessage(new HIDFromClientMessage(btsdActiveObject,
						this.remoteDeviceAddress, clientString));
				}catch(IllegalArgumentException ex){
					btsdActiveObject.addMessage(new FromClientResponseMessage(
							this.remoteDeviceAddress, HIDDeviceManagerHelper.getUnrecognizedCommand()));
				}
			}else if("ConfigCommand".equalsIgnoreCase(clientString.getString(FromClientResponseMessage.TYPE))){
				
				try{
					btsdActiveObject.addMessage(new ConfigFromClientMessage(btsdActiveObject,
						this.remoteDeviceAddress));
				}catch(IllegalArgumentException ex){
					btsdActiveObject.addMessage(new FromClientResponseMessage(
							this.remoteDeviceAddress, HIDDeviceManagerHelper.getUnrecognizedCommand()));
				}
				
			}else{
				System.out.println("Invalid client request. Ignoring");
				btsdActiveObject.addMessage(new FromClientResponseMessage(
						this.remoteDeviceAddress, HIDDeviceManagerHelper.getUnrecognizedCommand()));
			}
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	public static enum ClientTargetsEnum{
		
		LIRC("LIRC"),
		HID("HID"),
		CONFIGURATION("CONFIG");
		
		private String name;
		
		ClientTargetsEnum(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
}
