package org.xeblix.server.messages;

import org.json.JSONObject;
import org.xeblix.server.util.MessagesEnum;

public class FromClientResponseMessage implements Message {

	private static final long serialVersionUID = -7512384778903150156L;

	private String remoteDeviceAddress;
	private String reponse;
	
	public static final String TYPE = "type";
	public static final String VALUE = "value";
	public static final String STATUS = "status";
	public static final String HOST_ADDRESS = "address";
	public static final String HOST_NAME = "hostName";
	public static final String KEY_CODES = "keycodes";
	public static final String KEY_MODIFIERS_DOWN = "keymodifiersdown";
	public static final String KEY_MODIFIERS_UP = "keymodifiersup";
	public static final String PINCODE = "pincode";
	public static final String REMOTE = "remote";
	public static final String SEND_COUNT = "count";
	public static final String MESSAGE_ID = "MESSAGE_ID";
	
	public FromClientResponseMessage(String remoteDeviceAddress, JSONObject response){
		
		/* If remoteDeviceAddress is null its a broadcast message
		 * remoteDeviceAddress = StringUtils.trimToNull(remoteDeviceAddress);
		if(remoteDeviceAddress == null){
			throw new IllegalArgumentException("This method does not accept null parameters");
		}*/
		
		this.reponse = response.toString();
		this.remoteDeviceAddress = remoteDeviceAddress;
	}
	
	public FromClientResponseMessage(JSONObject response){
		this.reponse = response.toString();
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.MESSAGE_FROM_CLIENT_RESPONSE;
	}

	public String getReponse() {
		return reponse;
	}

	public String getRemoteDeviceAddress() {
		return remoteDeviceAddress;
	}
	
	public boolean isBroadcastMessage(){
		if(this.remoteDeviceAddress == null){
			return true;
		}
		
		return false;
	}
}
