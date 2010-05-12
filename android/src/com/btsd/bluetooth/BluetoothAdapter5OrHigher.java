package com.btsd.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.btsd.bluetooth.BluetoothDevice;

public final  class BluetoothAdapter5OrHigher implements com.btsd.bluetooth.BluetoothAdapter {

	private BluetoothAdapter adapter;
	
	public BluetoothAdapter5OrHigher(){
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public boolean cancelDiscovery() {
		return adapter.cancelDiscovery();
	}

	@Override
	public BluetoothDevice getRemoteDevice(String address) {
		android.bluetooth.BluetoothDevice device =  adapter.getRemoteDevice(address);
		return new BluetoothDevice5OrHigher(device);
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
