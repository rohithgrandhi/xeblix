package org.xeblix.server.messages;

import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public class LircInit implements Message {

	private static final long serialVersionUID = 1273610555060472659L;

	private ActiveThread sender;
	
	public LircInit(ActiveThread sender){
		if(sender == null){
			throw new IllegalArgumentException("This method does not accept " +
				"null parameters");
		}
		this.sender = sender;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.LIRC_INIT;
	}

	public ActiveThread getSender() {
		return sender;
	}

	
	
}
