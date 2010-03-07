package org.btsd.server.bluez;

import java.io.IOException;

import javax.bluetooth.L2CAPConnection;

import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDInitMessage;
import org.btsd.server.messages.Message;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;
import org.btsd.server.util.ShutdownException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BTHIDWriterActiveObject extends ActiveThread {

	private L2CAPConnection connection;
	private long uuid;
	private static final byte ZERO = (new Integer(0x00)).byteValue();
	
	@Override
	public void handleMessage(Message msg) {
		
		//HID_INIT is not the right messageEnum
		if(msg.getType() == MessagesEnum.HID_INIT){
			HIDInitMessage message = (HIDInitMessage)msg;
			connection = message.getConnection();
			uuid = message.getUuid();
		}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
			HIDFromClientMessage clientMessage = (HIDFromClientMessage)msg;
			try{
				sendCommand(clientMessage);
			}catch(JSONException ex){
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}else if(msg.getType() == MessagesEnum.SHUTDOWN){
			if(connection != null){
				try{connection.close();}catch(IOException ex){}
			}
			connection = null;
			throw new ShutdownException();
		}

	}

	private void sendCommand(HIDFromClientMessage clientMessage) throws JSONException{
		
		byte zero = BTHIDWriterActiveObject.ZERO;
		byte[] commands = new byte[]{zero,zero,zero,zero,zero,zero};
		JSONObject clientArguments = clientMessage.getClientArguments();
		JSONArray arguments = clientArguments.getJSONArray(
			FromClientResponseMessage.KEY_CODES);
		//max length is 6 (maximum of 6 key presses)
		int length = 6;
		if(length > arguments.length()){
			length = arguments.length();
		}
		for(int i=0; i < length; i++){
			commands[i] = new Integer(arguments.getString(i)).byteValue() ;
		}
		try{
			
			/*if(arguments[2].equals("4")){
				System.out.println("blah0");
				connection.send(new byte[]{ 
						(new Integer(0xa1)).byteValue(), //a1 01 == Collection (Application)
						(new Integer(0x02)).byteValue(), 
						(new Integer(0x02)).byteValue(), //buttons - 0x01 - left, 0x02 - right, 0x04 - middle, 0x08 - side, 0x10 - extra
						(new Integer(0x00)).byteValue(), //move x
						(new Integer(0x00)).byteValue(), //move y
						(new Integer(0x00)).byteValue()}); //wheel
				
			}else{
			*/connection.send(new byte[]{ 
					(new Integer(0xa1)).byteValue(), //a1 01 == Collection (Application)
					(new Integer(0x01)).byteValue(), 
					(new Integer(0x00)).byteValue(), //modifiers
					(new Integer(0x00)).byteValue(), //reserve byte
					commands[0],
					commands[1],
					commands[2],
					commands[3],
					commands[4],
					commands[5]});
			
			connection.send(new byte[]{ 
					(new Integer(0xa1)).byteValue(), 
					(new Integer(0x01)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue(),
					(new Integer(0x00)).byteValue()});
			//}
			
			JSONObject response = new JSONObject();
			try{
				response.put(FromClientResponseMessage.TYPE, "result");
				response.put(FromClientResponseMessage.VALUE, "SUCCESS");
			}catch(JSONException ex){}
			clientMessage.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				clientMessage.getRemoteDeviceAddress(), response));
			
			//command.getClientActiveObject().addMessage(
				//new Message(MessagesEnum.MESSAGE_FROM_CLIENT_RESULT, true));
		}catch(IOException ex){
			//ignore errors for now, just print them out
			ex.printStackTrace();
			JSONObject response = new JSONObject();
			try{
				response.put(FromClientResponseMessage.TYPE, "result");
				response.put(FromClientResponseMessage.VALUE, "FAILED");
			}catch(JSONException e){}
			clientMessage.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					clientMessage.getRemoteDeviceAddress(), response));
		}
		
	}
	
}
