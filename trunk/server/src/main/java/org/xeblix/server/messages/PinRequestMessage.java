package org.xeblix.server.messages;

import org.json.JSONObject;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManagerHelper;
import org.xeblix.server.util.MessagesEnum;

public class PinRequestMessage implements Message {

	private static final long serialVersionUID = -3495427933218744720L;

	private String message  = "PINCODE_REQUEST";
	private String deviceIdentifier = null;
	private boolean clientMessage = false;
	private DeviceInfo info;
	
	public PinRequestMessage(String deviceIdentifier){
		this.deviceIdentifier = deviceIdentifier;
		clientMessage = false;
	}
	
	public PinRequestMessage(DeviceInfo info){
		this.info = info;
		clientMessage = true;
	}
	
	public boolean isClientMessage() {
		return clientMessage;
	}
	
	public String getHostName() {
		return info.getName();
	}

	public String getAddress() {
		return info.getName();
	}

	public MessagesEnum getType() {
		return MessagesEnum.AUTH_AGENT_PIN_REQUEST;
	}

	public JSONObject getMessage(){
		return HIDDeviceManagerHelper.getResponse(message);
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}
	
}
