package com.btsd.bluetooth;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public final class BluetoothDevice5OrHigher implements BluetoothDevice {

	private android.bluetooth.BluetoothDevice device;
	private BluetoothAdapter adapter;
	
	public BluetoothDevice5OrHigher(android.bluetooth.BluetoothDevice device, BluetoothAdapter adapter){
		this.device = device;
		this.adapter = adapter;
	}
	
	@Override
	public void pair() {
		//do nothing
	}
	
	@Override
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
			throws BluetoothException {
		
		android.bluetooth.BluetoothSocket socket = null;
		try{
			socket = device.createRfcommSocketToServiceRecord(uuid);
			adapter.cancelDiscovery();
			socket.connect();
		}catch(IOException ex){
			throw new BluetoothException(ex.getMessage(), ex);
		}
		
		return new BluetoothSocket5OrHigher(socket);
	}
}
