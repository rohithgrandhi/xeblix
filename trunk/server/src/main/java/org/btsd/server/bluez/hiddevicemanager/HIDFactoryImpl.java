package org.btsd.server.bluez.hiddevicemanager;

import org.btsd.server.bluez.BTHIDWriterActiveObject;
import org.btsd.server.bluez.BluetoothHIDSocketActiveObject;
import org.btsd.server.util.ActiveThread;

public class HIDFactoryImpl implements HIDFactory {

	public ActiveThread getBluetoothHIDSocketActiveObject() {
		return new BluetoothHIDSocketActiveObject();
	}

	public ActiveThread getBtHIDWriterActiveObject() {
		return new BTHIDWriterActiveObject();
	}
	
}
