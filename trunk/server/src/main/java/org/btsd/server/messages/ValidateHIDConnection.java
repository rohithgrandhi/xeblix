package org.btsd.server.messages;

import org.btsd.server.util.MessagesEnum;

public class ValidateHIDConnection implements Message {

	private static final long serialVersionUID = -2799107981448213444L;

	private String remtoeDeviceAddress;
	
	public ValidateHIDConnection(String remoteDeviceAddress){
		this.remtoeDeviceAddress = remoteDeviceAddress;
	}
	
	public String getRemoteDeviceAddress(){
		return remtoeDeviceAddress;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.VALIDATE_HID_CONNECT;
	}

}
