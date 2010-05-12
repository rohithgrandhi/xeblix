package com.btsd.bluetooth;

import android.content.Intent;
import android.bluetooth.BluetoothDevice;

import com.btsd.bluetooth.BluetoothAdapter;

public final class BluetoothAccessor5OrHigher extends com.btsd.bluetooth.BluetoothAccessor {

	@Override
	public BluetoothAdapter getDefaultAdapter() {
		return new BluetoothAdapter5OrHigher();
	}

	@Override
	public com.btsd.bluetooth.BluetoothDevice getBluetoothDeviceFromIntent(Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		return new BluetoothDevice5OrHigher(device);
	}
	
}
