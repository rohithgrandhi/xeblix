package com.btsd.bluetooth;

import android.content.Context;

public final class BluetoothAccessor5OrHigher extends BluetoothAccessor{

	@Override
	public BluetoothAdapter getBluetoothAdapter(Context context) {
		return new BluetoothAdapter5OrHigher();
	}
	
}
