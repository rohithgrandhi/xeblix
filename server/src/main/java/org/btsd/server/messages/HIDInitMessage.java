package org.btsd.server.messages;

import javax.bluetooth.L2CAPConnection;

import org.btsd.server.util.MessagesEnum;

public class HIDInitMessage implements Message {

	private static final long serialVersionUID = 4800985304191201352L;
	
	private long uuid;
	private L2CAPConnection connection;
	
	public HIDInitMessage(long uuid, L2CAPConnection connection){
		
		if(connection == null){
			throw new IllegalArgumentException("This method does not " +
				"accept null parameters");
		}
		
		this.uuid = uuid;
		this.connection = connection;
	}
	
	public long getUuid() {
		return uuid;
	}

	public L2CAPConnection getConnection() {
		return connection;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.HID_INIT;
	}

}
