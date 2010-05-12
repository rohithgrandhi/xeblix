package com.btsd.bluetooth;

import backport.android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.btsd.bluetooth.BluetoothAdapter;

public final class BluetoothAccessor4OrLower extends com.btsd.bluetooth.BluetoothAccessor {

	@Override
	public BluetoothAdapter getDefaultAdapter() {
		return new BluetoothAdapter4OrLower();
	}

	@Override
	public com.btsd.bluetooth.BluetoothDevice getBluetoothDeviceFromIntent(
			Intent intent) {
		
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		return new BluetoothDevice4OrLower(device);
	}
	
}
