package org.btsd.server.messages;

import org.btsd.server.client.ClientReaderActiveObject.ClientTargetsEnum;
import org.btsd.server.util.ActiveThread;
import org.json.JSONException;
import org.json.JSONObject;

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
			}else if(KEYCODE.getCommand().equalsIgnoreCase("KEYCODE")){
				return KEYCODE;
			}else{
				return null;
			}
			
		}
	}
}
