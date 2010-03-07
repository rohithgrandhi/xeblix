package org.xeblix.server.messages;

import org.xeblix.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public abstract class FromClientMessage implements Message {

	private static final long serialVersionUID = -6620031829177155717L;
	
	private final ActiveThread btsdActiveObject;
	private final String remoteDeviceAddress;
	private final ClientTargetsEnum target;
	
	
	public FromClientMessage(ClientTargetsEnum clientTarget,String remoteDeviceAddress, ActiveThread btsdActiveObject){
		
		this.remoteDeviceAddress = remoteDeviceAddress;
		this.target = clientTarget;
		this.btsdActiveObject = btsdActiveObject;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.MESSAGE_FROM_CLIENT;
	}

	public String getRemoteDeviceAddress() {
		return remoteDeviceAddress;
	}

	public ClientTargetsEnum getTarget() {
		return target;
	}

	public ActiveThread getBtsdActiveObject() {
		return btsdActiveObject;
	}
	
}
