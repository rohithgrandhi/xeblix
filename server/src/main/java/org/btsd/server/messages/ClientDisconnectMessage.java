package org.btsd.server.messages;

import org.apache.commons.lang.StringUtils;
import org.btsd.server.util.MessagesEnum;

public class ClientDisconnectMessage implements Message {

	private static final long serialVersionUID = 3390573816871597730L;
	
	private String remoteDeviceAddress;
	
	public ClientDisconnectMessage(String remoteDeviceAddress){
		
		remoteDeviceAddress = StringUtils.trimToNull(remoteDeviceAddress);
		if(remoteDeviceAddress == null){
			throw new IllegalArgumentException("This method does not " +
				"accept null parameters");
		}
		
		this.remoteDeviceAddress = remoteDeviceAddress;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.CLIENT_DISCONNECT;
	}

	public String getRemoteDeviceAddress() {
		return remoteDeviceAddress;
	}

	
}
