package com.btsd.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public final class BluetoothSocket5OrHigher implements com.btsd.bluetooth.BluetoothSocket {

	private BluetoothSocket socket;
	
	public BluetoothSocket5OrHigher(BluetoothSocket socket){
		if(socket == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		this.socket = socket;
	}
	
	@Override
	public void connect() throws IOException {
		socket.connect();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}
}
