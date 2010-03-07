package org.xeblix.server.bluez.hiddevicemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.xeblix.server.bluez.DBusManager;
import org.xeblix.server.messages.HIDConnectToPrimaryHostMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.messages.HIDFromClientMessage.HIDCommands;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public final class HIDDeviceManager extends ActiveThread{

	private ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
	private ActiveThread btsdActiveObject;
	
	private HIDDeviceManagerState previousState;
	private HIDDeviceManagerState state;
	
	private DBusManager dbusManager;
	private HIDFactory hidFactory;
	
	private HIDHostInfo hostInfo;
	private HIDHostInfo connectedHostInfo;
	
	private ActiveThread inputHIDSocketActiveObject;
	private ActiveThread controlHIDSocketActiveObject;
	private ActiveThread btHIDWriterActiveObject;
	
	public static final int INPUT_UUID = 3453459;
	public static final int CONTROL_UUID = 3153189;
	
	private int validateConnectionTimeout = 0;
	
	public HIDDeviceManager(DBusManager dbusManager, ActiveThread btsdActiveObject, HIDFactory hidFactory){
		this(dbusManager, btsdActiveObject, hidFactory, 5000);
	}
	
	public HIDDeviceManager(DBusManager dbusManager, ActiveThread btsdActiveObject, HIDFactory hidFactory, 
		int validateConnectionTimeout){
		
		super();
		
		if(dbusManager == null || btsdActiveObject == null){
			throw new IllegalArgumentException("This method does not accept null parameters");
		}
		
		//addHIDHost(new HIDHostInfo("000272159B71", "WINXP-DEV", false));
		
		try{
			hidHosts = (ArrayList<HIDHostInfo>)SerializationUtils.deserialize(new FileInputStream(
				HIDHostInfo.class.getName()));
		}catch(IOException ex){
			System.out.println("Failed to find HID Host information.");
			ex.printStackTrace();
		}
		this.dbusManager = dbusManager;
		this.btsdActiveObject = btsdActiveObject;
		this.state = HIDDeviceDisconnectedState.getInstance();
		this.previousState = this.state;
		this.hidFactory = hidFactory;
		this.validateConnectionTimeout = validateConnectionTimeout; 
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.HID_CONNECT_TO_PRIMARY_HOST){
			state.hidConnectToPrimaryHost(this, (HIDConnectToPrimaryHostMessage)msg);
		}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
			
			HIDFromClientMessage clientMessage = (HIDFromClientMessage)msg;
			
			if(clientMessage.getHidCommand() == HIDCommands.STATUS){
				state.clientMessageStatus(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.HID_HOSTS){
				state.clientMessageHIDHosts(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.PAIR_MODE){
				state.clientMessagePairMode(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.CLIENT_PINCODE_RESPONSE){
				state.clientMessagePinCodeResponse(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.CLIENT_PINCODE_CANCEL){
				state.clientMessagePinCodeCancel(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.CANCEL_PAIR_MODE){
				state.clientMessagePairModeCancel(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.CONNECT_TO_HOST){
				state.clientMessageConnectToHost(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.CONNECT_TO_HOST_CANCEL){
				state.clientMessageConnectToHostCancel(this, clientMessage);
			}else if(clientMessage.getHidCommand() == HIDCommands.KEYCODE){
				state.clientMessageKeyCode(this, clientMessage);
			}else{
				throw new UnsupportedOperationException("Implement me");
			}
		}else if(msg.getType() == MessagesEnum.HID_CONNECTION_INIT_RESULT){
			state.hidConnectionResult(this, (HIDConnectionInitResultMessage)msg);
		}else if(msg.getType() == MessagesEnum.VALIDATE_HID_CONNECT){
			state.validateHIDConnection(this, (ValidateHIDConnection)msg);
		}else if(msg.getType() == MessagesEnum.HID_HOST_DISCONNECT){
			state.hidHostDisconnect(this, (HIDHostDisconnect)msg);
		}
		
	}
	
	DBusManager getDbusManager() {
		return dbusManager;
	}

	ArrayList<HIDHostInfo> getHidHosts() {
		return hidHosts;
	}

	void setHidHosts(ArrayList<HIDHostInfo> hidHosts) {
		this.hidHosts = hidHosts;
	}

	ActiveThread getBtsdActiveObject() {
		return btsdActiveObject;
	}

	void setBtsdActiveObject(ActiveThread btsdActiveObject) {
		this.btsdActiveObject = btsdActiveObject;
	}

	void updateState(HIDDeviceManagerState state){
		this.previousState = this.state;
		this.state = state;
	}
	
	HIDDeviceManagerState getPreviousState(){
		return this.previousState;
	}
	
	ActiveThread getInputHIDSocketActiveObject() {
		return inputHIDSocketActiveObject;
	}

	void setInputHIDSocketActiveObject(ActiveThread inputHIDSocketActiveObject) {
		this.inputHIDSocketActiveObject = inputHIDSocketActiveObject;
	}

	ActiveThread getControlHIDSocketActiveObject() {
		return controlHIDSocketActiveObject;
	}

	void setControlHIDSocketActiveObject(ActiveThread controlHIDSocketActiveObject) {
		this.controlHIDSocketActiveObject = controlHIDSocketActiveObject;
	}

	ActiveThread getBtHIDWriterActiveObject() {
		return btHIDWriterActiveObject;
	}

	void setBtHIDWriterActiveObject(ActiveThread btHIDWriterActiveObject) {
		this.btHIDWriterActiveObject = btHIDWriterActiveObject;
	}

	HIDFactory getHidFactory() {
		return hidFactory;
	}

	void setHostInfo(HIDHostInfo hostInfo){
		this.hostInfo = hostInfo;
	}

	HIDHostInfo getHostInfo(){
		return this.hostInfo;
	}
	
	int getValidateConnectionTimeout() {
		return validateConnectionTimeout;
	}

	public HIDDeviceManagerState getDeviceManagerState() {
		return state;
	}


	void addHIDHost(HIDHostInfo hostInfo){
		
		if(this.hidHosts == null){
			hidHosts = new ArrayList<HIDHostInfo>();
		}
		
		hidHosts.add(hostInfo);
		
		try{
			SerializationUtils.serialize(hidHosts, new FileOutputStream(new File(HIDHostInfo.class.getName())));
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}
	
	HIDHostInfo getConnectedHostInfo() {
		return connectedHostInfo;
	}

	void setConnectedHostInfo(HIDHostInfo connectedHostInfo) {
		this.connectedHostInfo = connectedHostInfo;
	}

	public static class HIDHostInfo implements Serializable{
		
		private static final long serialVersionUID = 2995330023401925046L;
		
		private String address;
		private String name;
		private boolean primary;
		
		public HIDHostInfo(String address, String name, boolean primary){
			
			address = StringUtils.trimToNull(address);
			name = StringUtils.trimToNull(name);
			if(address == null){
				throw new IllegalArgumentException("This method does not accept null parameters");
			}
			
			this.address = address;
			this.name = name;
			this.primary = primary;
		}

		public String getAddress() {
			return address;
		}

		public String getName() {
			return name;
		}

		public boolean isPrimary() {
			return primary;
		}
	}
}
