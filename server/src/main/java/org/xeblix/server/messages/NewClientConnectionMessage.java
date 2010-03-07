package org.xeblix.server.messages;

import javax.microedition.io.StreamConnection;

import org.xeblix.server.util.MessagesEnum;

public class NewClientConnectionMessage implements Message {

	private static final long serialVersionUID = -7153661397943928922L;
	
	private StreamConnection connection;
	private String address;
	
	public NewClientConnectionMessage(String address, StreamConnection connection){
		if(address == null || connection == null){
			throw new IllegalArgumentException("This method does not accept null parameters");
		}
		this.connection = connection;
		this.address = address;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.NEW_CLIENT_CONNECTION;
	}

	public StreamConnection getConnection() {
		return connection;
	}

	public String getAddress() {
		return address;
	}

	
	
}
