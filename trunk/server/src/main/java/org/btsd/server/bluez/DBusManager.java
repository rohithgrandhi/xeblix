package org.btsd.server.bluez;

import org.btsd.server.util.ActiveThread;

public interface DBusManager {

	public void setDeviceHidden();
	
	public void setDeviceDiscoverable();
	
	public void setDeviceNotDiscoverable();
	
	public void registerAgent(ActiveThread mainActiveObject) ;
	
	public BluezAuthenticationAgent getAgent();
	
	public void registerSDPRecord();
}
