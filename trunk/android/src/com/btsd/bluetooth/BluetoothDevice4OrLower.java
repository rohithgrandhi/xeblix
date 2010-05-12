package com.btsd.bluetooth;

import java.io.IOException;
import java.util.UUID;

import backport.android.bluetooth.BluetoothDevice;

import com.btsd.bluetooth.BluetoothSocket;

public final class BluetoothDevice4OrLower extends com.btsd.bluetooth.BluetoothDevice {

	private BluetoothDevice device;
	
	public BluetoothDevice4OrLower(BluetoothDevice device){
		if(device == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		this.device = device;
	}
	
	@Override
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
			throws IOException {
		
		backport.android.bluetooth.BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
		return new BluetoothSocket4OrLower(socket);
	}

	@Override
	public int getBondState() {
		return device.getBondState();
	}
	
	@Override
	public String getAddress() {
		return device.getAddress();
	}
	
	@Override
	public String getName() {
		return device.getName();
	}
	
}
