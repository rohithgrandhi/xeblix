package org.btsd.server.bluez.mock;

import java.util.ArrayList;
import java.util.List;

import org.btsd.server.messages.Message;
import org.btsd.server.util.ActiveThread;

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
