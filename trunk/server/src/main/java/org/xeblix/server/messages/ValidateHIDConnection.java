package org.xeblix.server.messages;

import org.xeblix.server.util.MessagesEnum;

public class ValidateHIDConnection implements Message {

	private static final long serialVersionUID = -2799107981448213444L;

	private String remoteDeviceAddress;
	
	public ValidateHIDConnection(){
	}
	
	public ValidateHIDConnection(String remoteDeviceAddress){
		this.remoteDeviceAddress = remoteDeviceAddress;
	}
	
	public String getRemoteDeviceAddress(){
		return remoteDeviceAddress;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.VALIDATE_HID_CONNECT;
	}

}
