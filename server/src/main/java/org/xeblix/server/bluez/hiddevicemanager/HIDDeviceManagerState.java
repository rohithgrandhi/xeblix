package org.xeblix.server.bluez.hiddevicemanager;

import org.xeblix.server.messages.HIDConnectionInitResultMessage;
import org.xeblix.server.messages.HIDFromClientMessage;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.HIDHostDisconnect;
import org.xeblix.server.messages.PinRequestMessage;
import org.xeblix.server.messages.ValidateHIDConnection;

public interface HIDDeviceManagerState {

	/**
	 * Called when the HIDDeviceManager receives the client HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageStatus(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the HIDHost HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageHIDHosts(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the pairMode HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessagePairMode(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the pin code response HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessagePinCodeResponse(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the pin code cancel HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessagePinCodeCancel(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the Pair Mode cancel HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessagePairModeCancel(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the connect To host HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageConnectToHost(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the keyCode HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageKeyCode(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the connect to host cancel HIDFromClientMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageConnectToHostCancel(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when a client requests a HID Host be unpaired. This message can be received in any state
	 * @param deviceManager
	 * @param message
	 */
	public void clientMessageUnpairDevice(HIDDeviceManager deviceManager, HIDFromClientMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the HIDConnectionInitResultMessage.
	 * @param deviceManager
	 * @param message
	 */
	public void hidConnectionResult(HIDDeviceManager deviceManager, HIDConnectionInitResultMessage message);
	
	/**
	 * Called when the HIDDeviceManager receives the ValidateHIDConnection message
	 * @param deviceManager
	 * @param message
	 */
	public void validateHIDConnection(HIDDeviceManager deviceManager, ValidateHIDConnection message);
	
	/**
	 * Called when the HIDDeviceManager receives the HIDHostDisconnect message
	 * @param deviceManager
	 * @param message
	 */
	public void hidHostDisconnect(HIDDeviceManager deviceManager, HIDHostDisconnect message);
	
	/**
	 * Called when the HID Host we are trying to pair with cancels the pincode request.
	 * @param deviceManager
	 * @param message
	 */
	public void hidHostPinCodeCancel(HIDDeviceManager deviceManager, HIDHostCancelPinRequestMessage message);
	
	/**
	 * Validate the PinRequest coming from the HIDHost. Should only get 
	 * the message in PairMode.
	 * @param deviceManager
	 * @param pinRequestMessage
	 */
	public void validatePinRequest(HIDDeviceManager deviceManager, 
			PinRequestMessage pinRequestMessage);
}
