package com.btsd.bluetooth;

import android.content.Context;

public final class BluetoothAccessor4OrLower extends BluetoothAccessor{

	@Override
	public BluetoothAdapter getBluetoothAdapter(Context context) {
		return new BluetoothAdapter4OrLower(context);
	}
	
}
