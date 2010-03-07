package org.btsd.server.client;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.btsd.server.messages.ClientDisconnectMessage;
import org.btsd.server.messages.ClientInitMessage;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.LIRCFromClientMessage;
import org.btsd.server.messages.Message;
import org.btsd.server.messages.ServiceFailureMessage;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
			
			//got a connection to a client, make sure we are connected to the primary host if
			//one exists
			btsdActiveObject.addMessage(new HIDConnectToPrimaryHostMessage());
			
			try{
				handleClient();
			}catch(IOException ex){
				System.out.println("Error handling client");
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
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
			int readBytes = inStream.read(bytes);
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
		HID("HID");
		
		private String name;
		
		ClientTargetsEnum(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
}
