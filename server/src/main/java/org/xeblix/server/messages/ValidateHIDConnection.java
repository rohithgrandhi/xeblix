package org.xeblix.server.messages;

import org.xeblix.server.util.MessagesEnum;

public class ValidateHIDConnection implements Message {

	private static final long serialVersionUID = -2799107981448213444L;

	private String remoteDeviceAddress;
	//time in ms when a validation is considered failed
	private long failedTime;
	
	public ValidateHIDConnection(long failedTime){
		this.failedTime = failedTime;
	}
	
	public ValidateHIDConnection(String remoteDeviceAddress, long failedTime){
		this.remoteDeviceAddress = remoteDeviceAddress;
		this.failedTime = failedTime;
	}
	
	public String getRemoteDeviceAddress(){
		return remoteDeviceAddress;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.VALIDATE_HID_CONNECT;
	}

	public long getFailedTime() {
		return failedTime;
	}
	
}
