package com.btsd.bluetooth;

import java.io.IOException;
import java.util.UUID;

public abstract class BluetoothDevice {

	public abstract BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)	throws IOException;
	
	public abstract int getBondState();
	
	public abstract String getName();
	
	public abstract String getAddress();
}
