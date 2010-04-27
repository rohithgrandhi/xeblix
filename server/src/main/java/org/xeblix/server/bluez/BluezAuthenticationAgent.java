package org.xeblix.server.bluez;

import org.bluez.v4.Agent;

public interface BluezAuthenticationAgent extends Agent {

	public String getPinCode();

	public void setPinCode(String pinCode);
	
	/**
	 * Used when device is not in pair mode, the agent will return a default
	 * PIN code (0000). This is used when a phone client is connecting. 
	 */
	public void setDefaultPinCode();
}
