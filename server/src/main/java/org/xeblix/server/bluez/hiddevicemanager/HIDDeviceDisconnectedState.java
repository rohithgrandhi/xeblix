package org.xeblix.server.bluez.hiddevicemanager;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDConnectionInitMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.PinRequestMessage;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.util.ActiveThread;


public final class HIDDeviceDisconnectedState implements HIDDeviceManagerState {

	private static HIDDeviceDisconnectedState instance;
	
	public static final String STATUS = "disconnected";
	
	private HIDDeviceDisconnectedState(){}
	
	public static synchronized HIDDeviceManagerState getInstance(){
		
		if(instance == null){
			instance = new HIDDeviceDisconnectedState();
		}
		
		return instance;
	}
	
	public void clientMessageConnectToHost(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		String hostAddress = null;
		try{
			hostAddress = message.getClientArguments().getString(
				FromClientResponseMessage.HOST_ADDRESS);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
		System.out.println("Received request to Connect to host: " + hostAddress);
		
		//validate the hostAddress
		List<DeviceInfo> hidHosts = deviceManager.getHidHosts();
		DeviceInfo hostToConnect = null;
		System.out.println("HIDHosts size: " + hidHosts.size());
		for(DeviceInfo hostInfo: hidHosts){
			if(hostInfo.getAddress().equalsIgnoreCase(hostAddress)){
				hostToConnect = hostInfo;
				break;
			}
		}
		
		if(hostToConnect == null){
			System.out.println("Unable to connect to address: " + hostAddress + 
					". Failed to find a matching HIDHost");
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),HIDDeviceManagerHelper.getFailedResponse(STATUS)));
			return;
		}
		
		System.out.println("Connecting to HIDHost: " + hostAddress);
		deviceManager.setConnectedHostInfo(hostToConnect);
		connectoToHost(deviceManager, message.getRemoteDeviceAddress(), hostAddress);
		
	}

	private void connectoToHost(HIDDeviceManager deviceManager,
			String remoteDeviceAddress, String hostAddress) {
		
		deviceManager.addMessage(new ValidateHIDConnection(remoteDeviceAddress), 
			deviceManager.getValidateConnectionTimeout());
		
		ActiveThread controlHIDSocketActiveObject = deviceManager.getHidFactory().getBluetoothHIDSocketActiveObject();
		controlHIDSocketActiveObject.start();
		controlHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(deviceManager, hostAddress, 11, 
			HIDDeviceManager.CONTROL_UUID));
		deviceManager.setControlHIDSocketActiveObject(controlHIDSocketActiveObject);
		
		//everything is setup to connect, send response to clients if this connect was client initiated
		if(remoteDeviceAddress != null){
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					HIDDeviceManagerHelper.getStatus(HIDDeviceProbationallyConnectedState.STATUS)));
		}
		
		deviceManager.updateState(HIDDeviceProbationallyConnectedState.getInstance());
	}

	public void clientMessageConnectToHostCancel(
			HIDDeviceManager deviceManager, HIDFromClientMessage message) {
		
		//should not get this message in disconnected state
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));
	}

	public void clientMessageHIDHosts(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		HIDDeviceManagerHelper.sendHIDHosts(deviceManager.getHidHosts(), 
			deviceManager.getBtsdActiveObject(), message.getRemoteDeviceAddress());
	}

	public void clientMessageKeyCode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
			". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));
	}

	public void clientMessagePairMode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		ActiveThread inputHIDSocketActiveObject = deviceManager.getHidFactory().getBluetoothHIDSocketActiveObject();
		inputHIDSocketActiveObject.start();
		inputHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(deviceManager, 13, HIDDeviceManager.INPUT_UUID));
		deviceManager.setInputHIDSocketActiveObject(inputHIDSocketActiveObject);
		
		ActiveThread controlHIDSocketActiveObject = deviceManager.getHidFactory().getBluetoothHIDSocketActiveObject();
		controlHIDSocketActiveObject.start();
		controlHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(deviceManager, 11, HIDDeviceManager.CONTROL_UUID));
		deviceManager.setControlHIDSocketActiveObject(controlHIDSocketActiveObject);
		
		deviceManager.getDbusManager().setDeviceDiscoverable();
		
		//send a status changed to all clients
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			HIDDeviceManagerHelper.getStatus(HIDDevicePairModeState.STATUS)));
		
		deviceManager.updateState(HIDDevicePairModeState.getInstance());
	}

	public void clientMessagePairModeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {

		//canceling PairMode takes the server to DISCONNECTED state so just send back an ack
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getSuccessResponse()));
	}

	public void clientMessagePinCodeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Received invalid client message: " + message.getHidCommand() + 
			" while in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));
	}

	public void hidHostPinCodeCancel(HIDDeviceManager deviceManager,
			HIDHostCancelPinRequestMessage message) {
		
		System.out.println("Invalid state to received client message: " + 
				message.getType().getDescription() + ". Currently in state: " + 
				getClass().getSimpleName());
		
	}
	
	public void clientMessagePinCodeResponse(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Received invalid client message: " + message.getHidCommand() + 
				" while in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));
		
	}

	public void clientMessageStatus(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			message.getRemoteDeviceAddress(), getStatus()));
		
	}

	public void hidConnectionResult(HIDDeviceManager deviceManager,
			HIDConnectionInitResultMessage message) {
		
		//if you got here, something has gone wrong
		String error = "Received invalid HIDConnectionInitResult in state: " + getClass().getSimpleName() + 
			". Not expecting connection results. Attempting to shutdown all Bluetooth HID sockets/connections";
		System.out.println(error);
		
		//these should already be shutdown, but just in case shut them down again
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		
	}

	public void hidHostDisconnect(HIDDeviceManager deviceManager,
			HIDHostDisconnect message) {
		
		//ignore the message
		HIDDeviceManagerHelper.ignoringMessage(message, this);
		
	}

	public void validateHIDConnection(HIDDeviceManager deviceManager,
			ValidateHIDConnection message) {
		
		//ignore the message
		HIDDeviceManagerHelper.ignoringMessage(message, this);
	}
	
	private static JSONObject getStatus(){
		
		return HIDDeviceManagerHelper.getStatus(STATUS);
	}
	
	public void validatePinRequest(HIDDeviceManager deviceManager,
			PinRequestMessage pinRequestMessage) {
		
		//ignore the message
		HIDDeviceManagerHelper.ignoringMessage(pinRequestMessage, this);
		
	}
	
	public void clientMessageUnpairDevice(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		HIDDeviceManagerHelper.unpairHIDHost(deviceManager, message);		
	}
}
