package org.xeblix.server.messages;

import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.util.MessagesEnum;

public class HIDHostCancelPinRequestMessage implements Message {

	private static final long serialVersionUID = 6930574072836478680L;
	
	private static final String MESSAGE = "HIDHostPinCancel";
	
	public MessagesEnum getType() {
		return MessagesEnum.AUTH_AGENT_HID_HOST_CANCEL_PIN_REQUEST;
	}

	public JSONObject getMessage(){
		return HIDDeviceManagerHelper.getResponse(MESSAGE);
	}
	
}
