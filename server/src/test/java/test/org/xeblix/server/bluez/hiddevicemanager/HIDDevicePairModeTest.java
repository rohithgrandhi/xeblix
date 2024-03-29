package test.org.xeblix.server.bluez.hiddevicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.xeblix.server.messages.PinConfirmationMessage;
import org.xeblix.server.messages.ValidateHIDConnection;
import org.xeblix.server.util.ActiveThread;

public class HIDDevicePairModeTest {

	@Test
	public void testNoTransitionMessages(){
		
		//Assert.fail("Test pin confirmation");
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , new MockHIDFactory());
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//################################################
			//clientMessageHIDHosts
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", ServerMessages.getHidHosts()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":[],\"type\":\"HIDHosts\"}", message.getReponse());
			
			//#####################################################
			//clientMessagePairMode
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			
			//##################################################
			//clientMessagePinCodeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":\"SUCCESS\",\"type\":\"result\"}", message.getReponse());
			
			//#####################################################
			//clientMessageStatus
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getHidStatus()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//############################################
			//validateHIDConnection
			//this message will be ignored
			mainThread.getMessages().clear();
			deviceManager.addMessage(new ValidateHIDConnection("000000",(new Date()).getTime() + 1000));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , new MockHIDFactory());
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//###########################################
			//clientMessageConnectToHost
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHost("1111111111")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			
			//##############################################
			//clientMessageConnectToHostCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getConnectToHostCancel()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
			//#################################################
			//clientMessageKeyCode
			mainThread.getMessages().clear();
			List<Integer> keyCodes  = new ArrayList<Integer>();
			keyCodes.add(4);
			keyCodes.add(123);
			keyCodes.add(11);
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getKeycodes(keyCodes)));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"value\":\"FAILED\",\"type\":\"result\"}", message.getReponse());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testClientMessagePairModeCancel(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , new MockHIDFactory());
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//####################################################
			//clientMessagePairModeCancel
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairModeCancel()));
			
			try{Thread.sleep(75);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testClientMessagePinCodeResponse(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , new MockHIDFactory());
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//####################################################
			//clientMessagePinCodeResponse
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPincodeResponse("12344")));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceProbationallyConnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"ProbationallyConnected\",\"type\":\"status\"}", message.getReponse());
			assertTrue(message.isBroadcastMessage());
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testHidConnectionResult(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//####################################################
			//hidConnectionResult
			//start with a control connection, verify nothing happens
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDConnectionInitResultMessage(1,HIDDeviceManager.CONTROL_UUID, new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "00000", "test_host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(0,hidFactory.getWriterCount());
			
			//###############################################
			//now send an input connection
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDConnectionInitResultMessage(11, HIDDeviceManager.INPUT_UUID, new L2CAPConnection(){
				public int getReceiveMTU() throws IOException {return 0;}
				public int getTransmitMTU() throws IOException {return 0;}
				public boolean ready() throws IOException {return false;}
				public int receive(byte[] inBuf) throws IOException {return 0;}
				public void send(byte[] data) throws IOException {}
				public void close() throws IOException {}
			}, "00000", "test_host", true));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			assertEquals(1,hidFactory.getWriterCount());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testHidHostDisconnect(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//####################################################
			//hidHostDisconnect
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDHostDisconnect());
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testPincodeCancel(){
		
		
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//#####################################
			//Now send a HIDHostPincodeCancel, verify goes to disconnected state
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDHostCancelPinRequestMessage());
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDeviceDisconnectedState.getInstance(), 
					deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"disconnected\",\"type\":\"status\"}", message.getReponse());
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
	
	@Test
	public void testPinConfirmation(){
		MockActiveObject mainThread = null;
		HIDDeviceManager deviceManager = null;
		try{
		
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public List<DeviceInfo> listDevices() {return new ArrayList<DeviceInfo>();}
				public boolean removePairedDevice(String address) {return true;}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//#####################################
			//Now send a PinConfirmationMessage,
			mainThread.getMessages().clear();
			deviceManager.addMessage(new PinConfirmationMessage("TestPIN"));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), 
					deviceManager.getDeviceManagerState());
			assertEquals(0, mainThread.getMessages().size());
			
			
			
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
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
			
			MockHIDFactory hidFactory = new MockHIDFactory();
			mainThread = new MockActiveObject();
			mainThread.start();
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
				public boolean removePairedDevice(String address) {
					
					if(hidHosts.get(0).getAddress().equalsIgnoreCase(address)){
						hidHosts.clear();
					}
					
					return true;
				}
				public DeviceInfo getDeviceInfo(String path) {return null;}
			}, mainThread , hidFactory);
			deviceManager.start();

			//first get it in pair mode
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getPairMode()));
			
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			FromClientResponseMessage message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"status\":\"PAIR_MODE\",\"type\":\"status\"}", message.getReponse());
			
			//############################################
			//start with a non-existant device
			mainThread.getMessages().clear();
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getRemovePairedHost("invalid")));
			
			try{Thread.sleep(100);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":[{\"address\":\"12345678910\",\"connected\":false,\"name\":\"Test Host\"}]," +
					"\"type\":\"HIDHosts\"}", message.getReponse());
			
			//###############################################
			//now remove the last device
			mainThread.getMessages().clear();
			
			deviceManager.addMessage(new HIDFromClientMessage(mainThread, "test", 
					ServerMessages.getRemovePairedHost("12345678910")));
			try{Thread.sleep(50);}catch(InterruptedException ex){ex.printStackTrace();}
			
			assertEquals(HIDDevicePairModeState.getInstance(), deviceManager.getDeviceManagerState());
			assertEquals(1, mainThread.getMessages().size());
			message = (FromClientResponseMessage)mainThread.getMessages().get(0);
			assertEquals("{\"value\":[],\"type\":\"HIDHosts\"}", message.getReponse());
		}finally{
			HIDDeviceDisconnectTest.cleanup(mainThread, deviceManager);
		}
	}
}
