package test.org.xeblix.server.bluez.hiddevicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.bluetooth.L2CAPConnection;

import org.bluez.Error.Canceled;
import org.bluez.Error.Rejected;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.junit.Test;
import org.xeblix.server.bluez.BluezAuthenticationAgent;
import org.xeblix.server.bluez.DBusManager;
import org.xeblix.server.bluez.DeviceInfo;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceConnectedState;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceDisconnectedState;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceManager;
import org.xeblix.server.bluez.hiddevicemanager.HIDDevicePairModeState;
import org.xeblix.server.bluez.hiddevicemanager.HIDDeviceProbationallyConnectedState;
import org.xeblix.server.bluez.mock.MockActiveObject;
import org.xeblix.server.bluez.mock.MockHIDFactory;
import org.xeblix.server.messages.FromClientResponseMessage;
import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.util.ActiveThread;

public class HIDDeviceProbationallyConnectedTest {

	@Test
	public void testHIDDeviceProbationallyConnectedState(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, Integer.MAX_VALUE, 
					hidHosts);
			
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//###############################################
			//clientMessageHIDHosts
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getHidHosts()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":[{\"address\":\"12345678910\",\"connected\":false,\"name\":\"Test Host\"}]," +
					"\"type\":\"HIDHosts\"}", message.getReponse());
			
			//###############################################
			//clientMessageStatus
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getHidStatus()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", message.getReponse());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testInvalidMessages(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, Integer.MAX_VALUE,
					hidHosts);
			
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//################################################
			//clientMessageConnectToHost
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("1111111111")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//##############################################
			//clientMessageKeyCode
			mainThread.getMessages().clear();
			List<Integer> keycodes = new ArrayList<Integer>();
			keycodes.add(4);
			keycodes.add(123);
			keycodes.add(11);
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getKeycodes(keycodes)));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//#############################################
			//clientMessagePairMode
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//########################################
			//clientMessagePairModeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairModeCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//######################################
			//clientMessagePinCodeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//###################################
			//clientMessagePinCodeResponse
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeResponse("12344")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			//#######################################
			//test pinCodeCancel, should be ignored
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDHostCancelPinRequestMessage());
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testClientMessageConnectToHostCancelFromDisconnected(){
		
		//########################################
		//clientMessageConnectToHostCancel
		disconnectMessageFromDisconnectedState(true);
	}

	@Test
	public void testClientMessageConnectToHostCancelFromPairMode(){
		
		//########################################
		//clientMessageConnectToHostCancel
		disconnectFromPairModeState(true);
	}

	@Test
	public void testHidConnectionResult(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, Integer.MAX_VALUE,
					hidHosts);
		
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//hidConnectionResult
			assertEquals(1, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			//first try a new control connection, nothing should happen
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(1, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			
			//now try a non-new control connection, should get a second socket
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", false));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			
			//now try new input connection
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(1, hidFactory.getWriterCount());
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			
			//now try a non-new input connection
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(2, hidFactory.getWriterCount());
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
				
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testValidateHIDConnectionFromDisconnected(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, 75,
					hidHosts);
		
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//start with no host connections, should transition to disconnected state
			mainThread.getMessages().clear();
			//wait for the validateMessage to be consumed
			try{Thread.sleep(175);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(1, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}

		try{
			
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, 125,
					hidHosts);
		
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//simulate a control connection, should still transition to disconnected state
			mainThread.getMessages().clear();
			
			assertEquals(1, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", false));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			//wait for the validateMessage to be consumed
			try{Thread.sleep(200);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
		
		try{
			
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			hidHosts.add(new DeviceInfo("Test Host 2","10987654321",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, 175,
					hidHosts);
		
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//simulate a control and input connection, should connected state
			mainThread.getMessages().clear();
			
			assertEquals(1, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "10987654321", "Test Host 2", false));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", false));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(1, hidFactory.getWriterCount());
			
			//wait for the validateMessage to be consumed
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"address\":\"12345678910\",\"status\":\"Connected\"," +
					"\"hostName\":\"Test Host\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(1, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testValidateHIDConnectionFromPairMode(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, 75,
					hidHosts);
		
			transitionViaPairMode(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//start with no host connections, should transition to disconnected state
			mainThread.getMessages().clear();
			
			//wait for the validateMessage to be consumed, since getting to pairmode killing
			//any connections are recreating them , reset the hidFactory counts
			hidFactory.resetCount();
			try{Thread.sleep(175);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}

		try{
			
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, 125,
					hidHosts);
		
			transitionViaPairMode(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//simulate a control connection, should still transition to disconnected state
			mainThread.getMessages().clear();
			
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "12345678910", "Test Host", true));
			
			try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			//wait for the validateMessage to be consumed, since getting to pairmode killing
			//any connections are recreating them , reset the hidFactory counts
			hidFactory.resetCount();
			try{Thread.sleep(200);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
		
		try{
			
			final ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgent getAgent() {return new BluezAuthenticationAgent(){
					public String getPinCode() {return null;}
					public void setPinCode(String pinCode) {}
					public void Authorize(Path device, String uuid)throws Rejected, Canceled {}
					public void Cancel() {}
					public void ConfirmModeChange(String mode) throws Rejected,Canceled {}
					public void DisplayPasskey(Path device, UInt32 passkey,byte entered) {}
					public void Release() {}
					public void RequestConfirmation(Path device, UInt32 passkey)throws Rejected, Canceled {}
					public UInt32 RequestPasskey(Path device) throws Rejected,Canceled {return null;}
					public String RequestPinCode(Path device) throws Rejected,Canceled {return null;}
					public void setDefaultPinCode() {}
					public boolean isRemote() {return false;}
				};}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {return Collections.unmodifiableList(hidHosts);}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory,175, 1500);
			deviceManager.start();
			
			transitionViaPairMode(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//validateHIDConnection
			//simulate a control and input connection, should connected state
			mainThread.getMessages().clear();
			
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "10987654321", "Test Host 2", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(0, hidFactory.getWriterCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, 
				new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "10987654321", "Test Host 2", true));
			
			try{Thread.sleep(25);}catch(InterruptedException ex){ex.printStackTrace();}
			
			//simulate bluez returning the newly paired device, this must be done after the input 
			//connection and before the validateHIDConnection is called
			hidHosts.add(new DeviceInfo("Test Host 2","10987654321",true, false));
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(),
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(1, hidFactory.getWriterCount());
			
			//wait for the validateMessage to be consumed 
			try{Thread.sleep(125);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"address\":\"10987654321\",\"status\":\"Connected\"," +
					"\"hostName\":\"Test Host 2\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertEquals(2, hidFactory.getSocketCount());
			assertEquals(1, hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testHidHostDisconnectFromDisconnected(){
		
		
		//#########################################
		//hidHostDisconnect
		disconnectMessageFromDisconnectedState(false);
	}
	
	@Test
	public void testHidHostDisconnectFromPairMode(){
		
		//########################################
		//hidHostDisconnect
		disconnectFromPairModeState(false);
	}
	
	@Test
	public void testClientMessageUnpairDevice(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		
		try{
			final ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgent getAgent() {return new BluezAuthenticationAgent(){
					public String getPinCode() {return null;}
					public void setPinCode(String pinCode) {}
					public void Authorize(Path device, String uuid)throws Rejected, Canceled {}
					public void Cancel() {}
					public void ConfirmModeChange(String mode) throws Rejected,Canceled {}
					public void DisplayPasskey(Path device, UInt32 passkey,byte entered) {}
					public void Release() {}
					public void RequestConfirmation(Path device, UInt32 passkey)throws Rejected, Canceled {}
					public UInt32 RequestPasskey(Path device) throws Rejected,Canceled {return null;}
					public String RequestPinCode(Path device) throws Rejected,Canceled {return null;}
					public void setDefaultPinCode() {}
					public boolean isRemote() {return false;}
				};}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {return Collections.unmodifiableList(hidHosts);}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory,175, 325);
			deviceManager.start();
			
			transitionViaPairMode(mainThread, deviceManager, hidFactory);
			
			//###################################################
			//start with a non-existant device
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getRemovePairedHost("invalid")));
			
			//probationally connected passes the buck to whichever state comes next, either connected
			//disconencted, or pair mode, in this case nothing should happen for a total of 750ms
			//then probationally connected will transition back to pair mode because there are 
			//no connections.
			try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), deviceManager.getDeviceManagerState());
			
			
			try{Thread.sleep(750);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(2, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			message = (FromClientResponseMessage)mainThread.getMessages().get(1);
			assertEquals("{\"value\":[{\"address\":\"12345678910\",\"connected\":false,\"name\":\"Test Host\"}]," +
					"\"type\":\"HIDHosts\"}", message.getReponse());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	private void transitionViaPairMode(MockActiveObject mainThread,
			HIDDeviceManager deviceManager, MockHIDFactory hidFactory) {
		//get to the right state
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getPairMode()));
		
		try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDevicePairModeState.getInstance(),deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
		assertTrue(message.isBroadcastMessage());
		
		//now send pincode response
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getPincodeResponse("12344")));
		
		try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
			deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", 
				message.getReponse());
		assertTrue(message.isBroadcastMessage());
		assertEquals(2, hidFactory.getSocketCount());
		assertEquals(0, hidFactory.getWriterCount());
	}

	private HIDDeviceManager createDeviceManager(MockActiveObject mainThread,
			MockHIDFactory hidFactory, int validateConnectionInterval, 
			final List<DeviceInfo> hidHosts) {
		HIDDeviceManager deviceManager;
		deviceManager = new HIDDeviceManager(new DBusManager(){
			public BluezAuthenticationAgent getAgent() {return new BluezAuthenticationAgent(){
				public String getPinCode() {return null;}
				public void setPinCode(String pinCode) {}
				public void Authorize(Path device, String uuid)throws Rejected, Canceled {}
				public void Cancel() {}
				public void ConfirmModeChange(String mode) throws Rejected,Canceled {}
				public void DisplayPasskey(Path device, UInt32 passkey,byte entered) {}
				public void Release() {}
				public void RequestConfirmation(Path device, UInt32 passkey)throws Rejected, Canceled {}
				public UInt32 RequestPasskey(Path device) throws Rejected,Canceled {return null;}
				public String RequestPinCode(Path device) throws Rejected,Canceled {return null;}
				public void setDefaultPinCode() {}
				public boolean isRemote() {return false;}
			};}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
			public List<DeviceInfo> listDevices() {return Collections.unmodifiableList(hidHosts);}
			public boolean removePairedDevice(String address) {return true;}
			public DeviceInfo getDeviceInfo(String path) {return null;}
		}, mainThread , hidFactory,validateConnectionInterval, 150);
		deviceManager.start();
		return deviceManager;
	}
	
	private void transitionViaDisconnected(MockActiveObject mainThread,
			HIDDeviceManager deviceManager, MockHIDFactory hidFactory) {
		//get to the right state
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getConnectToHost("12345678910")));
		
		try{Thread.sleep(60);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
			deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", message.getReponse());
		assertTrue(message.isBroadcastMessage());
		assertEquals(1, hidFactory.getSocketCount());
	}
	
	private void simulateHostConnect(HIDDeviceManager deviceManager,
			MockHIDFactory hidFactory, boolean newConnection) {
		deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
			new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, 
			"12345678910", "Test Host", newConnection));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(2, hidFactory.getSocketCount());
		
		//now input
		deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, new L2CAPConnection(){
			public int getReceiveMTU() throws IOException {return 0;}
			public int getTransmitMTU() throws IOException {return 0;}
			public boolean ready() throws IOException {return false;}
			public int receive(byte[] inBuf) throws IOException {return 0;}
			public void send(byte[] data) throws IOException {}
			public void close() throws IOException {}
		}, "12345678910", "Test Host", newConnection));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(2, hidFactory.getSocketCount());
		assertEquals(1, hidFactory.getWriterCount());
		
		assertTrue(hidFactory.getConnections().get(0).isAlive());
		assertTrue(hidFactory.getConnections().get(1).isAlive());
		assertTrue(hidFactory.getConnections().get(2).isAlive());
	}
	
	private void disconnectMessageFromDisconnectedState(boolean clientMessage) {
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, Integer.MAX_VALUE,
					hidHosts);
		
			transitionViaDisconnected(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//clientMessageConnectToHostCancel
			
			//send a control and input connect
			simulateHostConnect(deviceManager, hidFactory, false);
			
			//finally send the host cancel
			mainThread.getMessages().clear();
			if(clientMessage){
				deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHostCancel()));
			}else{
				deviceManager.addMessage(new HIDHostDisconnect());
			}
			
			try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertFalse(hidFactory.getConnections().get(0).isAlive());
			assertFalse(hidFactory.getConnections().get(1).isAlive());
			assertFalse(hidFactory.getConnections().get(2).isAlive());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	private void disconnectFromPairModeState(boolean clientMessage) {
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory, Integer.MAX_VALUE,
					hidHosts);
		
			transitionViaPairMode(mainThread, deviceManager, hidFactory);
			
			//##############################################
			//clientMessageConnectToHostCancel
			
			//send a control and input connect
			simulateHostConnect(deviceManager, hidFactory, true);
			
			//finally send the host cancel
			mainThread.getMessages().clear();
			if(clientMessage){
				deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHostCancel()));
			}else{
				deviceManager.addMessage(new HIDHostDisconnect());
			}
			
			try{Thread.sleep(175);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(),deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
			assertFalse(hidFactory.getConnections().get(0).isAlive());
			assertFalse(hidFactory.getConnections().get(1).isAlive());
			assertFalse(hidFactory.getConnections().get(2).isAlive());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
}
