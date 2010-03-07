package org.xeblix.server.messages;

import org.xeblix.server.util.MessagesEnum;

public class HIDConnectToPrimaryHostMessage implements Message {

	private static final long serialVersionUID = 853551167020062812L;

	public MessagesEnum getType() {
		return MessagesEnum.HID_CONNECT_TO_PRIMARY_HOST;
	}

}
