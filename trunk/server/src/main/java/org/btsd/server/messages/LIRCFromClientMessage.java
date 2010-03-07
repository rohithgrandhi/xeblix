package org.btsd.server.messages;

import org.btsd.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.btsd.server.util.ActiveThread;
import org.json.JSONException;
import org.json.JSONObject;

public class LIRCFromClientMessage extends FromClientMessage {

	private static final long serialVersionUID = 1140404070905618310L;
	
	private String lircCommand;
	
	public LIRCFromClientMessage(ActiveThread btsdActiveObject, String remtoeDeviceAddress, 
		JSONObject clientString){
		super(ClientTargetsEnum.LIRC, remtoeDeviceAddress,btsdActiveObject);
		
		try{
			StringBuilder fullCommand = new StringBuilder("SEND_ONCE ").
				append(clientString.getString(FromClientResponseMessage.REMOTE)).
				append(" ").
				append(clientString.getString(FromClientResponseMessage.KEY_CODES)).
				append(" ").
				append(clientString.getString(FromClientResponseMessage.SEND_COUNT)).
				append("\n");
			
			lircCommand = fullCommand.toString();
		}catch(JSONException ex){
			throw new IllegalArgumentException("Expecting remote and key_codes and send " +
				"count messages from the client.");
		}
	}

	public String getLircCommand() {
		return lircCommand;
	}
	
}
