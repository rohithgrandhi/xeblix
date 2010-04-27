package org.xeblix.server.messages;

import org.xeblix.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.xeblix.server.util.ActiveThread;

public class ConfigFromClientMessage extends FromClientMessage {

	private static final long serialVersionUID = 7143821320918763954L;

	public ConfigFromClientMessage(ActiveThread btsdActiveObject, String remoteDeviceAddress) {
		super(ClientTargetsEnum.CONFIGURATION, remoteDeviceAddress, btsdActiveObject);
	}
	
}
