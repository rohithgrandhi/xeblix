package org.xeblix.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.DBusManagerImpl;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManager;
import org.xeblix.server.bluez.hiddevicemanager.HIDFactoryImpl;
import org.xeblix.server.client.ClientManagerActiveObject;
import org.xeblix.server.client.ClientReaderActiveObject;
import org.xeblix.server.client.ClientWriterActiveObject;
import org.xeblix.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.xeblix.server.configuration.RemoteConfigurationParser;
import org.xeblix.server.lirc.LIRCActiveObject;
import org.xeblix.server.messages.ClientDisconnectMessage;
import org.xeblix.server.messages.ClientInitMessage;
import org.xeblix.server.messages.ClientManagerInitMessage;
import org.xeblix.server.messages.FromClientMessage;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.LircInit;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.NewClientConnectionMessage;
import org.xeblix.server.messages.PinConfirmationMessage;
import org.xeblix.server.messages.PinRequestMessage;
import org.xeblix.server.messages.ServiceFailureMessage;
import org.xeblix.server.messages.ShutdownMessage;
import org.xeblix.server.messages.StartupMessage;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;
import org.xeblix.server.util.ShutdownException;

public class XeblixServer {

	private static final DBusManagerImpl dbusManager = new DBusManagerImpl();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//parse the configuration file
		final JSONArray remoteConfiguration = readRemoteConfiguration(); 
		
		//start the main Manager ActiveObject
		ActiveThread managerActiveThread = new ActiveThread(){
			
			private List<ActiveThread> activeThreads = new ArrayList<ActiveThread>();
			
			private Map<String, ActiveThread> clientReaders = new HashMap<String, ActiveThread>();
			private Map<String, ActiveThread> clientWriters = new HashMap<String, ActiveThread>();
			
			
			private LIRCActiveObject lircActiveObject;
			private ClientManagerActiveObject clientManagerActiveObject;
			private HIDDeviceManager hidDeviceManager;
			
			@Override
			public void handleMessage(Message msg) {
				
				if(msg.getType() == MessagesEnum.STARTUP){
					
					System.out.println("Starting services...");
					
					//start with lirc
					lircActiveObject = new LIRCActiveObject();
					activeThreads.add(lircActiveObject);
					lircActiveObject.start();
					lircActiveObject.addMessage(new LircInit(this));
					
					hidDeviceManager = new HIDDeviceManager(dbusManager, this, new HIDFactoryImpl());
					activeThreads.add(hidDeviceManager);
					hidDeviceManager.start();
					System.out.println("HIDDeviceManager started");
					
					clientManagerActiveObject = new ClientManagerActiveObject();
					activeThreads.add(clientManagerActiveObject);
					clientManagerActiveObject.start();
					clientManagerActiveObject.addMessage(new ClientManagerInitMessage(this));
					
				}else if(msg.getType() == MessagesEnum.SERVICE_FAILURE){
					
					ServiceFailureMessage message = (ServiceFailureMessage)msg;
					
					System.out.println("Failure in service: " + message.getFailedService() + 
						". Shutting down");
					ShutdownMessage shutdown = new ShutdownMessage();
					for(ActiveThread activeThread: activeThreads){
						activeThread.addMessage(shutdown);
					}
					
					//give services some time to shutdown
					try{sleep(10000);}catch(InterruptedException ex){}
					//stops this thread
					throw new ShutdownException();
					
				}else if(msg.getType() == MessagesEnum.NEW_CLIENT_CONNECTION){
					
					NewClientConnectionMessage connectionInfo = (NewClientConnectionMessage)msg; 
					
					ClientReaderActiveObject clientActiveObject = new ClientReaderActiveObject();
					clientReaders.put(connectionInfo.getAddress(), clientActiveObject);
					activeThreads.add(clientActiveObject);
					clientActiveObject.start();
					
					ClientInitMessage clientInit = new ClientInitMessage(this, connectionInfo);
					clientActiveObject.addMessage(clientInit);
					
					ClientWriterActiveObject clientWriterAO = new ClientWriterActiveObject();
					clientWriters.put(connectionInfo.getAddress(), clientWriterAO);
					activeThreads.add(clientWriterAO);
					clientWriterAO.start();
					clientWriterAO.addMessage(clientInit);
					
					
				}else if(msg.getType() == MessagesEnum.CLIENT_DISCONNECT){
					
					ClientDisconnectMessage disconnectMessage = (ClientDisconnectMessage)msg;
					if(clientReaders.remove(disconnectMessage.getRemoteDeviceAddress()) != null){
						System.out.println("Removed client with address: " + disconnectMessage.
							getRemoteDeviceAddress()+ " from ClientReaders");
					}else{	
						System.out.println("Failed to remove client with address: " + disconnectMessage.
								getRemoteDeviceAddress()+ " from ClientReaders");
					}
					
					if(clientWriters.remove(disconnectMessage.getRemoteDeviceAddress()) != null){
						System.out.println("Removed client with address: " + disconnectMessage.
							getRemoteDeviceAddress()+ " from ClientWriters.");
					}else{
						System.out.println("Faield to remove client with address: " + disconnectMessage.
								getRemoteDeviceAddress()+ " from ClientWriters.");
					}
					
				}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
					
					FromClientMessage clientMessage = (FromClientMessage)msg;
										
					ActiveThread targetThread = null;
					
					if(ClientTargetsEnum.LIRC == clientMessage.getTarget()){
						
						targetThread = lircActiveObject;
						targetThread.addMessage(msg);
					}else if(ClientTargetsEnum.HID == clientMessage.getTarget()){
						
						targetThread = hidDeviceManager;
						targetThread.addMessage(msg);
					}else if(ClientTargetsEnum.CONFIGURATION == clientMessage.getTarget()){
						ActiveThread clientWriter = clientWriters.get(clientMessage.getRemoteDeviceAddress());
						JSONObject clientResponse = new JSONObject();
						try{
							clientResponse.put(FromClientResponseMessage.TYPE, "RemoteConfiguration");
							clientResponse.put("remoteConfiguration", remoteConfiguration);
						}catch(JSONException ex){
							throw new RuntimeException(ex.getMessage(), ex);
						}
						clientWriter.addMessage(new FromClientResponseMessage(clientResponse));
						
					}else{
						throw new IllegalArgumentException("Unknown target: " + clientMessage.getTarget());
					}
				}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT_RESPONSE){
					
					FromClientResponseMessage responseMessage = (FromClientResponseMessage)msg;
					
					//if the address is null its a broadcast message to all clients
					if(responseMessage.isBroadcastMessage()){
						for(String address: clientWriters.keySet()){
							ActiveThread clientWriter = clientWriters.get(address);
							clientWriter.addMessage(msg);
						}
					}else{
						ActiveThread clientWriterAO = clientWriters.get(responseMessage.getRemoteDeviceAddress());
						if(clientWriterAO != null){
							clientWriterAO.addMessage(msg);
						}else{
							System.out.println("Can't find client with address: " + 
								responseMessage.getRemoteDeviceAddress());
						}
					}
				}else if(msg.getType() == MessagesEnum.AUTH_AGENT_PIN_CONFIRMATION){
					
					//let the HIDDeviceManager know if it gets two connections its connected
					hidDeviceManager.addMessage(msg);
					
					//send confirmation to all clients
					PinConfirmationMessage message  = (PinConfirmationMessage)msg;
					for(String address: clientWriters.keySet()){
						ActiveThread clientWriter = clientWriters.get(address);
						clientWriter.addMessage(new FromClientResponseMessage(address, 
								message.getMessage()));
					}
					
				}else if(msg.getType() == MessagesEnum.AUTH_AGENT_PIN_REQUEST){
					
					//for now send pin request to all clients
					PinRequestMessage message  = (PinRequestMessage)msg;
					if(message.isClientMessage()){
						for(String address: clientWriters.keySet()){
							ActiveThread clientWriter = clientWriters.get(address);
							clientWriter.addMessage(new FromClientResponseMessage(address, 
									message.getMessage()));
						}
					}else{
						hidDeviceManager.addMessage(msg);
					}
				}else if(msg.getType() == MessagesEnum.AUTH_AGENT_HID_HOST_CANCEL_PIN_REQUEST){
					
					hidDeviceManager.addMessage(msg);
					
				}else if(msg.getType() == MessagesEnum.HID_HOST_DISCONNECT){
					
					HIDHostDisconnect message  = (HIDHostDisconnect)msg;
					for(String address: clientWriters.keySet()){
						ActiveThread clientWriter = clientWriters.get(address);
						clientWriter.addMessage(new FromClientResponseMessage(address, 
								message.getMessage()));
					}
					
				}else{
					System.out.println("Unknown command: " + msg.getType().getDescription());
				}
			}
		};
		managerActiveThread.setDaemon(false);
		managerActiveThread.start();

		//before sending the startup signal registering the bluez agent
		dbusManager.registerAgent(managerActiveThread);
		
		//now register the SDP record
		dbusManager.registerSDPRecord();
		//dbusManager.setDeviceDiscoverable();
		
		//finally set the whole thing in motion
		managerActiveThread.addMessage(new StartupMessage());
		
	}

	private static JSONArray readRemoteConfiguration(){
		
		File file = new File("RemoteConfiguration");
		
		FileReader fileReader = null;
		try{
			
			fileReader = new FileReader(file);
			StringBuilder fileContents = new StringBuilder();
			char[] buffer = new char[1000];
			int charsRead = 0;
			while( (charsRead = fileReader.read(buffer)) != -1 ){
				fileContents.append(new String(buffer, 0, charsRead));
			}
			
			//System.out.println(fileContents.toString());
			JSONArray remoteConfiguration = new JSONArray(fileContents.toString());
			//got the configuration as JSONArray, now parse the jsonArray to verify
			//configuration is valid
			for(int i=0; i < remoteConfiguration.length(); i++){
				
				RemoteConfigurationParser.parseRemoteConfiguration(
					remoteConfiguration.getJSONObject(i));
			}
			
			return remoteConfiguration;
			
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}finally{
			if(fileReader != null){
				try{fileReader.close();}catch(IOException ex){ex.printStackTrace();}
			}
		}
	}
	
}
