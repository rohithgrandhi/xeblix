package org.xeblix.server.configuration;

import java.util.ArrayList;
import java.util.List;

import org.xeblix.configuration.ButtonConfiguration;
import org.xeblix.configuration.ScreensEnum;
import org.xeblix.configuration.UserInputTargetEnum;

public final class TempConfigurationBuilder {

	public static final List<ButtonConfiguration> getVIP1200Configuration(){
		
		List<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,"vip1200", "DVR"));
		
		//ROOT Screen Configuration
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT, UserInputTargetEnum.ROOT_POWER, "POWER", "Power"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_OK, "OK", "OK"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "MENU", "Menu"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "GUIDE", "Guide"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "RECORDEDTV", "Recorded TV"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "gointeractive", "Go Interactive"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "VIDEOONDEMAND", "Video On Demand"));
		
		//Gesture Screen Direction
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_1, "BACK", "Back"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_3, "OK", "OK"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_4, "RECORD", "Record"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_6, "Up", "Up"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_7, "CHPG+", "Page Up"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_8, "DOWN", "Down"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_9, "CHPG-", "Page Down"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_10, "LEFT", "Left"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_11, "REW", "Back 24 Hours"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_12, "RIGHT", "Right"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_13, "FF", "Forward 24 Hours"));
		
		//Gesture Screen Speed
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED, UserInputTargetEnum.GESTURE_SCREEN_3, "PLAY", "Play"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_4, "REPLAY", "Replay"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_5, "FWD", "Forward"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_6, "PAUSE", "Pause"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_7, "PAUSE", "Pause"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_8, "STOP", "Stop"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_9, "STOP", "Stop"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_10, "REW", "Rewind"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_11, "REW", "Rewind"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_12, "FF", "Fast Forward"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_13, "FF", "Fast Forward"));
		
		//Gesture Screen Channel
		toReturn.add(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_3, "OK", "OK"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.CHANNEL, UserInputTargetEnum.GESTURE_SCREEN_6, "CHPG+", "Channel Up"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_7, "CHPG+", "Channel Up"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_8, "CHPG-", "Channel Down"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_9, "CHPG-", "Channel Down"));
		
		return toReturn;
	}
	
}
