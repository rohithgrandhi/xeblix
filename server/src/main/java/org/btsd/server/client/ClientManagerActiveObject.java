package org.btsd.server.client;

import java.io.IOException;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.btsd.server.messages.ClientManagerInitMessage;
import org.btsd.server.messages.Message;
import org.btsd.server.messages.NewClientConnectionMessage;
import org.btsd.server.messages.ServiceFailureMessage;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;

public class ClientManagerActiveObject extends ActiveThread {

	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.CLIENT_MANAGER_INIT){
			handleClientRequests((ClientManagerInitMessage)msg);
		}

	}

	private void handleClientRequests(ClientManagerInitMessage initMessage){
		
		String url = "btspp://localhost:1111;name=BTSDServer;authenticate=false;encrypt=false;master=true";
		
		System.out.println("Starting BTSD Server using URL:" + url);
		StreamConnectionNotifier streamConnNotifier = null;
		try{
			streamConnNotifier = (StreamConnectionNotifier)Connector.open( url);
		}catch(IOException ex){
			System.out.println("Failed to start the BTSD Server using URL: " + url);
			ex.printStackTrace();
			initMessage.getMainAO().addMessage(new ServiceFailureMessage(
					getClass().getSimpleName()));
			return;
		}
		
		while(true){
			
			
			StreamConnection connection= null;
			try{
				System.out.println("Waiting for clients to connect to the BTSD Server using URL:" + url);
				connection = streamConnNotifier.acceptAndOpen();
			}catch(IOException ex){
				System.out.println("Failed to start the BTSD Server using URL: " + url);
				ex.printStackTrace();
				initMessage.getMainAO().addMessage(new ServiceFailureMessage(
					getClass().getSimpleName()));
				return;
			}
			
			RemoteDevice dev = null;
			try{
				dev = RemoteDevice.getRemoteDevice(connection);
				System.out.println("Client conencted to BTSD Server. Remote address: " + 
					dev.getBluetoothAddress() + " name: " + dev.getFriendlyName(true));
			}catch(IOException ex){
				System.out.println("Failed to read the remote device information. URL: " + url);
				ex.printStackTrace();
				initMessage.getMainAO().addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
				return;
			}
			
			//got a new client, send back to main AO so a new ActiveObject can be created for it
			//System.out.println("Sending new client connection");
			initMessage.getMainAO().addMessage(new NewClientConnectionMessage(
				dev.getBluetoothAddress(), connection));
		}
	}
	
}
