package org.btsd.server.messages;

import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;

public class ClientManagerInitMessage implements Message {

	private static final long serialVersionUID = 9115336052779711498L;
	
	private ActiveThread mainAO;
	
	public ClientManagerInitMessage(ActiveThread mainAO){
		
		if(mainAO == null){
			throw new IllegalArgumentException("This method does " +
				"not accept null parameters.");
		}
		
		this.mainAO = mainAO;
		
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.CLIENT_MANAGER_INIT;
	}

	public ActiveThread getMainAO() {
		return mainAO;
	}
	
}
