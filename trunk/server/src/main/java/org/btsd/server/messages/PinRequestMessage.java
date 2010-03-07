package org.btsd.server.messages;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.btsd.server.util.MessagesEnum;
import org.json.JSONObject;

public class PinRequestMessage implements Message {

	private static final long serialVersionUID = -3495427933218744720L;

	private String message  = "PINCODE_REQUEST";
	
	public MessagesEnum getType() {
		return MessagesEnum.AUTH_AGENT_PIN_REQUEST;
	}

	public JSONObject getMessage(){
		return HIDDeviceManagerHelper.getResponse(message);
	}
	
}
