package org.xeblix.server.messages;

import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.util.MessagesEnum;

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
