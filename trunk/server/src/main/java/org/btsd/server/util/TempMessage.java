package org.btsd.server.util;

public class TempMessage {

	private MessagesEnum type;
	private Object payload;
	
	public TempMessage(MessagesEnum type, Object payload){
		this.type = type;
		this.payload = payload;
	}

	public MessagesEnum getType() {
		return type;
	}

	public Object getPayload() {
		return payload;
	}
	
}
