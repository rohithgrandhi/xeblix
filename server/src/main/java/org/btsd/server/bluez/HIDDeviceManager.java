package org.btsd.server.bluez;

import org.btsd.server.util.ActiveThread;

public /*final*/ abstract class HIDDeviceManager extends ActiveThread {

	/*private ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
	private HIDDeviceState state = HIDDeviceState.DISCONNECTED;
	private DBusManagerImpl dbusManager;
	private ActiveThread btsdActiveObject;
	
	
	private BluetoothHIDSocketActiveObject inputHIDSocketActiveObject;
	private BluetoothHIDSocketActiveObject controlHIDSocketActiveObject;
	private BTHIDWriterActiveObject btHIDWriterActiveObject;
	
	private HIDHostInfo hostInfo;
	
	private static final int INPUT_UUID = 3453459;
	private static final int CONTROL_UUID = 3153189;
	
	public HIDDeviceManager(DBusManagerImpl dbusManager, ActiveThread btsdActiveObject){
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
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		if(msg.getType() == MessagesEnum.HID_CONNECT_TO_PRIMARY_HOST){
			connectToPrimaryHost();
		}else if(msg.getType() == MessagesEnum.MESSAGE_FROM_CLIENT){
			
			HIDFromClientMessage clientMessage = (HIDFromClientMessage)msg;
			
			if(clientMessage.getHidCommand() == HIDCommands.STATUS){
				sendHIDStatus(clientMessage.getRemoteDeviceAddress());
			}else if(clientMessage.getHidCommand() == HIDCommands.HID_HOSTS){
				sendHIDHosts(clientMessage.getRemoteDeviceAddress());
			}else if(clientMessage.getHidCommand() == HIDCommands.PAIR_MODE){
				setupPairMode(clientMessage.getRemoteDeviceAddress());
			}else if(clientMessage.getHidCommand() == HIDCommands.CLIENT_PINCODE_RESPONSE){
				String pincode = "";
				if(clientMessage.getClientArguments().length >= 3){
					pincode = clientMessage.getClientArguments()[2];
				}
				sendPincodeResponse(clientMessage.getRemoteDeviceAddress(), pincode);
			}else if(clientMessage.getHidCommand() == HIDCommands.CLIENT_PINCODE_CANCEL){
				
				System.out.println("Received request from client to cancel Pairing.");
				sendPincodeResponse(clientMessage.getRemoteDeviceAddress(), null);
			}else if(clientMessage.getHidCommand() == HIDCommands.CANCEL_PAIR_MODE){
				
				cancelPairMode(clientMessage.getRemoteDeviceAddress());
			}else if(clientMessage.getHidCommand() == HIDCommands.CONNECT_TO_HOST){
				
				connectToHost(clientMessage.getRemoteDeviceAddress(), clientMessage.getClientArguments()[2]);
			}else if(clientMessage.getHidCommand() == HIDCommands.CONNECT_TO_HOST_CANCEL){
				
				connectToHostCancel(clientMessage.getRemoteDeviceAddress());
			}else if(clientMessage.getHidCommand() == HIDCommands.KEYCODE){
				
				btHIDWriterActiveObject.addMessage(clientMessage);
				
			}else{
				throw new UnsupportedOperationException("Implement me");
			}
		}else if(msg.getType() == MessagesEnum.HID_CONNECTION_INIT_RESULT){
			
			if(state != HIDDeviceState.DISCONNECTED && 
				state != HIDDeviceState.PAIR_MODE && 
				state != HIDDeviceState.PROBATIONALLY_CONNECTED){
				//not expecting this so ignore it
				System.out.println("Received unexpected message: " + msg.getType().getDescription() + 
					" while in state: " + state.getDescription());
				return;
			}
			
			HIDConnectionInitResultMessage message = (HIDConnectionInitResultMessage)msg;
			if(message.getUuid() == INPUT_UUID){
				//got the input connection
				System.out.println("New InputHID Connection. Creating BT HID Writer.");
				if(btHIDWriterActiveObject != null){
					//TODO: in the future will need to support multiple Bluetooth HID connections
					throw new UnsupportedOperationException("Failed to setup BluetoothHIDWriter " +
						"ActiveObject. A BluetoothHIDWriter is already setup.");
				}else{
					btHIDWriterActiveObject = new BTHIDWriterActiveObject();
					btHIDWriterActiveObject.start();
					btHIDWriterActiveObject.addMessage(new HIDInitMessage(
						message.getUuid(),message.getConnection()));
				}
				
				if(message.isNewConnection()){
					this.dbusManager.setDeviceNotDiscoverable();
					
					this.hostInfo = new HIDHostInfo(message.getAddress(), 
							message.getFriendlyName(), false);
				}
				
				/*state = HIDDeviceState.CONNECTED;
				btsdActiveObject.addMessage(new HIDConnectionComplete());
				*/
			/*}else if(message.getUuid() == CONTROL_UUID){
				//will probably use this in the future but for now do nothing
				System.out.println("New ControlHID Connection. ");
				if(!message.isNewConnection()){
					//client initiated a reconnect, only the control socket has been opened, now
					//need to open the input socket. The control socket must be connected before 
					//we can open the input socket else you will get an error
					inputHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
					inputHIDSocketActiveObject.start();
					inputHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(
						this, message.getAddress(), 13,INPUT_UUID));
				}
			}
		}else if(msg.getType() == MessagesEnum.VALIDATE_HID_CONNECT){
			
			if(state == HIDDeviceState.PROBATIONALLY_CONNECTED || 
				state == HIDDeviceState.DISCONNECTED){
				
				System.out.println("Validating connection with HID Host");
				ValidateHIDConnection message = (ValidateHIDConnection)msg;
				
				
				//make sure the hidWriter and both hidSockets are all running
				if(this.inputHIDSocketActiveObject != null && this.inputHIDSocketActiveObject.isAlive() && 
					this.controlHIDSocketActiveObject != null && this.controlHIDSocketActiveObject.isAlive() && 
					this.btHIDWriterActiveObject != null && this.btHIDWriterActiveObject.isAlive()){
					
					System.out.println("Connected to HID Host");
					//if hostInfo is not null then we have a new connection so store it
					if(hostInfo != null){
						addHIDHost(hostInfo);
						hostInfo = null;
					}
					this.state = HIDDeviceState.CONNECTED;
					//removed HIDConnectionComplete, now will just return a status
					//btsdActiveObject.addMessage(new HIDConnectionComplete());
				}else{
					
					System.out.println("Connection to HID Host failed. Resetting PairMode");
					
					//connection failed so reset PairMode
					if(this.inputHIDSocketActiveObject != null){
						this.inputHIDSocketActiveObject.interrupt();
						this.inputHIDSocketActiveObject = null;
					}
					if(this.controlHIDSocketActiveObject != null){
						this.controlHIDSocketActiveObject.interrupt();
						this.controlHIDSocketActiveObject = null;
					}
					if(this.btHIDWriterActiveObject != null){
						this.btHIDWriterActiveObject.addMessage(new ShutdownMessage());
						this.btHIDWriterActiveObject = null;
					}
					
					setupPairMode(null);
					
					//TODO: rename PIN_REQUEST_AUTH_FAILED to HIDConnectionFailed
					btsdActiveObject.addMessage(new FromClientResponseMessage(
							message.getRemoteDeviceAddress(), "PIN_REQUEST_AUTH_FAILED"));
				}
			}else{
				//ignore
				System.out.println("Ignoring AuthAgentAuthFailed message. Must be in state: " + 
						HIDDeviceState.PROBATIONALLY_CONNECTED.getDescription() + " but currently in: " + 
						state.getDescription());
			}
		}else if(msg.getType() == MessagesEnum.HID_HOST_DISCONNECT){
			
			if(state == HIDDeviceState.CONNECTED || 
				state == HIDDeviceState.PROBATIONALLY_CONNECTED){
				//make sure both HID sockets are killed, they are both blocking so need to
				//interrupt them
				this.inputHIDSocketActiveObject.interrupt();
				this.inputHIDSocketActiveObject = null;
				this.controlHIDSocketActiveObject.interrupt();
				this.controlHIDSocketActiveObject = null;
				this.btHIDWriterActiveObject.addMessage(new ShutdownMessage());
				this.btHIDWriterActiveObject = null;
				
				state = HIDDeviceState.DISCONNECTED;
				
				//let the clients know we are disconnected
				btsdActiveObject.addMessage(msg);
			}
		}
	}
	
	private void sendPincodeResponse(String remoteDeviceAddress, String pincode){
		
		if(state != HIDDeviceState.PAIR_MODE && 
			state != HIDDeviceState.PROBATIONALLY_CONNECTED){
			//not in pair mode so ignore
			System.out.println("Ignoring message: PINCodeResponse. Not in required state: " + 
				HIDDeviceState.PAIR_MODE.getDescription() + ". Currently in state: " + state.getDescription());
			return;
		}
		
		this.dbusManager.getAgent().setPinCode(pincode);
		btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, null));
		if(pincode != null){
			//stay is pair_mode, wait for host to respond
			//if authentication fails there is no message from HID Host, so schedule
			//a failed message. If we have not heard from the HID Host then assume failure, 
			//else we have heard from the HID Host and the message can be ignored.
			state = HIDDeviceState.PROBATIONALLY_CONNECTED;
			addMessage(new ValidateHIDConnection(remoteDeviceAddress), 5000);
		}
	}
	
	private void cancelPairMode(String remoteDeviceAddress){
		
		if(state != HIDDeviceState.PAIR_MODE){
			//ignore, not in pair mode
			return;
		}
		
		System.out.println("Exiting Pair Mode");
		
		this.dbusManager.setDeviceNotDiscoverable();
		
		this.inputHIDSocketActiveObject.interrupt();
		this.inputHIDSocketActiveObject = null;
		this.controlHIDSocketActiveObject.interrupt();
		this.controlHIDSocketActiveObject = null;
		
		state = HIDDeviceState.DISCONNECTED;
		
		btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, null));
	}
	
	private void connectToHostCancel(String remoteDeviceAddress){
		
		if(state != HIDDeviceState.PROBATIONALLY_CONNECTED){
			//ignore
			System.out.println("Ignoring client request to CONNECT_TO_HOST_CANCEL" + 
				". Currently in state: " + state.getDescription() + 
				". Must be in " + HIDDeviceState.PROBATIONALLY_CONNECTED.getDescription() + 
				" to accept this command.");
			return;
		}
		//make sure both HID sockets are killed
		if(this.inputHIDSocketActiveObject != null){
			this.inputHIDSocketActiveObject.interrupt();
			this.inputHIDSocketActiveObject = null;
		}
		if(this.controlHIDSocketActiveObject != null){
			this.controlHIDSocketActiveObject.interrupt();
			this.controlHIDSocketActiveObject = null;
		}
		
		state = HIDDeviceState.DISCONNECTED;
		btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, null));
	}
	
	private void connectToHost(String remoteDeviceAddress, String hostAddress){
		
		if(state != HIDDeviceState.DISCONNECTED){
			//ignore connect from any state except disconnected
			System.out.println("Ignoring client request CONNECT_TO_HOST to:" + 
				hostAddress + ". Currently in state: " + state.getDescription() + 
				". Must be in " + HIDDeviceState.DISCONNECTED.getDescription() + 
				" to accept this command.");
			return;
		}
		
		//validate the hostAddress
		ArrayList<HIDHostInfo> hidHosts = this.hidHosts;
		boolean validHIDHost = false;
		for(HIDHostInfo hostInfo: hidHosts){
			if(hostInfo.getAddress().equalsIgnoreCase(hostAddress)){
				validHIDHost = true;
				break;
			}
		}
		
		if(!validHIDHost){
			btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, 
				"INVALID_HOST_ADDRESS"));
			return;
		}
		
		//state = HIDDeviceState.CONNECTING;
		state = HIDDeviceState.PROBATIONALLY_CONNECTED;
		addMessage(new ValidateHIDConnection(remoteDeviceAddress), 5000);
		
		controlHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
		controlHIDSocketActiveObject.start();
		controlHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(this, hostAddress, 11,CONTROL_UUID));
		
		//everything is setup to connect, send response to client
		btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, null));
	}
	
	private void setupPairMode(String remoteDeviceAddress){
		
		//if we are connected to any device we need to disconnect
		if(state == HIDDeviceState.CONNECTED){
			//need to disconnect from any device currently connected
			throw new UnsupportedOperationException("Implement me");
		}
		
		if(state != HIDDeviceState.DISCONNECTED){
			//not in a state to hande the request, ignore it
			return;
		}
		
		
		this.state = HIDDeviceState.PAIR_MODE;
		
		inputHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
		inputHIDSocketActiveObject.start();
		inputHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(this, 13, INPUT_UUID));
		
		controlHIDSocketActiveObject = new BluetoothHIDSocketActiveObject();
		controlHIDSocketActiveObject.start();
		controlHIDSocketActiveObject.addMessage(new HIDConnectionInitMessage(this, 11, CONTROL_UUID));
		
		this.dbusManager.setDeviceDiscoverable();
		
		//only send to remoteDeviceAddress is one is passed in
		if(remoteDeviceAddress != null){
			//ok now in pair mode, send response to client
			btsdActiveObject.addMessage(new FromClientResponseMessage(remoteDeviceAddress, null));
		}
	}
	
	private void sendHIDHosts(String remoteDeviceAddress){
		
		JSONArray hidHosts = new JSONArray();
		for(HIDHostInfo hostInfo: this.hidHosts){
			JSONObject hidHost = new JSONObject();
			try{
				hidHost.put("address", hostInfo.getAddress());
				hidHost.put("name", hostInfo.getName());
				hidHost.put("primary", hostInfo.isPrimary());
			}catch(JSONException ex){
				//will never get here
				throw new IllegalStateException(ex.getMessage(), ex);
			}
			hidHosts.put(hidHost);
		}
		
		btsdActiveObject.addMessage(new FromClientResponseMessage(
			remoteDeviceAddress, hidHosts.toString()));
	}

	private void sendHIDStatus(String remoteDeviceAddress){
		
		if(state == HIDDeviceState.DISCONNECTED){
			JSONObject result = new JSONObject();
			try{
				result.put("status", "disconnected");
			}catch(JSONException ex){
				//will never get here
				throw new IllegalStateException(ex.getMessage(), ex);
			}
			
			btsdActiveObject.addMessage(new FromClientResponseMessage(
					remoteDeviceAddress, result.toString()));
		}else{
			
			JSONObject result = new JSONObject();
			try{
				result.put("status", "connected");
				//TODO: add info about the HID Host
			}catch(JSONException ex){
				//will never get here
				throw new IllegalStateException(ex.getMessage(), ex);
			}
			
			btsdActiveObject.addMessage(new FromClientResponseMessage(
					remoteDeviceAddress, result.toString()));
		}
		
	}
	
	private void addHIDHost(HIDHostInfo hostInfo){
		
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
	
	private void connectToPrimaryHost(){
		
		if(state != HIDDeviceState.DISCONNECTED){
			System.out.println("Ignoring HID_CONNECT_TO_PRIMARY_HOST request. " +
				"Currently in status: " + state.getDescription() + ". Must be in status: " + 
				HIDDeviceState.DISCONNECTED.getDescription() + " to service this request.");
			return;
		}
		
		
		HIDHostInfo primaryHost = null;
		for(HIDHostInfo hostInfo: this.hidHosts){
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
		
		throw new UnsupportedOperationException("finish me");
		/*try{
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			//LocalDevice.getLocalDevice().getDiscoveryAgent().
		}catch(Exception ex){
			
		}*/
	/*	
	}
	
	private static enum HIDDeviceState{
		
		DISCONNECTED("Disconnected"),
		PROBATIONALLY_CONNECTED("Probationally Connected"),
		CONNECTED("Connected"),
		//CONNECTING("Connecting"),
		PAIR_MODE("Pair Mode");
		
		private String description;
		
		HIDDeviceState(String description){
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
		
		
	}
	
	private static class HIDHostInfo implements Serializable{
		
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
	*/
}
