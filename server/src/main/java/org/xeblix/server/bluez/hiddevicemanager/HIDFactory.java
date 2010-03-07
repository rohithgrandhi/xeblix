package org.xeblix.server.bluez.hiddevicemanager;

import org.xeblix.server.util.ActiveThread;

public interface HIDFactory {

	public ActiveThread getBluetoothHIDSocketActiveObject();
	
	public ActiveThread getBtHIDWriterActiveObject();
}
