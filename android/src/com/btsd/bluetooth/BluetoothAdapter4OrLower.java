package com.btsd.bluetooth;


import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import android.content.Context;
import android.util.Log;

public final class BluetoothAdapter4OrLower implements BluetoothAdapter{

	private LocalBluetoothDevice device;
	
	public BluetoothAdapter4OrLower(Context context){
		try{
			device = LocalBluetoothDevice.initLocalDevice(context);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public boolean isEnabled() {
		try{
			return device.isEnabled();
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public BluetoothDevice getRemoteDevice(String address) {
		
		return new BluetoothDevice4OrLower(device.getRemoteBluetoothDevice(address), 
			this);
	}
	
	@Override
	public boolean cancelDiscovery() {
		try{
			device.stopScanning();
			return true;
		}catch(Exception ex){
			Log.e(getClass().getSimpleName(), ex.getMessage(), ex);
			return false;
		}
	}
}
