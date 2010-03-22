package org.xeblix.server.messages;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.util.MessagesEnum;

public class PinConfirmationMessage implements Message {

	private static final long serialVersionUID = -6156480756715473405L;

	private final String message  = "PINCONFIRMATION_REQUEST";
	private final String pin;
	
	public PinConfirmationMessage(String pin){
		if(pin == null){
			throw new IllegalArgumentException("This method does not accept " +
				"null parameters.");
		}
		this.pin = pin;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.AUTH_AGENT_PIN_CONFIRMATION;
	}

	public JSONObject getMessage(){
		JSONObject toReturn = HIDDeviceManagerHelper.getResponse(message);
		try{
			toReturn.put(FromClientResponseMessage.PINCODE, pin);
		}catch(JSONException ex){}
		return toReturn;
	}
	
}
