package org.xeblix.server.bluez;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bluez.dbus.DBusProperties;
import org.bluez.v4.Adapter;
import org.bluez.v4.Device;
import org.bluez.v4.Manager;
import org.bluez.v4.Service;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.xeblix.server.util.ActiveThread;


public final class DBusManagerImpl implements DBusManager{

	private DBusConnection conn = null;
	private Manager manager = null;
	private BluezAuthenticationAgentImpl authenticationAgent = null;

	private static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
			+ "<record>"
			+

			"<attribute id=\"0x0000\">"
			+ "<uint32 value=\"0x0001000e\"/>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0001\">"
			+ "<sequence>"
			+ "<uuid value=\"0x1124\" />"
			+ "</sequence>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0004\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x0100\" />"
			+ "<uint16 value=\"0x0011\" />"
			+ "</sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x0011\" />"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0005\">"
			+ "<sequence>"
			+ "<uuid value=\"0x1002\" />"
			+ "</sequence>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0006\">"
			+ "<sequence>"
			+ "<uint16 value=\"0x656E\"/>"
			+ "<uint16 value=\"0x006A\"/>"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</sequence>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0009\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x1124\"/>"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+
			//from rocketfish
			/*"<attribute id=\"0x0009\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x0011\"/>"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+*/

			"<attribute id=\"0x000d\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x0100\"/>"
			+ "<uint16 value=\"0x0013\" />"
			+ "</sequence>"
			+ "<sequence>"
			+ "<uuid value=\"0x0011\"/>"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0100\">"
			//+ "<text value=\"BTSD Device\" name=\"name\"/>"
			+	"<text encoding=\"hex\" value=\"426c75655a20484944206d6f7573652026204b6579626f617264\" />"				
			+ "</attribute>"
			+

			"<attribute id=\"0x0101\">"
			//+ "<text value=\"BTSD Device\" name=\"name\"/>"
			+ "<text encoding=\"hex\" value=\"426c75655a207669727475616c20636f6d626f\" />"
			+ "</attribute>"
			+

			"<attribute id=\"0x0102\">"
			//+ "<text value=\"Kenneth Lewelling\" name=\"name\"/>"
			+ "<text encoding=\"hex\" value=\"436f7079726967687420323030382d323030392056616c6572696f2056616c6572696f202d2076647631303040676d61696c2e636f6d\" />"
			+ "</attribute>"
			+

			"<attribute id=\"0x0200\">"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</attribute>"
			+

			"<attribute id=\"0x0201\">"
			+ "<uint16 value=\"0x0111\"/>"
			+ "</attribute>"
			+

			// HIDDevice Subclass (keyboard = 0x40, mouse=0x80,
			// keyboard+mouse=0xc0)
			//Note: when set to c0 most keys worked except "windows key" (hid keycode 227)
			"<attribute id=\"0x0202\">"
			+ "<uint8 value=\"0xc0\" />"
			+ "</attribute>"
			+
			/*"<attribute id=\"0x0202\">"
			+ "<uint8 value
			=\"0xc0\" />"
			+ "</attribute>"
			+
			*/
			
			// 30? 33? 000d? no idea what valid values are for this
			"<attribute id=\"0x0203\">"
			+ "<uint8 value=\"0x30\" />"
			+ "</attribute>"
			+
			//from rocketfish
			/*"<attribute id=\"0x0203\">"
			+ "<uint8 value=\"0x00\" />"
			+ "</attribute>"
			+*/
			
			//HIDVirtualCable true = device supports 1:1 bonding and expects automatically reconnect if connection is dropped
			"<attribute id=\"0x0204\">"
			+ "<boolean value=\"false\" />"
			+ "</attribute>"
			+
			//from rocketfish
			/*"<attribute id=\"0x0204\">"
			+ "<uint16 value=\"0x0000\" />"
			+ "</attribute>"
			+*/
			
			//HIDReconnectInitiate true means device initiates reconnect, false host should
			"<attribute id=\"0x0205\">"
			+ "<boolean value=\"true\" />"
			+ "</attribute>"
			+

			"<attribute id=\"0x0206\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uint8 value=\"0x22\"/>"
			//+ "<text encoding=\"hex\" value=\"05010906a101850175019508050719e029e715002501810295017508810395057501050819012905910295017503910395067508150026ff000507190029ff8100c0\" />"
			+ "<text encoding=\"hex\" value=\"05010906a101850175019508050719e029e715002501810295017508810395057501050819012905910295017503910395067508150026ff000507190029ff8100c0\" />"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+
			/*
			 * 05010906a101850175019508050719e029e715002501810295017508810395057501050819012905910295017503910395067508150026ff000507190029ff8100c0
				05 01 Usage Page (Generic Desktop),
				09 06 Usage (Keyboard),
				A1 01 Collection (Application),
				05 07 Usage Page (Key Codes);
				19 E0 Usage Minimum (224),
				29 E7 Usage Maximum (231),
				15 00 Logical Minimum (0),
				25 01 Logical Maximum (1),
				75 01 Report Size (1),
				95 08 Report Count (8),
				81 02 Input (Data, Variable, Absolute), ;modifier byte (report size 1bits * report count 8 = 1byte)
				
				95 01 Report Count (1),
				75 08 Report Size (8),
				81 01 Input (Constant,Ary,Abs),  ;Reserved byte (report size 8bits * report count 1 = 1byte)
				
				95 03 Report Count (3),
				75 01 Report Size (1),
				05 08 Usage Page (Page# for LEDs),
				19 01 Usage Minimum (1),
				29 03 Usage Maximum (3),
				91 02 Output (Data, Variable, Absolute), ;LED Report (report size 1bits * report count 3 = 3bits)
				
				95 01 Report Count (1),
				75 05 Report Size (5),
				91 01 Output (Constant,Ary,Abs), ;LED Report padding (report size 5bits * report count 1 = 5bits)
				
				95 06 Report Count (6),
				75 08 Report Size (8),
				15 00 Logical Minimum (0),
				26 ff 00 Logical Maximum(255),
				05 07 Usage Page (Key Codes),
				19 00 Usage Minimum (0),
				2a ff 00 Usage Maximum (255),
				81 00 Input (Data, Array), ;Key arrays (report size 8bits * report count 6 = 6bytes)
				
				C0 End Collection
			

			 */
			
			//05010906a1018501050719e029e71500250175019508810295017508810195057501050819012905910295017503910195067508150025650507190029658100c005010902a10185020901a1000509190129031500250175019503810275059501810105010930093109381581257f750895038106c0c0
			/*
			 * 05 01 Usage Page (Generic Desktop),
			 * 09 06 Usage (Keyboard),
			 * a1 01 Collection (Application),
			 * 85 01 Report ID (1) 
			 * 05 07 Usage Page (Key Codes);
			 * 19 e0 Usage Minimum (224),
			 * 29 e7 Usage Maximum (231),
			 * 15 00 Logical Minimum (0),
			 * 25 01 Logical Maximum (1),
			 * 75 01 Report Size (1),
			 * 95 08 Report Count (8),
			 * 81 02 Input (Data, Variable, Absolute), ;modifier byte (report size 1bits * report count 8 = 1byte)
			 *  
			 * 95 01 Report Count (1),
			 * 75 08 Report Size (8),
			 * 81 01 Input (Constant, Variable)
			 * 
			 * 95 05 Report Count (5),
			 * 75 01 Report Size (1),
			 * 05 08 Usage Page (Page# for LEDs),
			 * 19 01 Usage Minimum (1),
			 * 29 05 Usage Maximum (5),
			 * 91 02 Output (Data, Variable, Absolute), ;LED Report (report size 1bits * report count 5 = 5bits)
			 * 
			 * 95 01 Report Count (1),
			 * 75 03 Report Size (3),
			 * 91 01 Output (Constant, Variable)
			 * 
			 * 95 06 Report Count (6),
			 * 75 08 Report Size (8),
			 * 15 00 Logical Minimum (0),
			 * 25 65 Logical Maximum(101),
			 * 05 07 Usage Page (Key Codes),
			 * 19 00 Usage Minimum (0),
			 * 29 65 Usage Maximum (101),
			 * 81 00 Input (Data, Array), ;Key arrays (report size 8bits * report count 6 = 6bytes)
			 * 
			 * c0
			 * 05 01 Usage Page (Generic Desktop),
			 * 09 02 Usage (Mouse),
			 * a1 01 Collection (Application),
			 * 85 02 Report ID (2)
			 * 09 01 Usage (Pointer)
			 * a1 00 Collection (Physical)
			 * 05 09 Uage Page (Buttons)
			 * 19 01 Usage Minimum (1)
			 * 29 03 Usage Maximum (3)
			 * 15 00 Logical Minimum (0),
			 * 25 01 Logical Maximum (1),
			 * 75 01 Report Size (1)
			 * 95 03 Report Count (3)
			 * 81 02 Input (Data,Variable,Absolute) (report size 1bit * report count 3 = 3bits)
			 * 
			 * 75 05 Report Size (5)
			 * 95 01 Report Count (1)
			 * 81 01 Input (Constant) ; button padding (report size 5bit * report count 1 = 5bits)
			 * 
			 * 05 01 Usage Page (Generic Desktop),
			 * 09 30 Usage (X)
			 * 09 31 Usage (Y)
			 * 09 38 Usage (Z)
			 * 15 81 Logical Minimum (-127)
			 * 25 7f Logical Maximum (127)
			 * 75 08 Report Size (8)
			 * 95 03 Report Count (3)
			 * 81 06 Input (Data, Variable, Relative) ; (report size 8 * report count(3) = 3bytes)
			 * c0
			 * c0
			 */
			
			//05010906a1018501050719e029e71500250175019508810295017508810395057501050819012905910295017503910395067508150025650507190029658100c005010902a10185020901a1000509190129031500250175019503810275059501810105010930093109381581257f750895038106c0c0
			/*
			 * 05 01
			 * 09 06
			 * a1 01
			 * 85 01
			 * 05 07
			 * 19 e0
			 * 29 e7
			 * 15 00
			 * 250175019508810295017508810395057501050819012905910295017503910395067508150025650507190029658100c005010902a10185020901a1000509190129031500250175019503810275059501810105010930093109381581257f750895038106c0c0
			 */
			
			//rocketfish
			//05010906a101850175019508050719e029e715002501810295017508810395057501050819012905910295017503910395067508150026ff000507190029ff8100c0050c0901a101850215002501750195120a2a020a9d020a83010a23020a8a010a9e020a21020a24020a25020a26020a270209b609b509b709cd09e909ea09e28102950175068103c0050c0901a101850305010906a10205060920150026ff00750895018102c0c005010980a101850415002501750195030981098209838102950175058103c0050c0901a101850505010906a1020600ff2501750195020a03fe0a04fe810295068103c0c0050c0901a10185ff05069501750219242926810275068101c0
			/*
			 * 05 01 Usage Page (Generic Desktop),
			 * 09 06 Usage (Keyboard),
			 * a1 01 Collection (Application),
			 * 85 01 Report ID (1) 
			 * 75 01 Report Size (1),
			 * 95 08 Report Count (8),
			 * 05 07 Usage Page (Key Codes);
			 * 19 e0 Usage Minimum (224),
			 * 29 e7 Usage Maximum (231),
			 * 15 00 Logical Minimum (0),
			 * 25 01 Logical Maximum (1),
			 * 81 02 Input (Data, Variable, Absolute), ;modifier byte (report size 1bits * report count 8 = 1byte)
			 * 
			 * 95 01 Report Count (1),
			 * 75 08 Report Size (8),
			 * 81 03 Input (Constant)
			 * 
			 * 95 05 Report Count (5),
			 * 75 01 Report Size (1),
			 * 05 08 Usage Page (Page# for LEDs),
			 * 19 01 Usage Minimum (1),
			 * 29 05 Usage Maximum (5),
			 * 91 02 Output (Data, Variable, Absolute), ;LED Report (report size 1bits * report count 5 = 5bits)
			 * 
			 * 95 01 Report Count (1),
			 * 75 03 Report Size (3),
			 * 91 03 Output (Constant, Variable)
			 * 
			 * 95 06 Report Count (6),
			 * 75 08 Report Size (8),
			 * 15 00 Logical Minimum (0),
			 * 26 ff 00 Logical Maximum(255),
			 * 05 07 Usage Page (Key Codes),
			 * 19 00 Usage Minimum (0),
			 * 29 ff Usage Maximum (255),
			 * 81 00 Input (Data, Array), ;Key arrays (report size 8bits * report count 6 = 6bytes)
			 * c0 
			 * 
			 * 05 0c Usage Page  (Consumer Page)
			 * 09 01 Usage (Generic Desktop Controls)
			 * a1 01 Input (Data, Array), ;Key arrays (report size 8bits * report count 6 = 6bytes)
			 * 85 02 Report ID (2)
			 * 15 00 Logical Minimum (0),
			 * 25 01 Logical Maximum (1),
			 * 75 01 Report Size (1),
			 * 95 12 Report Count (12)
			 * 0a 2a 02 Usage (Ordinal Page)   
			 * 0a 9d 02
			 * 0a 83 01 
			 * 0a 23 02
			 * 0a 8a 01 
			 * 0a 9e 02
			 * 0a 21 02 
			 * 0a 24 02
			 * 0a 25 02 
			 * 0a 26 02
			 * 0a 27 02 
			 * 09 b6 
			 * 09 b5 
			 * 09 b7 
			 * 09 cd 
			 * 09 e9
			 * 09 ea
			 * 09 e2
			 * 81 02Input (Data, Variable, Absolute), (report size 1bits * report count 12 = 12bits)
			 * 
			 * 95 01 Report Count (1)
			 * 75 06 Report Size (6),
			 * 81 03 Input (Data) (report size 6bits * report count 1 = 6bits)
			 * c0 
			 * 
			 * 05 0c Usage Page  (Consumer Page)
			 * 09 01 Usage (Generic Desktop Controls)
			 * a1 01 Input (Data, Array), ;Key arrays (report size 8bits * report count 6 = 6bytes)
			 * 85 03 Report ID (3)
			 * 05 01 Usage Page (Generic Desktop),
			 * 09 06 Usage (Keyboard),
			 * a1 02 Collection (??),
			 * 05 06 Usage Page (Generic Device Controls Page)
			 * 09 20
			 * 15 00
			 * 26 ff 00
			 * 75 08
			 * 95 01
			 * 81 02
			 * c0
			 * c0
			 * 
			 * 05 01
			 * 09 80
			 * a1 01
			 * 85 04
			 * 15 00
			 * 25 01
			 * 75 01
			 * 95 03
			 * 09 81
			 * 09 82
			 * 09 83
			 * 81 02
			 * 95 01
			 * 75 05
			 * 81 03
			 * c0 
			 * 
			 * 05 0c
			 * 09 01
			 * a1 01
			 * 85 05
			 * 05 01
			 * 09 06
			 * a1 02
			 * 06 00
			 * ff 
			 * 25 01
			 * 75 01
			 * 95 02
			 * 0a 03
			 * fe 0a
			 * 04 fe
			 * 81 02
			 * 95 06
			 * 81 03
			 * c0
			 * c0
			 * 
			 * 05 0c
			 * 09 01
			 * a1 01
			 * 85 ff
			 * 05 06
			 * 95 01
			 * 75 02
			 * 19 24
			 * 29 26
			 * 81 02
			 * 75 06
			 * 81 01
			 * c0
			 */
			
			
			//05010902a10185010901a1000509190129031500250175019503810275059501810105010930093109381581257f750895038106c0
			/*
			 * 05 01
			 * 09 02
			 * a1 01
			 * 85 02
			 * 09 01 //Usage (Pointer)
			 * a1 00 //Collection Physical
			 * 05 09 Usage Page (Button)
			 * 19 01 Usage Min (1)
			 * 29 03 Usage max (3)
			 * 15 00 logical min (0)
			 * 25 01 Logical max (1)
			 * 75 01 Report size 1
			 * 95 03 Report Count 3
			 * 81 02 Input
			 * 
			 * 75 05 Report size 5
			 * 95 01 report count 1
			 * 81 01 Input
			 * 
			 * original
			 * 05 01 Usage Page (Generic Desktop)
			 * 09 30 Usage (x)
			 * 09 31 Usage (y)
			 * 09 38 Usage (wheel)
			 * 15 81 Logical Min (-127)
			 * 25 7f Logical Max (127)
			 * 75 08 Report size 8
			 * 95 03 Repor1 count 3
			 * 81 06 input
			 * c0
			 * c0
			 * 
			 */
			
			"<attribute id=\"0x0207\">"
			+ "<sequence>"
			+ "<sequence>"
			+ "<uint16 value=\"0x0409\"/>"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</sequence>"
			+ "</sequence>"
			+ "</attribute>"
			+

			//HIDDSPDisable - if true will not have an sdp service connection at the same time as a control/interupt connection
			"<attribute id=\"0x0208\">"
			+ "<boolean value=\"false\" />"
			+ "</attribute>"
			+

			/*
			 * "<attribute id=\"0x0209\">" + "<boolean value=\"true\" />" +
			 * "</attribute>" +
			 */
			"<attribute id=\"0x0200\">"
			+ "<boolean value=\"true\" />"
			+ "</attribute>"
			+
			
			"<attribute id=\"0x020a\">"
			+ "<boolean value=\"false\" />"
			+ "</attribute>"
			+

			"<attribute id=\"0x20b\">"
			+ "<uint16 value=\"0x0100\"/>"
			+ "</attribute>"
			+
			
			/*"<attribute id=\"0x20c\">"
			+ "<uint16 value=\"0x1f40\"/>"
			+ "</attribute>"
			+
*/
			"<attribute id=\"0x20d\">"
			+ "<boolean value=\"false\" />"
			+ "</attribute>"
			+

			//HIDBootDevice
			"<attribute id=\"0x20e\">"
			+ "<boolean value=\"true\" />"
			+ "</attribute>" +
			
			"</record>";

	public DBusManagerImpl() {

		try {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
			manager = (Manager) conn.getRemoteObject("org.bluez", "/",
					Manager.class);
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
		setDeviceHidden();
	}

	public void setDeviceHidden(){
		//set mode to undiscoverable
		Path adaptorLocation = manager.DefaultAdapter();
		try {
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			adapter.SetProperty(DBusProperties.getPropertyName(Adapter.Properties.Discoverable), 
					new Variant<Boolean>(Boolean.FALSE));
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		System.out.println("Adapter is now set to hidden. Hosts will be unable to discover this device.");
	}
	
	public List<DeviceInfo> listDevices(){
		Path adaptorLocation = manager.DefaultAdapter();
		List<DeviceInfo> toReturn = new ArrayList<DeviceInfo>();
		try {
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			Path[] devicePaths = adapter.ListDevices();
			for(Path devicePath: devicePaths){
				
				Device device = conn.getRemoteObject("org.bluez", devicePath.getPath(), Device.class);
				Map<String, Variant<?>> properties = device.GetProperties();
				String  name = DBusProperties.getStringValue(properties, Device.Properties.Name);
				String address = DBusProperties.getStringValue(properties, Device.Properties.Address);
				boolean paired = DBusProperties.getBooleanValue(properties, Device.Properties.Paired);
				boolean connected = DBusProperties.getBooleanValue(properties, Device.Properties.Connected);
				toReturn.add(new DeviceInfo(name, address, paired, connected));
			}
			
			return toReturn;
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public DeviceInfo getDeviceInfo(String path){
		try{
			Device device = conn.getRemoteObject("org.bluez", path, Device.class);
			if(device != null){
				Map<String, Variant<?>> properties = device.GetProperties();
				String  name = DBusProperties.getStringValue(properties, Device.Properties.Name);
				String address = DBusProperties.getStringValue(properties, Device.Properties.Address);
				boolean paired = DBusProperties.getBooleanValue(properties, Device.Properties.Paired);
				boolean connected = DBusProperties.getBooleanValue(properties, Device.Properties.Connected);
				return new DeviceInfo(name, address, paired, connected);
			}else{
				return null;
			}
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public boolean removePairedDevice(String addressToRemove){
		Path adaptorLocation = manager.DefaultAdapter();
		try {
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			Path[] devicePaths = adapter.ListDevices();
			for(Path devicePath: devicePaths){
				
				Device device = conn.getRemoteObject("org.bluez", devicePath.getPath(), Device.class);
				Map<String, Variant<?>> properties = device.GetProperties();
				String address = DBusProperties.getStringValue(properties, Device.Properties.Address);
				address = address.replace(":", "");
				if(addressToRemove.equalsIgnoreCase(address)){
					adapter.RemoveDevice(devicePath);
					return true;
				}
			}
			
			return false;
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public void setDeviceDiscoverable(){
		//set mode to undiscoverable
		Path adaptorLocation = manager.DefaultAdapter();
		try {
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			adapter.SetProperty(DBusProperties.getPropertyName(Adapter.Properties.Discoverable), 
					new Variant<Boolean>(Boolean.TRUE));
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		System.out.println("Adapter is now set to discoverable.");
	}
	
	public void setDeviceNotDiscoverable(){
		//set mode to undiscoverable
		Path adaptorLocation = manager.DefaultAdapter();
		try {
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			adapter.SetProperty(DBusProperties.getPropertyName(Adapter.Properties.Discoverable), 
					new Variant<Boolean>(Boolean.FALSE));
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		System.out.println("Adapter is now not discoverable.");
	}
	
	public void registerAgent(ActiveThread mainActiveObject) {

		Path adaptorLocation = manager.DefaultAdapter();
		// System.out.println(adaptorLocation.getPath());
		
		// now create and register an agent
		try {
			this.authenticationAgent = new BluezAuthenticationAgentImpl(mainActiveObject);
			conn.exportObject("/btsd/agent", this.authenticationAgent);
			Adapter adapter = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Adapter.class);
			adapter.RegisterAgent(new Path("/btsd/agent"), "DisplayYesNo");
		} catch (DBusException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		System.out.println("BluezAuthenticationAgent registered");
	}

	public BluezAuthenticationAgentImpl getAgent(){
		return this.authenticationAgent;
	}
	
	public void registerSDPRecord() {

		Path adaptorLocation = manager.DefaultAdapter();

		try {
			Service service = conn.getRemoteObject("org.bluez", adaptorLocation
					.getPath(), Service.class);
			service.AddRecord(xml);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		System.out.println("SDP Record registered");
	}
}
