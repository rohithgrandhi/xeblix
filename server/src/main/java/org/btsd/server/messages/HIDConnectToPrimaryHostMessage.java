package org.btsd.server.messages;

import org.btsd.server.util.MessagesEnum;

public class HIDConnectToPrimaryHostMessage implements Message {

	private static final long serialVersionUID = 853551167020062812L;

	public MessagesEnum getType() {
		return MessagesEnum.HID_CONNECT_TO_PRIMARY_HOST;
	}

}
