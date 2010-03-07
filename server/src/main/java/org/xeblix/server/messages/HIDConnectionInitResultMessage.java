package org.xeblix.server.messages;

import javax.bluetooth.L2CAPConnection;

import org.xeblix.server.util.MessagesEnum;

public class HIDConnectionInitResultMessage implements Message {

	private static final long serialVersionUID = -3415535824194407345L;
	
	private int psm;
	private long uuid;
	private L2CAPConnection connection;
	private String address;
	private String friendlyName;
	private boolean newConnection;
	
	public HIDConnectionInitResultMessage(int psm, long uuid, L2CAPConnection connection, 
			String address, String friendlyName, boolean newConnection){
		
		if(connection == null || address == null){
			throw new IllegalArgumentException("This method does not " +
				"accept null parameters");
		}
		
		this.psm = psm;
		this.uuid = uuid;
		this.connection = connection;
		this.address = address;
		this.friendlyName = friendlyName;
		this.newConnection = newConnection;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.HID_CONNECTION_INIT_RESULT;
	}

	public int getPsm() {
		return psm;
	}

	public long getUuid() {
		return uuid;
	}

	public L2CAPConnection getConnection() {
		return connection;
	}

	public String getAddress() {
		return address;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public boolean isNewConnection() {
		return newConnection;
	}
	
	
}
