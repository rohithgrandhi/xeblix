package org.xeblix.server.bluez.mock;

import java.util.ArrayList;
import java.util.List;

import org.xeblix.server.messages.Message;
import org.xeblix.server.util.ActiveThread;

public class MockActiveObject extends ActiveThread {

	private List<Message> messages = new ArrayList<Message>();
	
	@Override
	public void handleMessage(Message msg) {
		messages.add(msg);
	}

	public List<Message> getMessages(){
		return this.messages;
	}
}
