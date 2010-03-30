package com.btsd.bluetooth;

public interface BluetoothAdapter {

	public boolean isEnabled();

	public BluetoothDevice getRemoteDevice(String address);
	
	public boolean cancelDiscovery();
}
