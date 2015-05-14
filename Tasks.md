# Tasks #

**~~Create Add/Manage HID Hosts.~~**
~~Need to be able to add new HID Hosts and remove. Currently thinking will need a context menu from the Root activity with two buttons, Add HID Host and Edit HID Hosts~~ Complete

**~~Mobile Device Pairing~~**
~~Need to be able to Pair a mobile device, right now have to go to the bluez testing simple agent. This should be easy, if not in PairMode and get a Pin request then return 000 or 1234.~~ Complete

**~~Connect to Xeblix Server~~**
~~Right now my client is hard coded to connect to a Xeblix server, need a way to select Xeblix servers to connect to.~~ Complete

**~~Use bluez APIs to get HID Hosts~~**
~~Bluez can return lists of Paired bluetooth devices, use this instead of relying on a serialized list of HID Hosts.~~ Complete

**~~Move remote configuration to Server and JSON format~~**
~~Right now configuration is in code on the client, move to server and parse from file. Use JSON format in file.~~ Complete

**~~Add Keyboard/Mouse Activity~~**
~~Need to port over the old BluetoohHIDActivity to the new UI. Need to add ability to specify an intent in the RemoteConfiguration.~~ Complete