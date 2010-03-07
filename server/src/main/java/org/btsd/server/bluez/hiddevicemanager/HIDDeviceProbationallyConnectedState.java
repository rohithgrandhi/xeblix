package org.btsd.server.bluez.hiddevicemanager;

import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDConnectionInitMessage;
import org.btsd.server.messages.HIDConnectionInitResultMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.HIDInitMessage;
import org.btsd.server.messages.ValidateHIDConnection;
import org.btsd.server.util.ActiveThread;
import org.json.JSONException;
import org.json.JSONObject;


public final class HIDDeviceProbationallyConnectedState implements
		HIDDeviceManagerState {

	private static HIDDeviceProbationallyConnectedState instance = null;

	public static final String STATUS = "ProbationallyConnected";
	
	private HIDDeviceProbationallyConnectedState(){}
	
	public static synchronized HIDDeviceManagerState getInstance(){
		
		if(instance == null){
			instance = new HIDDeviceProbationallyConnectedState();
		}
		
		return instance;
	}
	
	public void clientMessageConnectToHost(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),  HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessageConnectToHostCancel(
			HIDDeviceManager deviceManager, HIDFromClientMessage message) {
		
		transitionToPreviousState(deviceManager,
			"Client requested host connect cancel. Returing to PairMode",
			"Client requested host connect cancel");
		
	}

	public void clientMessageHIDHosts(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		HIDDeviceManagerHelper.sendHIDHosts(deviceManager.getHidHosts(),deviceManager.getBtsdActiveObject(), 
			message.getRemoteDeviceAddress());
	}

	public void clientMessageKeyCode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(),HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessagePairMode(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessagePairModeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessagePinCodeCancel(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessagePinCodeResponse(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		System.out.println("Invalid state to received client message: " + message.getHidCommand() + 
				". Currently in state: " + getClass().getSimpleName());
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), HIDDeviceManagerHelper.getFailedResponse(STATUS)));

	}

	public void clientMessageStatus(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				message.getRemoteDeviceAddress(), getStatus()));

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
			
			ActiveThread btHIDWriterActiveObject = deviceManager.getHidFactory().
				getBtHIDWriterActiveObject();
			btHIDWriterActiveObject.start();
			btHIDWriterActiveObject.addMessage(new HIDInitMessage(
				message.getUuid(),message.getConnection()));
			
			deviceManager.setBtHIDWriterActiveObject(btHIDWriterActiveObject);
			
			//only set deviceNotDiscoverable and save the host info
			//if this is a new connection, else it must be a re-connect
			if(message.isNewConnection()){
				deviceManager.getDbusManager().setDeviceNotDiscoverable();
				
				deviceManager.setHostInfo(new HIDHostInfo(message.getAddress(), 
						message.getFriendlyName(), false));
			}
			
		}else if(message.getUuid() == HIDDeviceManager.CONTROL_UUID){
			System.out.println("New ControlHID Connection. ");
			//not interested at this time in the control, perhaps in the future this will change
			
			if(!message.isNewConnection()){
				//client initiated a reconnect, only the control socket has been opened, now
				//need to open the input socket. The control socket must be connected before 
				//we can open the input socket else you will get an error
				ActiveThread inputHIDSocketActiveObject = deviceManager.getHidFactory().
					getBluetoothHIDSocketActiveObject();
				
				inputHIDSocketActiveObject.start();
				inputHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(
					deviceManager, message.getAddress(), 13,HIDDeviceManager.INPUT_UUID));
				
				deviceManager.setInputHIDSocketActiveObject(inputHIDSocketActiveObject);
			}
		}

	}

	public void validateHIDConnection(HIDDeviceManager deviceManager,
			ValidateHIDConnection message) {
		
		System.out.println("Validating connection with HID Host");
		if(deviceManager.getInputHIDSocketActiveObject() != null && 
			deviceManager.getInputHIDSocketActiveObject().isAlive() && 
			deviceManager.getControlHIDSocketActiveObject() != null && 
			deviceManager.getControlHIDSocketActiveObject().isAlive() && 
			deviceManager.getBtHIDWriterActiveObject() != null && 
			deviceManager.getBtHIDWriterActiveObject().isAlive()){
			
			System.out.println("Connected to HID Host");
			//if hostInfo is not null then we have a new connection so store it
			if(deviceManager.getHostInfo() != null){
				deviceManager.addHIDHost(deviceManager.getHostInfo());
				deviceManager.setHostInfo(null);
				deviceManager.setConnectedHostInfo(deviceManager.getHostInfo());
			}
			
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
				HIDDeviceConnectedState.getStatus(deviceManager.getConnectedHostInfo())));
			
			deviceManager.updateState(HIDDeviceConnectedState.getInstance());
		}else{
			//validation failed
			transitionToPreviousState(deviceManager,
					"Connection to HID Host failed. Resetting PairMode.",
					"Connection to HID Host failed. Going back to Disconnected state.");
		}
	}

	public void hidHostDisconnect(HIDDeviceManager deviceManager,
			HIDHostDisconnect message) {
		
		transitionToPreviousState(deviceManager,
				"HID Host has disconnected. Resetting PairMode.",
				"HID Host has disconnected. Going back to Disconnected state.");
	}

	private static JSONObject getStatus(){
		
		return HIDDeviceManagerHelper.getStatus(STATUS);
	}
	
	private void transitionToPreviousState(HIDDeviceManager deviceManager, String pairModeMessage, 
		String disconnectedMessage) {
		
		//kill all HID connections then go to previous state
		HIDDeviceManagerHelper.disconnectFromHost(deviceManager);
		//clear any hostInfo
		deviceManager.setHostInfo(null);
		
		//need to figure out how we got to this state, if we were in the PairMode then go back there
		//else go to disconnected
		if(deviceManager.getPreviousState() == HIDDevicePairModeState.getInstance()){
			System.out.println(pairModeMessage);
			
			//don't bother sending out a message to all the clients, that will happen
			//during the call to handleMessage
			
			deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());
			
			//Add message PairMode message, skip adding the message in the queue, this is a priority 
			//don't want another message to get infront of this one
			JSONObject fakeClientMessage = new JSONObject();
			try{
				fakeClientMessage.put(FromClientResponseMessage.TYPE, "HIDCommand");
				fakeClientMessage.put(FromClientResponseMessage.VALUE, "PAIR_MODE");
			}catch(JSONException ex){
				throw new RuntimeException(ex.getMessage(), ex);
			}
			deviceManager.handleMessage(new HIDFromClientMessage(
				deviceManager.getBtsdActiveObject(), null,fakeClientMessage));
		}else{
			System.out.println(disconnectedMessage);
			
			deviceManager.updateState(HIDDeviceDisconnectedState.getInstance());
			
			deviceManager.getBtsdActiveObject().addMessage(new FromClientResponseMessage(
					HIDDeviceManagerHelper.getStatus(HIDDeviceDisconnectedState.STATUS)));
		}
	}
}
