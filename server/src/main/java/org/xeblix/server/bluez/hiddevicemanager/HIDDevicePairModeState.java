package org.xeblix.server.bluez.hiddevicemanager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDConnectToPrimaryHostMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.HIDInitMessage;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.util.ActiveThread;

public final class HIDDevicePairModeState implements HIDDeviceManagerState {

	private static HIDDevicePairModeState instance;
	
	public static final String STATUS = "PAIR_MODE";
	
	private HIDDevicePairModeState(){}
	
	public static synchronized HIDDeviceManagerState getInstance(){
		
		if(instance == null){
			instance = new HIDDevicePairModeState();
		}
		
		return instance;
	}
	
	public void clientMessageConnectToHost(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		//if we get here, probably got a confused client, send failed and current status
		System.out.println("Received invalid client message: " + message.getHidCommand() + 
				" while in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),  HIDDeviceManagerHelper.getFailedResponse(STATUS)));
	}

	public void clientMessageConnectToHostCancel(
			HIDDeviceManager deviceManager, HIDFromClientMessage message) {
		
		//Not a valid message for this state
		System.out.println("Received invalid client message: " + message.getHidCommand() + 
				" while in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),  HIDDeviceManagerHelper.getFailedResponse(STATUS)));

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
				message.getRemoteDeviceAddress(),  HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessagePairMode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		//already in pair mode so do nothing except send response to the requesting client 
		//(rather than every client)
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			message.getRemoteDeviceAddress(),HIDDeviceManagerHelper.getStatus(HIDDevicePairModeState.STATUS)));
	}

	public void clientMessagePairModeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Exiting Pair Mode");
		
		deviceManager.getDbusManager().setDeviceNotDiscoverable();
		
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			HIDDeviceManagerHelper.getStatus(HIDDeviceDisconnectedState.STATUS)));
		
		deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());
	}

	public void clientMessagePinCodeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		sendPincodeResponse(deviceManager, message.getRemoteDeviceAddress(), null);
	}

	public void clientMessagePinCodeResponse(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		try{
			sendPincodeResponse(deviceManager, message.getRemoteDeviceAddress(), 
				message.getClientArguments().getString(FromClientResponseMessage.PINCODE));
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private void sendPincodeResponse(HIDDeviceManager deviceManager, String remoteDeviceAddress, String pincode){
		
		deviceManager.getDbusManager().getAgent().setPinCode(pincode);
		//if pincode is null stay in PAIR_MODE
		if(pincode != null){
			//send message to all clients about the state change
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					HIDDeviceManagerHelper.getStatus(HIDDeviceProbationallyConnectedState.STATUS)));
			//if authentication fails there is no message from HID Host, so schedule
			//a failed message. If we have not heard from the HID Host then assume failure, 
			//else we have heard from the HID Host and the message can be ignored.
			deviceManager.addMessage(new ValidateHIDConnection(remoteDeviceAddress), 
				deviceManager.getValidateConnectionTimeout());
			deviceManager.updateState(HIDDeviceProbationallyConnectedState.getInstance());	
		}else{
			//user selected cancel, send back a response
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				remoteDeviceAddress, HIDDeviceManagerHelper.getSuccessResponse()));
		}
	} 
	
	public void clientMessageStatus(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			message.getRemoteDeviceAddress(),getStatus()));
	}

	public void hidConnectToPrimaryHost(HIDDeviceManager deviceManager,
			HIDConnectToPrimaryHostMessage message) {
		
		//ignore
		HIDDeviceManagerHelper.ignoringMessage(message, this);

	}

	public void hidConnectionResult(HIDDeviceManager deviceManager,
			HIDConnectionInitResultMessage message) {
		
		if(message.getUuid() == HIDDeviceManager.INPUT_UUID){
			
			//got the input connection
			System.out.println("New InputHID Connection. Creating BT HID Writer.");
			
			ActiveThread btHIDWriterActiveObject = deviceManager.getHidFactory().getBtHIDWriterActiveObject();
			btHIDWriterActiveObject.start();
			btHIDWriterActiveObject.addMessage(new HIDInitMessage(
				message.getUuid(),message.getConnection()));
			
			deviceManager.setBtHIDWriterActiveObject(btHIDWriterActiveObject);
			
			deviceManager.getDbusManager().setDeviceNotDiscoverable();
			
			//store the hostInfo, we will need it later if we get connected
			deviceManager.setHostInfo(new HIDHostInfo(message.getAddress(), 
					message.getFriendlyName(), false));
			
		}else if(message.getUuid() == HIDDeviceManager.CONTROL_UUID){
			System.out.println("New ControlHID Connection. ");
			//not interested at this time in the control, perhaps in the future this will change
		}

	}

	public void hidHostDisconnect(HIDDeviceManager deviceManager,
			HIDHostDisconnect message) {
		
		//host has decided to disconnect during the paring process, go back to disconnected state
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
			HIDDeviceManagerHelper.getStatus(HIDDeviceDisconnectedState.STATUS)));
		
		deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());
	}

	public void validateHIDConnection(HIDDeviceManager deviceManager,
			ValidateHIDConnection message) {
		
		//the only way I can think of to get this message in this state is
		//if a host disconnects while in ProbationallyConnected state before
		//the ValidateHIDConnection message is dequeued (assuming got to ProbationallyConnected
		//through PairMode and not re-connect)
		HIDDeviceManagerHelper.ignoringMessage(message, this);
		
		//should not get validateHIDConnection in this state but go ahead and
		//reset pair mode anyways
		/*System.out.println("Connection to HID Host failed. Resetting PairMode");
		
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());

		//skip adding the message in the queue, this is a priority don't want another message to
		//get infront of this one
		deviceManager.handleMessage(new HIDFromClientMessage(
			deviceManager.getBtsdActiveObject(), null, "HIDCommand:PAIR_MODE"));
		*/
	}

	private static JSONObject getStatus(){
		
		return HIDDeviceManagerHelper.getStatus(STATUS);
	}
	
}
