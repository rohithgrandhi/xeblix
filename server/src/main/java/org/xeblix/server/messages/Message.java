package org.xeblix.server.messages;

import java.io.Serializable;

import org.xeblix.server.util.MessagesEnum;

public interface Message extends Serializable {

	public MessagesEnum getType();
	
}
