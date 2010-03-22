package org.xeblix.server.bluez.hiddevicemanager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.ValidateHIDConnection;

public class HIDDeviceConnectedState implements HIDDeviceManagerState {

	private static HIDDeviceConnectedState instance = null;
	
	private static final String STATUS = "Connected";
	
	private HIDDeviceConnectedState(){}
	
	public static synchronized HIDDeviceManagerState getInstance(){
		
		if(instance == null){
			instance = new HIDDeviceConnectedState();
		}
		
		return instance;
	}
	
	
	public void clientMessageConnectToHost(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));
	}

	public void clientMessageConnectToHostCancel(
			HIDDeviceManager deviceManager, HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));

	}

	public void clientMessageHIDHosts(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		HIDDeviceManagerHelper.sendHIDHosts(deviceManager.getHidHosts(),
			deviceManager.getBtsdActiveObject(),message.getRemoteDeviceAddress());

	}

	public void clientMessageKeyCode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {

		//just forward message to the HID Writer
		deviceManager.getBtHIDWriterActiveObject().addMessage(message);
	}

	public void clientMessagePairMode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));
		
	}

	public void clientMessagePairModeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));

	}

	public void clientMessagePinCodeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));

	}

	public void hidHostPinCodeCancel(HIDDeviceManager deviceManager,
			HIDHostCancelPinRequestMessage message) {
		
		System.out.println("Invalid state to received client message: " + 
				message.getType().getDescription() + ". Currently in state: " + 
				getClass().getSimpleName());
	}
	
	public void clientMessagePinCodeResponse(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		JSONObject response = HIDDeviceManagerHelper.getFailedResponse(STATUS);
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), response));

	}

	public void clientMessageStatus(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), getStatus(deviceManager.getConnectedHostInfo())));

	}

	public void hidConnectionResult(HIDDeviceManager deviceManager,
			HIDConnectionInitResultMessage message) {

		//ignore
		HIDDeviceManagerHelper.ignoringMessage(message, this);

	}

	public void hidHostDisconnect(HIDDeviceManager deviceManager,
			HIDHostDisconnect message) {
		
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				HIDDeviceManagerHelper.getStatus(HIDDeviceDisconnectedState.STATUS)));
		
		deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());
	}

	public void validateHIDConnection(HIDDeviceManager deviceManager,
			ValidateHIDConnection message) {
		
		HIDDeviceManagerHelper.ignoringMessage(message, this);

	}

	public static JSONObject getStatus(DeviceInfo hidHostInfo){
		JSONObject toReturn =HIDDeviceManagerHelper.getStatus(STATUS);
		try{
			toReturn.put(FromClientResponseMessage.HOST_NAME, hidHostInfo.getName());
			toReturn.put(FromClientResponseMessage.HOST_ADDRESS, hidHostInfo.getAddress());
		}catch(JSONException ex){
			ex.printStackTrace();
			throw new RuntimeException();
		}
		return toReturn;
	}
}
