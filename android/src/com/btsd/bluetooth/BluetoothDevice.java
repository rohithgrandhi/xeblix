package com.btsd.bluetooth;

import java.util.UUID;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.BluetoothSocket;

public interface BluetoothDevice {

	public void pair();
	
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws BluetoothException;
	
}
