package org.xeblix.configuration;

import java.util.ArrayList;
import java.util.List;

public enum UserInputTargetEnum {

	REMOTE_NAME("Remote Name", true, false, ScreensEnum.ROOT),
	ROOT_SOURCE("Root Source", true, true, ScreensEnum.ROOT),
	ROOT_POWER("Root Power", true, false, ScreensEnum.ROOT),
	ROOT_OK("Root OK", true, false, ScreensEnum.ROOT),
	ROOT_MENU("Root Menu", false, false, ScreensEnum.ROOT),
	ROOT_DIRECTION("Direction", true, false, ScreensEnum.ROOT),
	ROOT_SPEED("Speed", true, false, ScreensEnum.ROOT),
	ROOT_VOLUME("Volume", true, false, ScreensEnum.ROOT),
	ROOT_CHANNEL("Channel", true, false, ScreensEnum.ROOT),
	ROOT_NUMPAD("Numpad", true, false, ScreensEnum.ROOT),
	ROOT_FREE("Root Free", true, false, ScreensEnum.ROOT),
	ROOT_ADD_HID_HOST("Add HID Host", true, false, ScreensEnum.ROOT),
	ROOT_REMOVE_HID_HOST("Remove HID Host", true, false, ScreensEnum.ROOT),
	NUMPAD_0("Numpad 0", true, false, ScreensEnum.NUMERIC),
	NUMPAD_1("Numpad 1", true, false, ScreensEnum.NUMERIC),
	NUMPAD_2("Numpad 2", true, false, ScreensEnum.NUMERIC),
	NUMPAD_3("Numpad 3", true, false, ScreensEnum.NUMERIC),
	NUMPAD_4("Numpad 4", true, false, ScreensEnum.NUMERIC),
	NUMPAD_5("Numpad 5", true, false, ScreensEnum.NUMERIC),
	NUMPAD_6("Numpad 6", true, false, ScreensEnum.NUMERIC),
	NUMPAD_7("Numpad 7", true, false, ScreensEnum.NUMERIC),
	NUMPAD_8("Numpad 8", true, false, ScreensEnum.NUMERIC),
	NUMPAD_9("Numpad 9", true, false, ScreensEnum.NUMERIC),
	NUMPAD_STAR("Numpad Star", true, false, ScreensEnum.NUMERIC),
	NUMPAD_POUND("Numpad Pound", true, false, ScreensEnum.NUMERIC),
	GESTURE_SCREEN_1("Gesture Screen 1", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_2("Gesture Screen 2", true, true, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_3("Gesture Screen 3", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_4("Gesture Screen 4", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_5("Gesture Screen 5", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_6("Gesture Screen 6", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_7("Gesture Screen 7", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_8("Gesture Screen 8", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_9("Gesture Screen 9", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_10("Gesture Screen 10", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_11("Gesture Screen 11", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_12("Gesture Screen 12", true, false, DummyStaticClass.GESTURE_SCREENS),
	GESTURE_SCREEN_13("Gesture Screen 13", true, false, DummyStaticClass.GESTURE_SCREENS);
	
	
	private static final List<UserInputTargetEnum> userInputTargets = 
		new ArrayList<UserInputTargetEnum>();
	
	static{
		userInputTargets.add(REMOTE_NAME);
		userInputTargets.add(ROOT_SOURCE);
		userInputTargets.add(ROOT_POWER);
		userInputTargets.add(ROOT_OK);
		userInputTargets.add(ROOT_MENU);
		userInputTargets.add(ROOT_DIRECTION);
		userInputTargets.add(ROOT_SPEED);
		userInputTargets.add(ROOT_VOLUME);
		userInputTargets.add(ROOT_CHANNEL);
		userInputTargets.add(ROOT_NUMPAD);
		userInputTargets.add(ROOT_FREE);
		userInputTargets.add(ROOT_ADD_HID_HOST);
		userInputTargets.add(ROOT_REMOVE_HID_HOST);
		userInputTargets.add(NUMPAD_0);
		userInputTargets.add(NUMPAD_1);
		userInputTargets.add(NUMPAD_2);
		userInputTargets.add(NUMPAD_3);
		userInputTargets.add(NUMPAD_4);
		userInputTargets.add(NUMPAD_5);
		userInputTargets.add(NUMPAD_6);
		userInputTargets.add(NUMPAD_7);
		userInputTargets.add(NUMPAD_8);
		userInputTargets.add(NUMPAD_9);
		userInputTargets.add(NUMPAD_STAR);
		userInputTargets.add(NUMPAD_POUND);
		userInputTargets.add(GESTURE_SCREEN_1);
		userInputTargets.add(GESTURE_SCREEN_2);
		userInputTargets.add(GESTURE_SCREEN_3);
		userInputTargets.add(GESTURE_SCREEN_4);
		userInputTargets.add(GESTURE_SCREEN_5);
		userInputTargets.add(GESTURE_SCREEN_6);
		userInputTargets.add(GESTURE_SCREEN_7);
		userInputTargets.add(GESTURE_SCREEN_8);
		userInputTargets.add(GESTURE_SCREEN_9);
		userInputTargets.add(GESTURE_SCREEN_10);
		userInputTargets.add(GESTURE_SCREEN_11);
		userInputTargets.add(GESTURE_SCREEN_12);
		userInputTargets.add(GESTURE_SCREEN_13);
	}
	
	private final String name;
	private final boolean singleAssignment;
	private final boolean reserved;
	private final ScreensEnum[] screens;
	
	/**
	 * SingleAssignment means if the UserInputTarget allows a single assignment
	 * or multiple assignments
	 * @param name
	 * @param singleAssignment
	 */
	UserInputTargetEnum(String name, boolean singleAssignment, boolean reserved, ScreensEnum screen){
		this(name, singleAssignment, reserved, new ScreensEnum[]{screen});
	}

	UserInputTargetEnum(String name, boolean singleAssignment, boolean reserved, ScreensEnum[] screens){
		this.name = name;
		this.singleAssignment = singleAssignment;
		this.reserved = reserved;
		this.screens = screens;
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
	
	public boolean isValidScreen(ScreensEnum screen){
		for(ScreensEnum screenEnum: screens){
			if(screenEnum == screen){
				return true;
			}
		}
		return false;
	}
	
	public static UserInputTargetEnum getUserInputTargetEnum(String name){
		
		for(UserInputTargetEnum userInputTargetEnum: userInputTargets){
			if(userInputTargetEnum.getName().equalsIgnoreCase(name) || 
				userInputTargetEnum.getName().replace(" ", "_").equalsIgnoreCase(name)){
				return userInputTargetEnum;
			}
		}
		return null;
	}
	
	private static class DummyStaticClass{
		public static ScreensEnum[] GESTURE_SCREENS = new ScreensEnum[]{ScreensEnum.CHANNEL,ScreensEnum.DIRECTION,ScreensEnum.OPTIONAL, ScreensEnum.SPEED, ScreensEnum.VOLUME};
	}
}
