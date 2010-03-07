package org.xeblix.server.bluez;

import org.bluez.v4.Agent;

public interface BluezAuthenticationAgent extends Agent {

	public String getPinCode();

	public void setPinCode(String pinCode);
}
