package org.xeblix.server.bluez;

import java.util.concurrent.Semaphore;

import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.xeblix.server.messages.HIDHostCancelPinRequestMessage;
import org.xeblix.server.messages.PinRequestMessage;
import org.xeblix.server.util.ActiveThread;

public final class BluezAuthenticationAgentImpl implements BluezAuthenticationAgent {

	private final Semaphore available = new Semaphore(0, true);
	private ActiveThread mainActiveObject = null;
	private String pinCode;
	
	public BluezAuthenticationAgentImpl(ActiveThread mainActiveObject){
		if(mainActiveObject == null){
			throw new IllegalArgumentException("This method does not accept null parameters");
		}
		this.mainActiveObject = mainActiveObject;
	}
	
	public void DisplayPasskey(Path device, UInt32 passkey, byte entered) {
		System.out.println("DisplayPasskey");
	}
	
	public void Authorize(Path device, String uuid) {
		System.out.println("authorize");
	}

	public void Cancel() {
		System.out.println("cancel");
		this.mainActiveObject.addMessage(new HIDHostCancelPinRequestMessage());
		this.available.release();
	}

	public void ConfirmModeChange(String mode) {
		System.out.println("confirmModeChange");
	}

	public void Release() {
		System.out.println("Release");
	}

	public void RequestConfirmation(Path device, UInt32 passkey) {
		System.out.println("RequestConfirmation");
	}

	public UInt32 RequestPasskey(Path device) {
		
		System.out.println("Passkey request from " + device.getPath() + " :");
		return null;
		
		/*String passkey = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			passkey = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to get the pin!");
			System.exit(1);
		}

		return new UInt32(passkey);*/
	}

	public String RequestPinCode(Path device) {
		
		System.out.println("Pincode request from " + device.getPath() + " :");
		
		this.mainActiveObject.addMessage(new PinRequestMessage());
		
		try{
			//it is possible to have setPinCode and Cancel called be different
			//threads thus causing an extra permit, drain any permits before
			//calling acquire
			this.available.drainPermits();
			this.available.acquire();
		}catch(InterruptedException ex){
			System.out.println("Got Interrupted");
			ex.printStackTrace();
			return null;	
		}
		
		System.out.println("Responding to HID Host with PinCode");
		
		return getPinCode();
	}

	public boolean isRemote() {
		System.out.println("isRemote");
		return false;
	}

	public synchronized String getPinCode() {
		return pinCode;
	}

	public synchronized void setPinCode(String pinCode) {
		this.pinCode = pinCode;
		this.available.release();
	}

	
}
