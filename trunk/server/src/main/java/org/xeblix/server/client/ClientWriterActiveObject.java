package org.xeblix.server.client;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import org.xeblix.server.messages.ClientInitMessage;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.ServiceFailureMessage;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public class ClientWriterActiveObject extends ActiveThread {

	private ActiveThread btsdActiveObject;
	private StreamConnection connection;
	private String remoteDeviceAddress;
	private OutputStream outStream = null;
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.CLIENT_INIT){
			
			ClientInitMessage clientInit = (ClientInitMessage)msg;
			btsdActiveObject = clientInit.getActiveThread();
			remoteDeviceAddress = clientInit.getConnectionInfo().getAddress();
			connection = clientInit.getConnectionInfo().getConnection();
			
			System.out.println("ClientWriter got CLIENT_INIT for Client: " + 
				remoteDeviceAddress);
			
			try{
				outStream = connection.openOutputStream();
			}catch(IOException ex){
				System.out.println("Error getting input output stream");
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
			}
			
			
		}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT_RESPONSE){
			
			FromClientResponseMessage responseMessage = (FromClientResponseMessage)msg;
			
			String messageToClient = responseMessage.getReponse();
			try{
				//let the client know we got a response
				if(messageToClient == null){
					outStream.write("Received".getBytes());
				}else{
					outStream.write(messageToClient.getBytes());
				}
			}catch(IOException ex){
				System.out.println("Error sending data to client. Message: " + messageToClient);
				ex.printStackTrace();
				btsdActiveObject.addMessage(new ServiceFailureMessage(
						getClass().getSimpleName()));
			}
			
		}
	}

}
