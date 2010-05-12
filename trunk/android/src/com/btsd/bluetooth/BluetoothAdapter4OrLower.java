package com.btsd.bluetooth;

import backport.android.bluetooth.BluetoothAdapter;

import com.btsd.bluetooth.BluetoothDevice;

public final class BluetoothAdapter4OrLower implements com.btsd.bluetooth.BluetoothAdapter {

	private BluetoothAdapter adapter;
	
	public BluetoothAdapter4OrLower(){
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public boolean cancelDiscovery() {
		return adapter.cancelDiscovery();
	}

	@Override
	public BluetoothDevice getRemoteDevice(String address) {
		backport.android.bluetooth.BluetoothDevice device =  adapter.getRemoteDevice(address);
		return new BluetoothDevice4OrLower(device);
	}

	@Override
	public boolean isEnabled() {
		return adapter.isEnabled();
	}

	@Override
	public boolean startDiscovery() {
		return adapter.startDiscovery();
	}
}
