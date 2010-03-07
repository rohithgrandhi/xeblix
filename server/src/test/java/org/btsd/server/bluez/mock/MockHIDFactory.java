package org.btsd.server.bluez.mock;

import java.util.ArrayList;
import java.util.List;

import org.btsd.server.bluez.hiddevicemanager.HIDFactory;
import org.btsd.server.messages.Message;
import org.btsd.server.util.ActiveThread;
import org.btsd.server.util.MessagesEnum;
import org.btsd.server.util.ShutdownException;

public class MockHIDFactory implements HIDFactory {

	private int socketCount = 0;
	private int writerCount = 0;
	private List<ActiveThread> connections = new ArrayList<ActiveThread>();
	private List<Message> writerMessages = new ArrayList<Message>();
	
	public ActiveThread getBluetoothHIDSocketActiveObject() {
		socketCount++;
		ActiveThread at = new ActiveThread(){
			@Override
			public void handleMessage(Message msg) {
				//throw new ShutdownException();
			}
		};
		connections.add(at);
		return at;
		
	}

	public ActiveThread getBtHIDWriterActiveObject() {
		writerCount++;
		ActiveThread at = new ActiveThread(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.getType() == MessagesEnum.SHUTDOWN){
					throw new ShutdownException();
				}else{
					writerMessages.add(msg);
				}
			}
		};
		connections.add(at);
		return at;
	}
	
	public int getSocketCount() {
		return socketCount;
	}

	public int getWriterCount() {
		return writerCount;
	}

	public void resetCount(){
		socketCount = 0;
		writerCount = 0;
		
		for(ActiveThread connection: this.connections){
			if(connection.isAlive()){
				connection.interrupt();
			}
		}
		this.connections.clear();
		this.writerMessages.clear();
	}
	
	public List<ActiveThread> getConnections(){
		return this.connections;
	}

	public List<Message> getWriterMessages() {
		return writerMessages;
	}
	
}
