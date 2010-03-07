package org.xeblix.server.messages;

import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public class HIDConnectionInitMessage implements Message {

	private static final long serialVersionUID = -2533197109470195742L;
	
	private ActiveThread activeThread;
	private int psm;
	private long uuid;
	private String hostAddress;
	private boolean serverMode;
	
	public HIDConnectionInitMessage(ActiveThread activeThread, int psm, long uuid){
		
		if(activeThread == null){
			throw new IllegalArgumentException("This method does not accept " +
				"null parameters");
		}
		this.activeThread = activeThread;
		this.psm = psm;
		this.uuid = uuid;
		this.serverMode = true;
	}
	
	public HIDConnectionInitMessage(ActiveThread activeThread, String hostAddress, int psm, long uuid){
		
		if(activeThread == null){
			throw new IllegalArgumentException("This method does not accept " +
				"null parameters");
		}
		this.activeThread = activeThread;
		this.psm = psm;
		this.hostAddress = hostAddress;
		this.uuid = uuid;
		this.serverMode = false;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.HID_CONNECTION_INIT;
	}

	public ActiveThread getActiveThread() {
		return activeThread;
	}

	public int getPsm() {
		return psm;
	}

	public long getUuid() {
		return uuid;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public boolean isServerMode() {
		return serverMode;
	}

	
}
