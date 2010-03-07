package org.btsd.server.lirc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.LIRCFromClientMessage;
import org.btsd.server.messages.LircInit;
import org.btsd.server.messages.Message;
import org.btsd.server.messages.ServiceFailureMessage;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;

public class LIRCActiveObject extends ActiveThread {

	public static final int INIT_MESSAGE = 1;
	
	
	public static final int UNINITIALIZED = 0;
	public static final int CONNECTED_STATE = 1;
	
	private Socket lircSocket;
	private PrintStream lircOutputstream; 
	
	private int state = UNINITIALIZED;
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.LIRC_INIT){
			initializeSocket((LircInit)msg);
		}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
			
			LIRCFromClientMessage clientMessage = (LIRCFromClientMessage)msg;
			
			sendCommand(clientMessage);
		}else if(msg.getType() == MessagesEnum.SHUTDOWN){
			
			try{
				lircOutputstream.close();
				lircSocket.close();
			}catch(IOException ex){
				//just log the error
				ex.printStackTrace();
			}
		}
		
	}

	public void sendCommand(LIRCFromClientMessage clientMessage){
		
		try{
			lircOutputstream.write(clientMessage.getLircCommand().getBytes());
			
			clientMessage.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					clientMessage.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getSuccessResponse()));
		}catch(IOException ex){
			//ignore errors for now, just print them out
			ex.printStackTrace();
			clientMessage.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					clientMessage.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse("LIRC")));
		}
	}
	
	public void initializeSocket(LircInit lircInit){
		
		if(state != UNINITIALIZED){
			//invalid request for the current state
			return;
		}
		
		try{
			//TODO: switch to unix sockets
			lircSocket = new Socket("localhost", 8000);
		}catch(IOException ex){
			System.out.println("LIRC Initialization Failed. Failed to attach to the " +
				"LIRC server at port 8000");
			ex.printStackTrace();
			lircInit.getSender().addMessage(new ServiceFailureMessage(getClass().getSimpleName()));
			return;
		}
		
		try{
			lircOutputstream = new PrintStream(lircSocket.getOutputStream());
		}catch(IOException ex){
			System.out.println("LIRC Initialization Failed. Failed to get output stream");
			ex.printStackTrace();
			lircInit.getSender().addMessage(new ServiceFailureMessage(getClass().getSimpleName()));
			return;
		}
		
		state = CONNECTED_STATE;
		System.out.println("LIRC initialization successful.");
	}
	
}
