package org.btsd.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.btsd.server.bluez.DBusManagerImpl;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager;
import org.btsd.server.bluez.hiddevicemanager.HIDFactoryImpl;
import org.btsd.server.client.ClientManagerActiveObject;
import org.btsd.server.client.ClientReaderActiveObject;
import org.btsd.server.client.ClientWriterActiveObject;
import org.btsd.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.btsd.server.lirc.LIRCActiveObject;
import org.btsd.server.messages.ClientDisconnectMessage;
import org.btsd.server.messages.ClientInitMessage;
import org.btsd.server.messages.ClientManagerInitMessage;
import org.btsd.server.messages.FromClientMessage;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDHostCancelPinRequestMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.LircInit;
import org.btsd.server.messages.Message;
import org.btsd.server.messages.NewClientConnectionMessage;
import org.btsd.server.messages.PinRequestMessage;
import org.btsd.server.messages.ServiceFailureMessage;
import org.btsd.server.messages.ShutdownMessage;
import org.btsd.server.messages.StartupMessage;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;
import org.btsd.server.util.ShutdownException;

public class BluetoothScrewdriverServer {

	private static final DBusManagerImpl dbusManager = new DBusManagerImpl();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//start the main Manager ActiveObject
		ActiveThread managerActiveThread = new ActiveThread(){
			
			private List<ActiveThread> activeThreads = new ArrayList<ActiveThread>();
			
			private Map<String, ActiveThread> clientReaders = new HashMap<String, ActiveThread>();
			private Map<String, ActiveThread> clientWriters = new HashMap<String, ActiveThread>();
			
			
			private LIRCActiveObject lircActiveObject;
			//private BluetoothHIDSocketActiveObject inputHIDSocketActiveObject;
			//private BluetoothHIDSocketActiveObject controlHIDSocketActiveObject;
			private ClientManagerActiveObject clientManagerActiveObject;
			//private BTHIDWriterActiveObject btHIDWriterActiveObject;
			private HIDDeviceManager hidDeviceManager;
			
			private ActiveThread hidHost;
			
			@Override
			public void handleMessage(Message msg) {
				
				if(msg.getType() == MessagesEnum.STARTUP){
					
					System.out.println("Starting services...");
					
					//start with lirc
					lircActiveObject = new LIRCActiveObject();
					activeThreads.add(lircActiveObject);
					lircActiveObject.start();
					lircActiveObject.addMessage(new LircInit(this));
					
					/*inputHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
					activeThreads.add(inputHIDSocketActiveObject);
					inputHIDSocketActiveObject.start();
					inputHIDSocketActiveObject.addMessage(new Message(MessagesEnum.HID_INIT, 
							new BluetoothHIDSocketActiveObject.InitMessage(this, 13, 3453459)));
					
					controlHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
					activeThreads.add(controlHIDSocketActiveObject);
					controlHIDSocketActiveObject.start();
					controlHIDSocketActiveObject.addMessage(new Message(MessagesEnum.HID_INIT, 
							new BluetoothHIDSocketActiveObject.InitMessage(this, 11, 3153189)));
					*/
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
					
				}else if(msg.getType() == MessagesEnum.HID_CONNECT_TO_PRIMARY_HOST){
					
					hidDeviceManager.addMessage(msg);
					
				}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
					
					FromClientMessage clientMessage = (FromClientMessage)msg;
										
					ActiveThread targetThread = null;
					
					if(ClientTargetsEnum.LIRC == clientMessage.getTarget()){
						
						targetThread = lircActiveObject;
						
					}else if(ClientTargetsEnum.HID == clientMessage.getTarget()){
						
						targetThread = hidDeviceManager;
					}else{
						throw new IllegalArgumentException("Unknown target: " + clientMessage.getTarget());
					}
					
					targetThread.addMessage(msg);
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
					
				}else if(msg.getType() == MessagesEnum.AUTH_AGENT_PIN_REQUEST){
					
					//for now send pin request to all clients
					PinRequestMessage message  = (PinRequestMessage)msg;
					for(String address: clientWriters.keySet()){
						ActiveThread clientWriter = clientWriters.get(address);
						clientWriter.addMessage(new FromClientResponseMessage(address, 
								message.getMessage()));
					}
				}else if(msg.getType() == MessagesEnum.AUTH_AGENT_HID_HOST_CANCEL_PIN_REQUEST){
					
					//send the cancel pin request to all clients
					HIDHostCancelPinRequestMessage message  = (HIDHostCancelPinRequestMessage)msg;
					for(String address: clientWriters.keySet()){
						ActiveThread clientWriter = clientWriters.get(address);
						clientWriter.addMessage(new FromClientResponseMessage(address, 
								message.getMessage()));
					}
					
				}else if(msg.getType() == MessagesEnum.HID_HOST_DISCONNECT){
					
					HIDHostDisconnect message  = (HIDHostDisconnect)msg;
					for(String address: clientWriters.keySet()){
						ActiveThread clientWriter = clientWriters.get(address);
						clientWriter.addMessage(new FromClientResponseMessage(address, 
								message.getMessage()));
					}
					
				}
				/*else if(msg.getType() == MessagesEnum.HID_INIT_RESULT){
					
					ResultMessage hidConnection = (ResultMessage)msg.getPayload();
					if(hidConnection.getUuid() == 3453459){
						//got the input connection
						if(btHIDWriterActiveObject != null){
							//TODO: in the future will need to support multiple Bluetooth HID connections
							System.out.println("Failed to setup BluetoothHIDWriter ActiveObject. " +
								"A BluetoothHIDWriter is already setup.");
						}else{
							btHIDWriterActiveObject = new BTHIDWriterActiveObject();
							activeThreads.add(btHIDWriterActiveObject);
							btHIDWriterActiveObject.start();
							btHIDWriterActiveObject.addMessage(new Message(MessagesEnum.HID_INIT, 
 								hidConnection));
						}
					}
					
				}*/else{
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

	
	
}
