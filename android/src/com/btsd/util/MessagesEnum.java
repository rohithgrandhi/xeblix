package com.btsd.util;

public enum MessagesEnum {

	//SEND_COMMAND(1),
	SHUTDOWN(2),
	REGISTER_ACTIVITY(3),
	SERVER_DISCONNECT(4),
	MESSAGE_FROM_SERVER(5),
	BT_CONNECTION_STATE(6),
	SHOW_ERROR_ALERT(7),
	SHOW_PAUSE_ALERT(8),
	FOCUS_EVENT(9),
	CANCEL_PAUSE_ALERT(10),
	MESSAGE_TO_SERVER(11),
	VERSION_REQUEST(12),
	CONNECT_TO_SERVER(13);
	
	private int id;
	
	private MessagesEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
