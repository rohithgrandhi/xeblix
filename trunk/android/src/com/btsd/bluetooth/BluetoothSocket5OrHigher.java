package com.btsd.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

public final class BluetoothSocket5OrHigher implements BluetoothSocket {

	private android.bluetooth.BluetoothSocket socket;
	
	public BluetoothSocket5OrHigher(android.bluetooth.BluetoothSocket socket){
		this.socket = socket;
	}
	
	@Override
	public BluetoothSocket accept(int timeout) throws Exception {
		throw new UnsupportedOperationException("accept is not supported");
	}

	@Override
	public void closeSocket() {
		try{
			socket.close();
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public InputStream getInputStream() throws Exception {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws Exception {
		return socket.getOutputStream();
	}

	@Override
	public int getPort() {
		throw new UnsupportedOperationException("Get Port is not supported");
	}

	@Override
	public RemoteBluetoothDevice getRemoteBluetoothDevice() {
		throw new UnsupportedOperationException("getRemoteBluetoothDevice is not supported");
	}

}
