package org.btsd.server.bluez.hiddevicemanager;

import java.util.ArrayList;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDConnectionInitMessage;
import org.btsd.server.messages.HIDConnectionInitResultMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.ValidateHIDConnection;
import org.btsd.server.util.ActiveThread;
import org.json.JSONException;
import org.json.JSONObject;


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
	
	public void hidConnectToPrimaryHost(HIDDeviceManager deviceManager,
			HIDConnectToPrimaryHostMessage message) {
		
		HIDHostInfo primaryHost = null;
		for(HIDHostInfo hostInfo: deviceManager.getHidHosts()){
			if(hostInfo.isPrimary()){
				primaryHost = hostInfo;
				break;
			}
		}
		
		if(primaryHost == null){
			System.out.println("No primary host found. Unable to connect to primary host.");
			return;
		}
		
		System.out.println("Attempting to connect to Primary Host with address: " + 
			primaryHost.getAddress() + " name: " + primaryHost.getName());
		deviceManager.setConnectedHostInfo(primaryHost);
		connectoToHost(deviceManager, null, primaryHost.getAddress());
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
		
		//validate the hostAddress
		ArrayList<HIDHostInfo> hidHosts = deviceManager.getHidHosts();
		HIDHostInfo hostToConnect = null;
		for(HIDHostInfo hostInfo: hidHosts){
			if(hostInfo.getAddress().equalsIgnoreCase(hostAddress)){
				hostToConnect = hostInfo;
				break;
			}
		}
		
		if(hostToConnect == null){
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),HIDDeviceManagerHelper.getFailedResponse(STATUS)));
			return;
		}
		
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
}
