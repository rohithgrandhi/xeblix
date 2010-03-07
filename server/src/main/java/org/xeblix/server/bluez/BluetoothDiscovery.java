package org.xeblix.server.bluez;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class BluetoothDiscovery implements DiscoveryListener {

	public List<RemoteDevice> remoteDevices = new ArrayList<RemoteDevice>();
	public Map<String, DeviceClass> remoteDeviceClasses = new HashMap<String, DeviceClass>();
	
	public List<ServiceRecord> serviceRecords= new ArrayList<ServiceRecord>();
	
	private Object lock;
	
	public BluetoothDiscovery(Object lock){
		this.lock =lock;
	}
	
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		
		if(btDevice != null){
			remoteDevices.add(btDevice);
			
			remoteDeviceClasses.put(btDevice.getBluetoothAddress(), cod);
		}

	}

	public void inquiryCompleted(int discType) {
		
		synchronized(lock){
            lock.notify();
        }

		switch (discType) {
	            case DiscoveryListener.INQUIRY_COMPLETED :
	                System.out.println("INQUIRY_COMPLETED");
	                break;
	            case DiscoveryListener.INQUIRY_TERMINATED :
	                System.out.println("INQUIRY_TERMINATED");
	                break;
	            case DiscoveryListener.INQUIRY_ERROR :
	                System.out.println("INQUIRY_ERROR");
	                break;
	            default :
	                System.out.println("Unknown Response Code");
	                break;
	        }

	}

	public void serviceSearchCompleted(int transID, int respCode) {
		
		synchronized(lock){
            lock.notify();
        }

		System.out.println("transId: " + transID + " respCode: " + respCode);
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for(ServiceRecord serviceRecord: servRecord){
			serviceRecords.add(serviceRecord);
		}
	}

}
