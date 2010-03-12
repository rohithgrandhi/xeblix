package org.xeblix.server.messages;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.xeblix.server.util.ActiveThread;

public final class HIDFromClientMessage extends FromClientMessage {

	private static final long serialVersionUID = 8399470286423534718L;

	private HIDCommands hidCommand;
	private JSONObject clientArguments;
	
	public HIDFromClientMessage(ActiveThread btsdActiveObject, String remoteDeviceAddress, 
		JSONObject clientString) {
		
		super(ClientTargetsEnum.HID, remoteDeviceAddress, btsdActiveObject);
		
		
		try{	
			String stringCommand =clientString.getString(
					FromClientResponseMessage.VALUE);
			HIDCommands command = HIDCommands.getCommand(stringCommand);
			if(command == null){
				throw new IllegalArgumentException("Unknow HIDCommand: " + command);
			}
			this.hidCommand = command;
			this.clientArguments = clientString;
		}catch(JSONException ex){
			throw new IllegalArgumentException("A Client HID message must have at " +
				"least 2 parameters");
		}
		
		
	}
	
	public HIDCommands getHidCommand() {
		return hidCommand;
	}

	public JSONObject getClientArguments() {
		return clientArguments;
	}

	public static enum HIDCommands{
		
		STATUS("HID_STATUS"),
		HID_HOSTS("HID_HOSTS"),
		PAIR_MODE("PAIR_MODE"),
		HID_REPORT("REPORT"),
		CANCEL_PAIR_MODE("PAIR_MODE_CANCEL"),
		CLIENT_PINCODE_CANCEL("PINCODE_CANCEL"),
		CONNECT_TO_HOST("CONNECT_TO_HOST"),
		CONNECT_TO_HOST_CANCEL("CONNECT_TO_HOST_CANCEL"),
		KEYCODE("KEYCODE"),
		DISCONNECTED_FROM_HOST("DISCONNECT_FROM_HOST"),
		CLIENT_PINCODE_RESPONSE("PINCODE_RESPONSE");
		
		private String command;
		
		private HIDCommands(String command) {
			this.command = command;
		}

		public String getCommand() {
			return command;
		}
		
		public static HIDCommands getCommand(String command){
			
			if(STATUS.getCommand().equalsIgnoreCase(command)){
				return STATUS;
			}else if(HID_HOSTS.getCommand().equalsIgnoreCase(command)){
				return HID_HOSTS;
			}else if(PAIR_MODE.getCommand().equalsIgnoreCase(command)){
				return PAIR_MODE;
			}else if(HID_REPORT.getCommand().equalsIgnoreCase(command)){
				return HID_REPORT;
			}else if(CLIENT_PINCODE_RESPONSE.getCommand().equalsIgnoreCase(command)){
				return CLIENT_PINCODE_RESPONSE;
			}else if(CLIENT_PINCODE_CANCEL.getCommand().equalsIgnoreCase(command)){
				return CLIENT_PINCODE_CANCEL;
			}else if(CANCEL_PAIR_MODE.getCommand().equalsIgnoreCase(command)){
				return CANCEL_PAIR_MODE;
			}else if(CONNECT_TO_HOST.getCommand().equalsIgnoreCase(command)){
				return CONNECT_TO_HOST;
			}else if(CONNECT_TO_HOST_CANCEL.getCommand().equalsIgnoreCase(command)){
				return CONNECT_TO_HOST_CANCEL;
			}else if(KEYCODE.getCommand().equalsIgnoreCase(command)){
				return KEYCODE;
			}else if(DISCONNECTED_FROM_HOST.getCommand().equalsIgnoreCase(command)){
				return DISCONNECTED_FROM_HOST;
			}else{
				return null;
			}
			
		}
	}
}
