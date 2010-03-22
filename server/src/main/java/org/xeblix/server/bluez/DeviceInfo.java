package org.xeblix.server.bluez;

public final class DeviceInfo {

	private final String name;
	private final String address;
	private final boolean paired;
	private final boolean connected; 
	
	public DeviceInfo(String name, String address, boolean paired, boolean connected){
		
		if(name == null || address == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		
		address = address.replace(":", "");
		
		this.name = name;
		this.address = address;
		this.paired = paired;
		this.connected = connected;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public boolean isPaired() {
		return paired;
	}

	public boolean isConnected() {
		return connected;
	}
	
}
