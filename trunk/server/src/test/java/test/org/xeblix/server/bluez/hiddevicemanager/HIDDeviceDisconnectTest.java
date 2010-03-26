package test.org.xeblix.server.bluez.hiddevicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.bluetooth.L2CAPConnection;

import junit.framework.Assert;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.xeblix.server.bluez.BluezAuthenticationAgentImpl;
import org.xeblix.server.bluez.DBusManager;
import org.xeblix.server.bluez.DeviceInfo;
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
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.util.ActiveThread;

public class HIDDeviceDisconnectTest {

	@Test
	public void testHIDDeviceDisconnectState(){
		
		//Assert.fail("Test clientMessageUnpairDevice for all states");
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
		mainThread = new MockActiveObject();
		mainThread.start();
		deviceManager = new HIDDeviceManager(new DBusManager(){
			public BluezAuthenticationAgentImpl getAgent() {return null;}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
			public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
			public boolean removePairedDevice(String address) {return true;}
			public DeviceInfo getDeviceInfo(String path) {return null;}
		}, mainThread , new MockHIDFactory());
		deviceManager.start();
		
		//###############################################
		//clientMessageStatus
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getHidStatus()));
		
		//give the thread time to process the message
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
		
		//###############################################
		//clientMessageHIDHosts
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getHidHosts()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"value\":[],\"type\":\"HIDHosts\"}", message.getReponse());
		
		//###############################################
		//hidHostDisconnect
		//already disconnected, this message should be ignored
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDHostDisconnect());
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(0, mainThread.getMessages().size());
		
		//###############################################
		//clientMessagePairModeCancel
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getPairModeCancel()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"value\":\"SUCCESS\",\"type\":\"result\"}", message.getReponse());
		
		}finally{
			cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testInvalidRequests(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
		mainThread = new MockActiveObject();
		mainThread.start();
		deviceManager = new HIDDeviceManager(new DBusManager(){
			public BluezAuthenticationAgentImpl getAgent() {return null;}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
			public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
			public boolean removePairedDevice(String address) {return true;}
			public DeviceInfo getDeviceInfo(String path) {return null;}
		}, mainThread , new MockHIDFactory());
		deviceManager.start();
		
		//###############################################
		//clientMessageKeyCode
		mainThread.getMessages().clear();
		List<Integer> keyCodes=  new ArrayList<Integer>();
		keyCodes.add(4);
		keyCodes.add(123);
		keyCodes.add(11);
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getKeycodes(keyCodes)));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
		
		//###############################################
		//clientMessagePinCodeCancel
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getPincodeCancel()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
		
		//###############################################
		//clientMessagePinCodeResponse
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getPincodeResponse("12344")));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
		
		//###############################################
		//clientMessageConnectToHostCancel
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getConnectToHostCancel()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
		
		//#######################################
		//test pinCodeCancel, should be ignored
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDHostCancelPinRequestMessage());
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
		assertEquals(0, mainThread.getMessages().size());
		
		}finally{
			cleanup(mainThread, deviceManager);
		}
		
	}
	
	@Test
	public void testClientMessageConnectToHost(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			//##########################################
			//clientMessageConnectToHost
			final ArrayList<DeviceInfo> hidHosts = new ArrayList<DeviceInfo>();
			hidHosts.add(new DeviceInfo("Test Host","12345678910",true, false));
			HashSet<String> hidHostAddresses = new HashSet<String>();
			hidHostAddresses.add("12345678910");
			HIDDeviceDisconnectTest.saveHIDHosts(hidHostAddresses);
			
			
			//start with an invalid address
			mainThread = new MockActiveObject();
			mainThread.start();
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgentImpl getAgent() {return null;}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {
					System.out.println("Returning HIDHosts: " + hidHosts.size());
					return hidHosts;
					}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , new MockHIDFactory());
			deviceManager.start();
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("1111111111")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
					message.getReponse());
			
			
			//now try valid address
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("12345678910")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", 
					message.getReponse());
			assertTrue(message.isBroadcastMessage());
			
		}finally{
			cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testClientMessagePairMode(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			//##########################################
			//clientMessagePairMode
			//start with an invalid address
			mainThread = new MockActiveObject();
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread.start();
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgentImpl getAgent() {return null;}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(0, hidFactory.getSocketCount());
			
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.getRemoteDeviceAddress() == null);
			//when entering PairMode should have created two BluetoothHIDSocketActiveObjects
			assertEquals(2, hidFactory.getSocketCount());
			
		}finally{
			cleanup(mainThread, deviceManager);
		}
		
	}
	
	@Test
	public void testHidConnectionResult(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			//##########################################
			//HidConnectionResult
			mainThread = new MockActiveObject();
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread.start();
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgentImpl getAgent() {return null;}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(0, hidFactory.getSocketCount());
			
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1, 1l, new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "00000", "test_host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertTrue(mainThread.getMessages().isEmpty());
			
		}finally{
			cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testValidateHIDConnection(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			//##########################################
			//validateHIDConnection
			mainThread = new MockActiveObject();
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread.start();
			deviceManager = new HIDDeviceManager(new DBusManager(){
				public BluezAuthenticationAgentImpl getAgent() {return null;}
				public void registerAgent(ActiveThread mainActiveObject) {}
				public void registerSDPRecord() {}
				public void setDeviceDiscoverable() {}
				public void setDeviceHidden() {}
				public void setDeviceNotDiscoverable() {}
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(0, hidFactory.getSocketCount());
			
			deviceManager.addMessage(new ValidateHIDConnection("1234"));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertTrue(mainThread.getMessages().isEmpty());
			
		}finally{
			cleanup(mainThread, deviceManager);
		}
	}
	
	public static void cleanup(MockActiveObject mainThread,
			HIDDeviceManager deviceManager) {
		if(mainThread != null){
			mainThread.interrupt();
		}
		if(deviceManager != null){
			deviceManager.interrupt();
		}
		saveHIDHosts(new HashSet<String>());
	}
	
	public static void saveHIDHosts(HashSet<String> hidHostAddresses){
		
		try{
			SerializationUtils.serialize(hidHostAddresses, new FileOutputStream(new File("HIDHosts")));
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
