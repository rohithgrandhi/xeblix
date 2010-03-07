package test.org.btsd.server.bluez.hiddevicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.L2CAPConnection;

import org.bluez.Error.Canceled;
import org.bluez.Error.Rejected;
import org.btsd.server.bluez.BluezAuthenticationAgent;
import org.btsd.server.bluez.DBusManager;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceConnectedState;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceDisconnectedState;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager;
import org.btsd.server.bluez.hiddevicemanager.HIDDeviceManager.HIDHostInfo;
import org.btsd.server.bluez.mock.MockActiveObject;
import org.btsd.server.bluez.mock.MockHIDFactory;
import org.btsd.server.messages.FromClientResponseMessage;
import org.btsd.server.messages.HIDConnectToPrimaryHostMessage;
import org.btsd.server.messages.HIDConnectionInitResultMessage;
import org.btsd.server.messages.HIDFromClientMessage;
import org.btsd.server.messages.HIDHostDisconnect;
import org.btsd.server.messages.ValidateHIDConnection;
import org.btsd.server.messages.HIDFromClientMessage.HIDCommands;
import org.btsd.server.util.ActiveThread;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.junit.Test;

public class HIDDeviceConnectedTest {

	@Test
	public void testHIDDeviceConnectedState(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			
			ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
			hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
			HIDDeviceDisconnectTest.saveHIDHosts(hidHosts);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory);
			
			transitionToConenctedState(mainThread, deviceManager, hidFactory);
			
			//###############################################################
			//clientMessageHIDHosts
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test",ServerMessages.getHidHosts()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":[{\"address\":\"12345678910\",\"primary\":false,\"name\":\"Test Host\"}]," +
				"\"type\":\"HIDHosts\"}", message.getReponse());
			
			//############################################
			//clientMessageStatus
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", ServerMessages.getHidStatus()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"type\":\"status\"}", message.getReponse());
			
			//######################################
			//hidConnectToPrimaryHost
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDConnectToPrimaryHostMessage());
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
			//#####################################
			//hidConnectionResult
			mainThread.getMessages().clear();
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
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
			//#############################################
			//validateHIDConnection
			mainThread.getMessages().clear();
			deviceManager.addMessage(new ValidateHIDConnection("12345678910"));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testInvalidMessages(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			
			ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
			hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
			HIDDeviceDisconnectTest.saveHIDHosts(hidHosts);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory);
			
			transitionToConenctedState(mainThread, deviceManager, hidFactory);
			
			//#######################################
			//clientMessageConnectToHost
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("1111111111")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", 
				message.getReponse());
			
			//###############################################
			//clientMessageConnectToHostCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHostCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			//###########################################
			//clientMessagePairMode
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			//############################################
			//clientMessagePairModeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairModeCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			//#####################################################
			//clientMessagePinCodeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			//#################################################
			//clientMessagePinCodeResponse
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeResponse("12344")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"Connected\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}

	@Test
	public void testClientMessageKeyCode(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			
			ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
			hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
			HIDDeviceDisconnectTest.saveHIDHosts(hidHosts);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory);
			
			transitionToConenctedState(mainThread, deviceManager, hidFactory);
			
			//###############################################
			//clientMessageKeyCode\
			assertEquals(1, hidFactory.getWriterMessages().size());
			mainThread.getMessages().clear();
			List<Integer> keyCodes = new ArrayList<Integer>();
			keyCodes.add(4);
			keyCodes.add(123);
			keyCodes.add(11);
			HIDFromClientMessage hidFromClientMessage = new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getKeycodes(keyCodes)); 
			deviceManager.addMessage(hidFromClientMessage);
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(2, hidFactory.getWriterMessages().size());
			assertTrue(hidFactory.getWriterMessages().get(1) instanceof HIDFromClientMessage);
			assertEquals(HIDCommands.KEYCODE, ((HIDFromClientMessage)hidFactory.
					getWriterMessages().get(1)).getHidCommand());
			assertTrue(hidFromClientMessage == hidFactory.getWriterMessages().get(1));
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testHidHostDisconnect(){
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
			
			ArrayList<HIDHostInfo> hidHosts = new ArrayList<HIDHostInfo>();
			hidHosts.add(new HIDHostInfo("12345678910","Test Host",false));
			HIDDeviceDisconnectTest.saveHIDHosts(hidHosts);
			
			mainThread = new MockActiveObject();
			mainThread.start();
			MockHIDFactory hidFactory = new MockHIDFactory();
			deviceManager = createDeviceManager(mainThread, hidFactory);
			
			transitionToConenctedState(mainThread, deviceManager, hidFactory);
			
			//###############################################
			//hidHostDisconnect
			assertEquals(1, hidFactory.getWriterMessages().size());
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDHostDisconnect());
			
			try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)
				mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", 
				message.getReponse());
			assertFalse(hidFactory.getConnections().get(0).isAlive());
			assertFalse(hidFactory.getConnections().get(1).isAlive());
			assertFalse(hidFactory.getConnections().get(2).isAlive());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
		
	}
	
	private void transitionToConenctedState(MockActiveObject mainThread,
			HIDDeviceManager deviceManager, MockHIDFactory hidFactory) {
		//get to the right state
		deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
			ServerMessages.getConnectToHost("12345678910")));
		
		deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.CONTROL_UUID, 
			new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, 
			"12345678910", "Test Host", false));
		
		//now input
		deviceManager.addMessage(new HIDConnectionInitResultMessage(1, HIDDeviceManager.INPUT_UUID, new L2CAPConnection(){
			public int getReceiveMTU() throws IOException {return 0;}
			public int getTransmitMTU() throws IOException {return 0;}
			public boolean ready() throws IOException {return false;}
			public int receive(byte[] inBuf) throws IOException {return 0;}
			public void send(byte[] data) throws IOException {}
			public void close() throws IOException {}
		}, "12345678910", "Test Host", false));
		
		//wait for the ValidateConnectionMessage to process 
		try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
		
		
		assertEquals(2, hidFactory.getSocketCount());
		assertEquals(1, hidFactory.getWriterCount());
		
		assertTrue(hidFactory.getConnections().get(0).isAlive());
		assertTrue(hidFactory.getConnections().get(1).isAlive());
		assertTrue(hidFactory.getConnections().get(2).isAlive());
		
		assertEquals(HIDDeviceConnectedState.getInstance(), 
				deviceManager.getDeviceManagerState());
		assertEquals(2, mainThread.getMessages().size());
		FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
		assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", message.getReponse());
		assertTrue(message.isBroadcastMessage());
		message = (FromClientResponseMessage)mainThread.getMessages().get(1);
		assertEquals("{\"status\":\"Connected\",\"type\":\"status\"}", message.getReponse());
		assertTrue(message.isBroadcastMessage());
		
	}
	
	private HIDDeviceManager createDeviceManager(MockActiveObject mainThread,
			MockHIDFactory hidFactory) {
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
				public boolean isRemote() {return false;}
			};}
			public void registerAgent(ActiveThread mainActiveObject) {}
			public void registerSDPRecord() {}
			public void setDeviceDiscoverable() {}
			public void setDeviceHidden() {}
			public void setDeviceNotDiscoverable() {}
		}, mainThread , hidFactory,25);
		deviceManager.start();
		return deviceManager;
	}
}
