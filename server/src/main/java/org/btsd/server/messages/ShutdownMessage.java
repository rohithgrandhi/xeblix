package org.btsd.server.messages;

import org.btsd.server.util.MessagesEnum;

public class ShutdownMessage implements Message {

	private static final long serialVersionUID = 7069343966958019170L;

	public MessagesEnum getType() {
		return MessagesEnum.SHUTDOWN;
	}

}
