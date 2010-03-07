package org.xeblix.server.bluez.hiddevicemanager;

import org.xeblix.server.bluez.BTHIDWriterActiveObject;
import org.xeblix.server.bluez.BluetoothHIDSocketActiveObject;
import org.xeblix.server.util.ActiveThread;

public class HIDFactoryImpl implements HIDFactory {

	public ActiveThread getBluetoothHIDSocketActiveObject() {
		return new BluetoothHIDSocketActiveObject();
	}

	public ActiveThread getBtHIDWriterActiveObject() {
		return new BTHIDWriterActiveObject();
	}
	
}
