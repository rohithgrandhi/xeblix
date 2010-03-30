package com.btsd.bluetooth;

import java.util.UUID;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

public final class BluetoothDevice4OrLower implements BluetoothDevice {

	private RemoteBluetoothDevice remoteDevice; 
	private BluetoothAdapter adapter;
	
	public BluetoothDevice4OrLower(RemoteBluetoothDevice remoteDevice, BluetoothAdapter adapter){
		this.remoteDevice = remoteDevice;
		this.adapter = adapter;
	}
	
	@Override
	public void pair() {
		remoteDevice.pair();
	}
	
	@Override
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) 
		throws BluetoothException{
		
		adapter.cancelDiscovery();
		//ignore the uuid just connect to a port. 
		//TODO: may want to attempt a connection to port 1, if it fails 
		//do a search or try another socket
		return remoteDevice.openSocket(1);
	}
}
