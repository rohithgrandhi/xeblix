package org.btsd.server.messages;

import java.io.Serializable;

import org.btsd.server.util.MessagesEnum;

public interface Message extends Serializable {

	public MessagesEnum getType();
	
}
