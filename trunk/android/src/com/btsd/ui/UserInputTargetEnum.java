package com.btsd.ui;

public enum UserInputTargetEnum {

	REMOTE_NAME("Remote Name", true, false),
	ROOT_SOURCE("Root Source", true, true),
	ROOT_POWER("Root Power", true, false),
	ROOT_OK("Root OK", true, false),
	ROOT_MENU("Menu", false, false),
	ROOT_DIRECTION("Direction", true, false),
	ROOT_SPEED("Speed", true, false),
	ROOT_VOLUME("Volume", true, false),
	ROOT_CHANNEL("Channel", true, false),
	ROOT_NUMPAD("Numpad", true, false),
	ROOT_FREE("Free", true, false),
	ROOT_ADD_HID_HOST("Add HID Host", true, false),
	ROOT_REMOVE_HID_HOST("Remove HID Host", true, false),
	NUMPAD_0("Numpad 0", true, false),
	NUMPAD_1("Numpad 1", true, false),
	NUMPAD_2("Numpad 2", true, false),
	NUMPAD_3("Numpad 3", true, false),
	NUMPAD_4("Numpad 4", true, false),
	NUMPAD_5("Numpad 5", true, false),
	NUMPAD_6("Numpad 6", true, false),
	NUMPAD_7("Numpad 7", true, false),
	NUMPAD_8("Numpad 8", true, false),
	NUMPAD_9("Numpad 9", true, false),
	NUMPAD_STAR("Numpad Star", true, false),
	NUMPAD_POUND("Numpad Pound", true, false),
	GESTURE_SCREEN_1("Gesture Screen 1", true, false),
	GESTURE_SCREEN_2("Gesture Screen 2", true, true),
	GESTURE_SCREEN_3("Gesture Screen 3", true, false),
	GESTURE_SCREEN_4("Gesture Screen 4", true, false),
	GESTURE_SCREEN_5("Gesture Screen 5", true, false),
	GESTURE_SCREEN_6("Gesture Screen 6", true, false),
	GESTURE_SCREEN_7("Gesture Screen 7", true, false),
	GESTURE_SCREEN_8("Gesture Screen 8", true, false),
	GESTURE_SCREEN_9("Gesture Screen 9", true, false),
	GESTURE_SCREEN_10("Gesture Screen 10", true, false),
	GESTURE_SCREEN_11("Gesture Screen 11", true, false),
	GESTURE_SCREEN_12("Gesture Screen 12", true, false),
	GESTURE_SCREEN_13("Gesture Screen 13", true, false);
	
	private final String name;
	private final boolean singleAssignment;
	private final boolean reserved;
	
	/**
	 * SingleAssignment means if the UserInputTarget allows a single assignment
	 * or multiple assignments
	 * @param name
	 * @param singleAssignment
	 */
	UserInputTargetEnum(String name, boolean singleAssignment, boolean reserved){
		
		this.name = name;
		this.singleAssignment = singleAssignment;
		this.reserved = reserved;
	}

	public String getName() {
		return name;
	}

	public boolean isSingleAssignment() {
		return singleAssignment;
	}

	public boolean isReserved() {
		return reserved;
	}
	
}
