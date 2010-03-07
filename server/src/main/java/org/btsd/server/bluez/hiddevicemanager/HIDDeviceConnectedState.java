package org.btsd.server.bluez.hiddevicemanager;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDConnectionInitResultMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.ValidateHIDConnection;
import org.json.JSONException;
import org.json.JSONObject;

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

	public void hidConnectToPrimaryHost(HIDDeviceManager deviceManager,
			HIDConnectToPrimaryHostMessage message) {
		
		//ignore
		HIDDeviceManagerHelper.ignoringMessage(message, this);

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

	public static JSONObject getStatus(HIDHostInfo hidHostInfo){
		JSONObject toReturn =HIDDeviceManagerHelper.getStatus(STATUS);
		try{
			toReturn.put(FromClientResponseMessage.HOST_NAME, hidHostInfo.getName());
			toReturn.put(FromClientResponseMessage.HOST_ADDRESS, hidHostInfo.getAddress());
		}catch(JSONException ex){
			
		}
		return HIDDeviceManagerHelper.getStatus(STATUS);
	}
}
