package org.xeblix.server.bluez;

import java.io.IOException;

import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;

import org.xeblix.server.messages.HIDConnectionInitMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.ServiceFailureMessage;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;
import org.xeblix.server.util.ShutdownException;

import com.intel.bluetooth.BlueCoveConfigProperties;
import com.intel.bluetooth.BlueCoveImpl;

public final class BluetoothHIDSocketActiveObject extends ActiveThread {

	private L2CAPConnection connection;
	private L2CAPConnectionNotifier connectionNotifier;
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.HID_CONNECTION_INIT){
			
			try{
				HIDConnectionInitMessage message = (HIDConnectionInitMessage)msg;
				if(message.isServerMode()){
					initializeL2CapServer(message);
				}else{
					initializeL2CapConnect(message);
				}
			}catch(InterruptedException ex){
				
				closeConnections();
			}
		}else if(msg.getType() == MessagesEnum.SHUTDOWN){
			closeConnections();
		}
		

	}


	private void closeConnections() {
		if(connection != null){
			try{
				connection.close();
			}catch(Exception e){
				//ignore
			}
		}
		
		if(connectionNotifier != null){
			try{
				connectionNotifier.close();
			}catch(Exception e){
				//ignore
			}
		}
		throw new ShutdownException();
	}

	
	private void initializeL2CapConnect(HIDConnectionInitMessage initMessage) throws InterruptedException{
		/*
		 * //TODO: connect to host
		BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
		UUID hid = new UUID(0x1124);
		//String url = "btl2cap://" + hostAddress + ":" + hid;// + ";psm=" + 0x11;
		String url = "btl2cap://" + hostAddress + ":11";
		
		try{
			System.out.println("url:" + url);
			L2CAPConnection connection = (L2CAPConnection) Connector.open(url);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		 */
		BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
		
		String url = "btl2cap://" + initMessage.getHostAddress()+ ":" + initMessage.getPsm();
		try{
			System.out.println("Connecting to remote HID device using url: " + url);
			connection = (L2CAPConnection) Connector.open(url);
		}catch(IOException ex){
			System.out.println("Failed to open bluetooth l2cap port. URL: " + url);
			ex.printStackTrace();
			initMessage.getActiveThread().addMessage(new ServiceFailureMessage(
				getClass().getSimpleName()));
			return;
		}
		
		listenForMessages(initMessage, url, false);
	}
	
	private void initializeL2CapServer(HIDConnectionInitMessage initMessage) throws InterruptedException{
		
		
		BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
		
		UUID hid = new UUID(initMessage.getUuid());
		String url = "btl2cap://localhost:" + hid + ";bluecovepsm=" + initMessage.getPsm();
		
		try{
			connectionNotifier = (L2CAPConnectionNotifier) Connector.open(url);
		}catch(IOException ex){
			System.out.println("Failed to open bluetooth l2cap port. URL: " + url);
			ex.printStackTrace();
			initMessage.getActiveThread().addMessage(new ServiceFailureMessage(
					getClass().getSimpleName()));
			return;
		}
		
		try{
			System.out.println("Starting L2CAP Listener for URL: " + url);
			connection = connectionNotifier.acceptAndOpen();
		}catch(IOException ex){
			System.out.println("Failed to open bluetooth l2cap port. URL: " + url);
			ex.printStackTrace();
			initMessage.getActiveThread().addMessage(new ServiceFailureMessage(
				getClass().getSimpleName()));
			return;
		}
		
		listenForMessages(initMessage, url, true);
	}


	private void listenForMessages(HIDConnectionInitMessage initMessage,
			String url, boolean server) {
		//successfully created l2cap socket, send back result
		String address = null;
		String name = null;
		try{
			RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
			address = dev.getBluetoothAddress();
			name = dev.getFriendlyName(true);
			if(server){
				System.out.println("HID Host has successfuly connected to L2CAP Listener for URL: " + 
					url + ". Remote device: " + address + " name: " + 
					name);
			}else{
				System.out.println("Successfuly connected to HID Host using URL: " + 
					url + ". Remote device: " + address + " name: " +  name);
			}
		}catch(IOException ex){
			if(address == null){
				System.out.println("Failed to read the remote device information. URL: " + url);
				initMessage.getActiveThread().addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
				ex.printStackTrace();
				return;
			}
			
			if(name == null){
				System.out.println("Failed to read the remote device name. Got the address so will continue.. URL: " + url);
				name = "Unknown";
				ex.printStackTrace();
			}
		}
		
		initMessage.getActiveThread().addMessage(new HIDConnectionInitResultMessage(
			initMessage.getPsm(),initMessage.getUuid(), connection, address, name, server));
		
		while(true){
			
			byte[] bytes = new byte[1024];
			int readBytes = -1;
			try{
				System.out.println("L2CAP Connection for URL: " + url + " is waiting for data from HID Host");
				readBytes = connection.receive(bytes);
			}catch(IOException ex){
				//ex.printStackTrace();
				System.out.println("HID Host connected with URL: " + url + " has disconnected.");
				ex.printStackTrace();
				initMessage.getActiveThread().addMessage(new HIDHostDisconnect());
				break;
			}
			if(readBytes == -1){
				System.out.println("No data from HID Host for L2CAP connection for URL: " + url);
			}
			
			System.out.print("Received From HID Host on L2CAP Connection: " + url +  " : ");
			for(int i=0; i < readBytes; i++){
				System.out.print(bytes[i] + " ");
			}
			
			/*if(readBytes == 1 && bytes[0] == 0x71){
				System.out.println("Received set_protocol REPORT");
				try{
					connection.send(new byte[]{ (new Integer(0x03)).byteValue()});
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}*/
			System.out.println(".");
		}
	}
	
}
