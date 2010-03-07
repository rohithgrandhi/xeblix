package org.xeblix.server.messages;

import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public class ClientInitMessage implements Message {

	private static final long serialVersionUID = 3552205496546131915L;
	
	private ActiveThread activeThread;
	private NewClientConnectionMessage connectionInfo;
	
	public ClientInitMessage(ActiveThread activeThread, 
		NewClientConnectionMessage connectionInfo){
		
		if(activeThread == null || connectionInfo == null){
			throw new IllegalArgumentException("This method does not accept " +
				"null parameters");
		}
		
		this.activeThread = activeThread;
		this.connectionInfo = connectionInfo;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.CLIENT_INIT;
	}

	public ActiveThread getActiveThread() {
		return activeThread;
	}

	public NewClientConnectionMessage getConnectionInfo() {
		return connectionInfo;
	}

	
	
}
