package org.xeblix.server.bluez.hiddevicemanager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDConnectionInitMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.HIDInitMessage;
import org.xeblix.server.messages.PinRequestMessage;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.util.ActiveThread;


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

	public void hidHostPinCodeCancel(HIDDeviceManager deviceManager,
			HIDHostCancelPinRequestMessage message) {
		
		System.out.println("Invalid state to received client message: " + 
			message.getType().getDescription() + ". Currently in state: " + 
			getClass().getSimpleName());
		
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
				//for now device will always be discoverable
				//deviceManager.getDbusManager().setDeviceNotDiscoverable();
				deviceManager.setPossibleHidHostAddress(message.getAddress());
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
			deviceManager.getBtHIDWriterActiveObject().isAlive() && 
			//finally check if this is a new connection, if so make sure 
			//the device is a valid paired device
			(deviceManager.getPossibleHidHostAddress() == null ||  
				deviceManager.isPairedDevice(deviceManager.getPossibleHidHostAddress())) ){
			
			System.out.println("Connected to HID Host");
			//if hostInfo is not null then we have a new connection so store it
			if(deviceManager.getPossibleHidHostAddress() != null){
				DeviceInfo connectedDevice = deviceManager.addHIDHost(
						deviceManager.getPossibleHidHostAddress());
				deviceManager.setConnectedHostInfo(connectedDevice);
				deviceManager.setPossibleHidHostAddress(null);
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
		deviceManager.setPossibleHidHostAddress(null);
		
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
	
	public void validatePinRequest(HIDDeviceManager deviceManager,
			PinRequestMessage pinRequestMessage) {
		
		//send default response
		deviceManager.getDbusManager().getAgent().setDefaultPinCode();		
	}
	
	public void clientMessageUnpairDevice(HIDDeviceManager deviceManager,
			HIDFromClientMessage message) {
		
		//not in a good state to handle this message, but it back on the queue to be 
		//picked up after we leave this state
		System.out.println("Can't Unpair HID Host in ProbationallyConnectedState. Putting " +
			"UnpairDevice message back on the queue to be processed in another state.");
		deviceManager.addMessage(message, 750);
	}
}
