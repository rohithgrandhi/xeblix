package com.btsd.bluetooth;

import android.content.Intent;
import android.os.Build;

public abstract class BluetoothAccessor {

	private static BluetoothAccessor instance;
	
	public static final int ECLAIR = 5;
	
	public synchronized static void registerReceivers(){
		
		/*
		 * Check the version of the SDK we are running on. If under 2.0
		 * then need to register the bluetooth backport receiver
		 */
		
		
	}
	
	public synchronized static BluetoothAccessor getInstance(){
		
		if(instance == null){
			String className;

            /*
             * Check the version of the SDK we are running on. Choose an
             * implementation class designed for that version of the SDK.
             *
             * Unfortunately we have to use strings to represent the class
             * names. If we used the conventional ContactAccessorSdk5.class.getName()
             * syntax, we would get a ClassNotFoundException at runtime on pre-Eclair SDKs.
             * Using the above syntax would force Dalvik to load the class and try to
             * resolve references to all other classes it uses. Since the pre-Eclair
             * does not have those classes, the loading of ContactAccessorSdk5 would fail.
             */
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);       // Cupcake style
            if (sdkVersion >= ECLAIR) {
                className = "com.btsd.bluetooth.BluetoothAccessor5OrHigher";
            } else {
                className = "com.btsd.bluetooth.BluetoothAccessor4OrLower";
            }

            /*
             * Find the required class by name and instantiate it.
             */
            try {
                Class<? extends BluetoothAccessor> clazz =
                        Class.forName(className).asSubclass(BluetoothAccessor.class);
                instance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

		}
		
		return instance;
		
	}
	
	public abstract BluetoothAdapter getDefaultAdapter();
	
	public abstract BluetoothDevice getBluetoothDeviceFromIntent(Intent intent);
}
