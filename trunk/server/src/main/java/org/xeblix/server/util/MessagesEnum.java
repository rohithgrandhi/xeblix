package org.xeblix.server.util;

public enum MessagesEnum {

	STARTUP("System startup comand"),
	SHUTDOWN("System shutdown command"),
	SERVICE_FAILURE("Service Failure"),
	LIRC_INIT("LIRC Init Message"),
	LIRC_COMMAND("LIRC Command"),
	HID_CONNECTION_INIT("Bluetooth HID Connection Initialization"),
	HID_CONNECTION_INIT_RESULT("Bluetooth HID Connection Init Result"),
	HID_INIT("HID Initialization"),
	HID_COMMAND("HID Command"),
	HID_STATUS_RESULT("HID Status Result"),
	HID_HOST_DISCONNECT("HID Host Disconnect"),
	CLIENT_MANAGER_INIT("Client Manager Init Message"),
	NEW_CLIENT_CONNECTION("New Client Connection Message"),
	CLIENT_INIT("Client Init"),
	CLIENT_DISCONNECT("Client Disconnect"),
	MESSAGE_FROM_CLIENT("Message from Client"),
	MESSAGE_FROM_CLIENT_RESPONSE("Message from Client Result"),
	MESSAGE_TO_CLIENT("Message to client"),
	AUTH_AGENT_PIN_REQUEST("Authentication Agent Pin Request"),
	AUTH_AGENT_PIN_CONFIRMATION("Authentication Agent Pin Confirmation"),
	HID_CONNECTION_COMPLETE("Connected to HID Device"),
	AUTH_AGENT_HID_HOST_CANCEL_PIN_REQUEST("Authentication Agent Cancel Pin Request"),
	VALIDATE_HID_CONNECT("Validate HID Connection state"),
	AUTH_AGENT_PIN_RESPONSE("Authentication Agent Pin Response");
	
	private String description;
	
	MessagesEnum(String description){
		
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
