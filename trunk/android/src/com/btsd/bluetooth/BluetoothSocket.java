package com.btsd.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BluetoothSocket {

	public void connect() throws IOException;
	
	public InputStream getInputStream() throws IOException;
	
	public OutputStream getOutputStream() throws IOException;
	
	public void close() throws IOException;
	
}
