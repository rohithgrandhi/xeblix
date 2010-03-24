package org.xeblix.server.bluez.hiddevicemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;
import org.xeblix.server.bluez.DBusManager;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.Message;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.messages.HIDFromClientMessage.HIDCommands;
import org.xeblix.server.util.ActiveThread;
import org.xeblix.server.util.MessagesEnum;

public final class HIDDeviceManager extends ActiveThread{

	/**
	 * Additional info of all known and paired hidHosts
	 */
	private List<DeviceInfo> hidHosts = null;
	private ActiveThread btsdActiveObject;
	
	private HIDDeviceManagerState previousState;
	private HIDDeviceManagerState state;
	
	private DBusManager dbusManager;
	private HIDFactory hidFactory;
	
	/*private HIDHostInfo hostInfo;*/
	/**
	 * Used to store the address of a potential new HIDHost 
	 */
	private String possibleHidHostAddress;
	/**
	 * Info about the currently connected hidHost
	 */
	private DeviceInfo connectedHostInfo;
	
	private ActiveThread inputHIDSocketActiveObject;
	private ActiveThread controlHIDSocketActiveObject;
	private ActiveThread btHIDWriterActiveObject;
	
	public static final int INPUT_UUID = 3453459;
	public static final int CONTROL_UUID = 3153189;
	
	private int validateConnectionTimeout = 0;
	//kind of a hack, using this boolean to keep from having to create a new state
	//The new state would behave just like PairModeState except moves to connected state
	//if it receives two hid host connections rather than wait for a pin code like it
	//does without the pin confirmation
	private boolean receivedPinconfirmation = false;
	
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
		
		this.dbusManager = dbusManager;
		this.btsdActiveObject = btsdActiveObject;
		this.state = HIDDeviceDisconnectedState.getInstance();
		this.previousState = this.state;
		this.hidFactory = hidFactory;
		this.validateConnectionTimeout = validateConnectionTimeout;
		
		Set<String> hidHostAddresses = getHIDHostAddressesFromFile();
		this.hidHosts =  getValidListOfHIDHosts(dbusManager,  hidHostAddresses);
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		System.out.println("State:" + state.getClass().getSimpleName() + 
				" handling message.");
		
		if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
			
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
			}else if(clientMessage.getHidCommand() == HIDCommands.DISCONNECTED_FROM_HOST){
				state.hidHostDisconnect(this, new HIDHostDisconnect());
			}else{
				throw new UnsupportedOperationException("Implement me");
			}
		}else if(msg.getType() == MessagesEnum.HID_CONNECTION_INIT_RESULT){
			state.hidConnectionResult(this, (HIDConnectionInitResultMessage)msg);
		}else if(msg.getType() == MessagesEnum.VALIDATE_HID_CONNECT){
			state.validateHIDConnection(this, (ValidateHIDConnection)msg);
		}else if(msg.getType() == MessagesEnum.HID_HOST_DISCONNECT){
			state.hidHostDisconnect(this, (HIDHostDisconnect)msg);
		}else if(msg.getType() == MessagesEnum.AUTH_AGENT_PIN_CONFIRMATION){
			//this value is set to false as soon as the HIDDeviceManagerState is changed
			this.receivedPinconfirmation = true;
		}else if(msg.getType() == MessagesEnum.AUTH_AGENT_HID_HOST_CANCEL_PIN_REQUEST){
			state.hidHostPinCodeCancel(this, (HIDHostCancelPinRequestMessage)msg);
		}
		
	}
	
	DBusManager getDbusManager() {
		return dbusManager;
	}

	synchronized List<DeviceInfo> getHidHosts(){
		return hidHosts;
	}
	
	/*synchronized void setHidHosts(List<DeviceInfo> hidHosts){
		this.hidHosts = Collections.unmodifiableList(hidHosts);
	}*/
	
	/* not sure this is needed
	 * void setHidHosts(Set<String> hidHosts) {
		this.hidHostAddressess = hidHosts;
	}*/

	ActiveThread getBtsdActiveObject() {
		return btsdActiveObject;
	}

	void setBtsdActiveObject(ActiveThread btsdActiveObject) {
		this.btsdActiveObject = btsdActiveObject;
	}

	void updateState(HIDDeviceManagerState state){
		this.receivedPinconfirmation = false;
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

	int getValidateConnectionTimeout() {
		return validateConnectionTimeout;
	}

	String getPossibleHidHostAddress() {
		return possibleHidHostAddress;
	}

	void setPossibleHidHostAddress(String possibleHidHostAddress) {
		this.possibleHidHostAddress = possibleHidHostAddress;
		
		//need to check if the specified address is already "known"
		//if so then need to forget it
		if(possibleHidHostAddress != null && isPairedDevice(possibleHidHostAddress)){
			removePairedDevice(possibleHidHostAddress,false);
		}
		
	}

	boolean isReceivedPinconfirmation() {
		return receivedPinconfirmation;
	}

	public HIDDeviceManagerState getDeviceManagerState() {
		return state;
	}


	synchronized DeviceInfo addHIDHost(String address){
		
		Set<String> oldHidHostAddresses = getHIDHostAddressesFromFile();
		
		if(oldHidHostAddresses.contains(address)){
			//throw exception to help find any bugs
			throw new IllegalStateException("Attempting to add HID Host with address: " + 
				address + ". The HID Host is already a know and valid HID Host.");
		}
		
		Set<String> newHIDHostAddresses = new HashSet<String>();
		newHIDHostAddresses.addAll(oldHidHostAddresses);
		newHIDHostAddresses.add(address);
		
		List<DeviceInfo> devices = getValidListOfHIDHosts(this.dbusManager, 
			newHIDHostAddresses);
		
		//validate the new address is in the list of devices
		DeviceInfo toReturn = null;
		for(DeviceInfo deviceInfo: devices){
			if(deviceInfo.getAddress().equalsIgnoreCase(address)){
				toReturn = deviceInfo;
				break;
			}
		}
		
		//check if specified address is associated with paired device.
		if(toReturn == null){
			throw new IllegalStateException("The address: " + address +  
				" is not valid. It is not paired with this Xeblix server.");
		}
		
		try{
			//try to backup the file
			File oldFile = new File("HIDHosts");
			if(oldFile.exists()){
				SimpleDateFormat ds = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
				oldFile.renameTo(new File("HIDHosts-" + ds.format(new Date()) + ".old"));
			}
			SerializationUtils.serialize((HashSet<String>)newHIDHostAddresses, 
				new FileOutputStream(new File("HIDHosts")));
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		//update the list of hidHosts
		this.hidHosts = devices;
		return toReturn;
	}
	
	/**
	 * Returns true if the specified address is a valid (i.e. paired) device 
	 * or false otherwise. 
	 * @param address
	 * @return
	 */
	boolean isPairedDevice(String address){
		Set<String> deviceAddresses = new HashSet<String>();
		deviceAddresses.add(address);
		List<DeviceInfo> devices = getValidListOfHIDHosts(this.dbusManager, 
				deviceAddresses);
		
		boolean foundAddress = false;
		for(DeviceInfo deviceInfo: devices){
			if(deviceInfo.getAddress().equalsIgnoreCase(address)){
				foundAddress = true;
				break;
			}
		}
		
		return foundAddress;
	}
	
	/**
	 * This method is called to remove a paired device. This is usually called when the
	 * device to remove is being re-paired
	 * @param address
	 */
	private void removePairedDevice(String address, boolean removeFromBluez){
		Set<String> oldHidHostAddresses = getHIDHostAddressesFromFile();
		
		System.out.println("Removing Paired Device: " + address);
		
		if(!oldHidHostAddresses.contains(address)){
			//throw exception to help find any bugs
			throw new IllegalStateException("Failed to remove HID Host with address: " + 
				address + ". The HID Host is not a known HID Host.");
		}
		
		Set<String> newHIDHostAddresses = new HashSet<String>(oldHidHostAddresses.size() - 1);
		for(String pairedAddress: oldHidHostAddresses){
			if(!address.equalsIgnoreCase(pairedAddress)){
				newHIDHostAddresses.add(pairedAddress);
			}
		}
		
		//we don't want to remove from bluez if this method is called when a hidHost
		//is trying to pair, will cause issues
		List<DeviceInfo> devices = this.hidHosts;
		if(removeFromBluez){
			dbusManager.removePairedDevice(address);
			
			devices = getValidListOfHIDHosts(this.dbusManager, 
				newHIDHostAddresses);
			
			//validate the address is not in the list of devices
			for(DeviceInfo deviceInfo: devices){
				if(deviceInfo.getAddress().equalsIgnoreCase(address)){
					throw new IllegalStateException("Device with address: " + 
						address + " was un-paired, but is still being returned by " +
						"dbus as paired.");
				}
			}
		}
		
		try{
			//try to backup the file
			File oldFile = new File("HIDHosts");
			if(oldFile.exists()){
				SimpleDateFormat ds = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
				oldFile.renameTo(new File("HIDHosts-" + ds.format(new Date()) + ".old"));
			}
			SerializationUtils.serialize((HashSet<String>)newHIDHostAddresses, 
				new FileOutputStream(new File("HIDHosts")));
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		//update the list of hidHosts
		this.hidHosts = devices;
	}
	
	//TODO: recover from corrupted HIDHosts file using backup files
	private Set<String> getHIDHostAddressesFromFile(){
		Set<String> hidHostAddressess = null;
		try{
			hidHostAddressess = (HashSet<String>)SerializationUtils.deserialize(new FileInputStream(
				"HIDHosts"));
		}catch(IOException ex){
			System.out.println("Failed to find HID Host information.");
			ex.printStackTrace();
		}
		if(hidHostAddressess == null){
			hidHostAddressess = new HashSet<String>();
		}
		
		return Collections.unmodifiableSet(hidHostAddressess);
	}
	
	private static List<DeviceInfo> getValidListOfHIDHosts(DBusManager dbusManager, 
		Set<String> hidHostAddressess){
		
		if(hidHostAddressess == null){
			throw new IllegalArgumentException("This method does not accept null parameters");
		}
		
		//list of all devices, hidHosts and xeblix clients, only interested in 
		//hidHosts
		List<DeviceInfo> devices = dbusManager.listDevices();
		Map<String, DeviceInfo> devicesByAddress = new HashMap<String, DeviceInfo>();
		for(DeviceInfo deviceInfo: devices){
			System.out.println("deviceInfo.getAddress(): " + deviceInfo.getAddress());
			devicesByAddress.put(deviceInfo.getAddress(), deviceInfo);
		}
		List<DeviceInfo> tempHIDHosts = new ArrayList<DeviceInfo>();
		for(String hidHostAddress: hidHostAddressess){
			
			if(hidHostAddress == null){
				throw new IllegalArgumentException("Null address");
			}
			
			System.out.println("Looking for: " + hidHostAddress);
			DeviceInfo deviceInfo = devicesByAddress.get(hidHostAddress);
			if(deviceInfo == null){
				System.out.println("Invalid HIDHost address: " + hidHostAddress + 
					". Bluez is unaware of any such device. Ignoring address.");
				continue;
			}
			
			if(!deviceInfo.isPaired()){
				System.out.println("HIDHost with address: " + hidHostAddress + 
					" is not paired with the Xeblix server. Ignoring HIDHost.");
			continue;
			}
			tempHIDHosts.add(deviceInfo);
		}
		return  Collections.unmodifiableList(tempHIDHosts);
	}
	
	DeviceInfo getConnectedHostInfo() {
		return connectedHostInfo;
	}

	void setConnectedHostInfo(DeviceInfo connectedHostInfo) {
		this.connectedHostInfo = connectedHostInfo;
	}


}
