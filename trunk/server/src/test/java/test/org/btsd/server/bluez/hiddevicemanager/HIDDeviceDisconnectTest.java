package test.org.btsd.server.bluez.hiddevicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.L2CAPConnection;

import org.apache.commons.lang.SerializationUtils;
import org.btsd.server.bluez.BluezAuthenticationAgentImpl;
import org.btsd.server.bluez.DBusManager;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceDisconnectedState;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager;
import org.btsd.server.bluez.hiddevicemanager.HIDDevicePairModeState;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceProbationallyConnectedState;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.bluez.mock.MockActiveObject;
import org.btsd.server.bluez.mock.MockHIDFactory;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDConnectionInitResultMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.ValidateHIDConnection;
import org.btsd.server.util.ActiveThread;
import org.junit.Test;

public class HIDDeviceDisconnectTest {

	@Test
	public void testHIDDeviceDisconnectState(){
		
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
		
		}finally{
			cleanup(mainThread, deviceManager);
		}
		
	}
	
	@Test
	public void testHidConnectToPrimaryHost(){

		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		//##########################################
		//hidConnectToPrimaryHost
		
		//add a non-primary hidHost, verify no connection is started
		ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
		hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
		saveHIDHosts(hidHosts);
		
		mainThread = new MockActiveObject();
		mainThread.start();
		deviceManager = new HIDDeviceManager(new DBusManager(){
			public BluezAuthenticationAgentImpl getAgent() {return null;}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
		}, mainThread , new MockHIDFactory());
		deviceManager.start();
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(0, mainThread.getMessages().size());

		//verify the non-primary hidHost was found
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
				ServerMessages.getHidHosts()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"value\":[{\"address\":\"12345678910\",\"primary\":false,\"name\":\"Test Host\"}]," +
			"\"type\":\"HIDHosts\"}", message.getReponse());
		
		//now send a connect to primary message and verify it was ignored
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDConnectToPrimaryHostMessage());
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(0, mainThread.getMessages().size());
		
		cleanup(mainThread, deviceManager);
		
		//#########################################
		//now add a primary hidHost
		hidHosts.clear();
		hidHosts.add(new HIDHostInfo("12345678910","Test Host",true));
		saveHIDHosts(hidHosts);
		
		mainThread = new MockActiveObject();
		mainThread.start();
		deviceManager = new HIDDeviceManager(new DBusManager(){
			public BluezAuthenticationAgentImpl getAgent() {return null;}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
		}, mainThread , new MockHIDFactory());
		deviceManager.start();
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(0, mainThread.getMessages().size());
		
		//verify the primary hidHost was found
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test",
				ServerMessages.getHidHosts()));
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
		assertEquals(1, mainThread.getMessages().size());
		message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"value\":[{\"address\":\"12345678910\",\"primary\":true,\"name\":" +
			"\"Test Host\"}],\"type\":\"HIDHosts\"}", message.getReponse());
		
		//now send a connect to primary message and verify a connection was started
		mainThread.getMessages().clear();
		deviceManager.addMessage(new HIDConnectToPrimaryHostMessage());
		
		try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
		
		assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), deviceManager.getDeviceManagerState());
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
			ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
			hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
			saveHIDHosts(hidHosts);
			
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
			assertEquals("{\"status\":\"disconnected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			
			//now try valid address
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("12345678910")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", message.getReponse());
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
		saveHIDHosts(new ArrayList<HIDHostInfo>());
	}
	
	public static void saveHIDHosts(ArrayList<HIDHostInfo> hidHosts){
		
		try{
			SerializationUtils.serialize(hidHosts, new FileOutputStream(new File(HIDHostInfo.class.getName())));
		}catch(IOException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
