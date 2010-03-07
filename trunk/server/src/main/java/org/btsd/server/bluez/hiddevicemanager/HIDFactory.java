package org.btsd.server.bluez.hiddevicemanager;

import org.btsd.server.util.ActiveThread;

public interface HIDFactory {

	public ActiveThread getBluetoothHIDSocketActiveObject();
	
	public ActiveThread getBtHIDWriterActiveObject();
}
