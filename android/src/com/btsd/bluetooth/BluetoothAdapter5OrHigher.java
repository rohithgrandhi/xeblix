package com.btsd.bluetooth;

public class BluetoothAdapter5OrHigher implements BluetoothAdapter{

	private android.bluetooth.BluetoothAdapter adapter;
	
	public BluetoothAdapter5OrHigher() {
		adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public boolean isEnabled() {
		return adapter.isEnabled();
	}
	
	@Override
	public BluetoothDevice getRemoteDevice(String address) {
		
		return new BluetoothDevice5OrHigher(adapter.getRemoteDevice(address), this);
	}
	
	@Override
	public boolean cancelDiscovery() {
		return adapter.cancelDiscovery();
	}
}
