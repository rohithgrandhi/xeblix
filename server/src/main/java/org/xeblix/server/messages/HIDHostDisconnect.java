package org.xeblix.server.messages;

import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.util.MessagesEnum;

public class HIDHostDisconnect implements Message {

	private static final long serialVersionUID = 110096401797357416L;

	private static final String MESSAGE = "HID_HOST_DISCONNECT";
	
	public MessagesEnum getType() {
		return MessagesEnum.HID_HOST_DISCONNECT;
	}

	public JSONObject getMessage(){
		return HIDDeviceManagerHelper.getResponse(MESSAGE); 
	}
	
}
