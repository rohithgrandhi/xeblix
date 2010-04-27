package com.btsd.bluetooth;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.BluetoothSocket;

import java.util.UUID;

public interface BluetoothDevice {

	public void pair();
	
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws BluetoothException;
	
}
