package com.btsd.ui.configuration;

import static com.btsd.BluetoothHIDActivity.getKeycode;
import android.view.KeyEvent;

import com.btsd.BluetoothHIDActivity;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.LIRCRemoteConfiguration;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;
import com.btsd.ui.hidremote.HIDRemoteConfiguration;
import com.btsd.ui.managehidhosts.AddHIDHostConfiguration;

public final class TempConfigurationBuilder {

	public static final RemoteConfiguration getVIP1200Configuration(){
		
		RemoteConfiguration toReturn = new LIRCRemoteConfiguration(2);
		
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,"vip1200", "DVR"));
		
		//ROOT Screen Configuration
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT, UserInputTargetEnum.ROOT_POWER, "POWER", "Power"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_OK, "OK", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "MENU", "Menu"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "GUIDE", "Guide"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "RECORDEDTV", "Recorded TV"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "gointeractive", "Go Interactive"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "VIDEOONDEMAND", "Video On Demand"));
		
		//Gesture Screen Direction
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_1, "BACK", "Back"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_3, "OK", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_4, "RECORD", "Record"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_6, "Up", "Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_7, "CHPG+", "Page Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_8, "DOWN", "Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_9, "CHPG-", "Page Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_10, "LEFT", "Left"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_11, "REW", "Back 24 Hours"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_12, "RIGHT", "Right"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_13, "FF", "Forward 24 Hours"));
		
		//Gesture Screen Speed
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED, UserInputTargetEnum.GESTURE_SCREEN_3, "PLAY", "Play"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_4, "REPLAY", "Replay"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_5, "FWD", "Forward"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_6, "PAUSE", "Pause"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_7, "PAUSE", "Pause"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_8, "STOP", "Stop"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_9, "STOP", "Stop"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_10, "REW", "Rewind"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_11, "REW", "Rewind"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_12, "FF", "Fast Forward"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_13, "FF", "Fast Forward"));
		
		//Gesture Screen Channel
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_3, "OK", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL, UserInputTargetEnum.GESTURE_SCREEN_6, "CHPG+", "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_7, "CHPG+", "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_8, "CHPG-", "Channel Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_9, "CHPG-", "Channel Down"));
		
		
		
		toReturn.lockConfiguration();
		return toReturn;
	}
	
	public static final RemoteConfiguration getSonyBraviaConfiguration(){
		
		RemoteConfiguration toReturn = new LIRCRemoteConfiguration(2);
		
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,"SONY_12_RM-YD010", "TV"));
		
		//ROOT Screen Configuration
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT, UserInputTargetEnum.ROOT_POWER, "BTN_POWER", "Power"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_OK, "BTN_SELECT", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, "BTN_MENU", "Menu"));
		
		//Gesture Screen Direction
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_1, "BTN_MENU", "Menu"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_3, "BTN_SELECT", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_6, "BTN_UP", "Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_7, "BTN_UP", "Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_8, "BTN_DOWN", "Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_9, "BTN_DOWN", "Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_10, "BTN_LEFT", "Left"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_11, "BTN_LEFT", "Left"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_12, "BTN_RIGHT", "Right"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_13, "BTN_RIGHT", "Right"));
		
		//Gesture Screen Volume
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME, UserInputTargetEnum.GESTURE_SCREEN_3, "BTN_MUTING", "Mute"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME, UserInputTargetEnum.GESTURE_SCREEN_6, "BTN_VOLUME_UP", "Volume Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_7, "BTN_VOLUME_UP", "Volume Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_8, "BTN_VOLUME_DOWN", "Volume Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_9, "BTN_VOLUME_DOWN", "Volume Down"));
		
		//Gesture Screen Channel
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_3, "BTN_SELECT", "OK"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL, UserInputTargetEnum.GESTURE_SCREEN_6, "BTN_CHANNEL_UP", "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_7, "BTN_CHANNEL_UP", "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_8, "BTN_CHANNEL_dOWN", "Channel Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_9, "BTN_CHANNEL_dOWN", "Channel Down"));
		
		
		toReturn.lockConfiguration();
		return toReturn;
	}
	
	public static final RemoteConfiguration getMediaPCConfiguration(){
		
		return getHIDHostConfiguration("000272159B71", "MEDIA-PC");
	}
	
	public static final RemoteConfiguration getHIDHostConfiguration(String address, String name){
		
		RemoteConfiguration toReturn = new HIDRemoteConfiguration();
		
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,address, name));
		
		//ROOT Screen Configuration
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT, UserInputTargetEnum.ROOT_POWER, 
				new int[]{getKeycode(KeyEvent.KEYCODE_SEARCH),getKeycode(KeyEvent.KEYCODE_ALT_LEFT),getKeycode(KeyEvent.KEYCODE_ENTER)}, 
				"Start"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_OK, 
				new int[]{getKeycode(KeyEvent.KEYCODE_ENTER)}, "Enter"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, 
				new int[]{getKeycode(KeyEvent.KEYCODE_SEARCH),getKeycode(KeyEvent.KEYCODE_ALT_LEFT),getKeycode(KeyEvent.KEYCODE_ENTER)}, 
				"Home"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_T)}, 
				"My TV"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_M)}, 
				"My Music"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_E)}, 
				"My Videos"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.ROOT_MENU, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_I)}, 
				"My Pictures"));
		
		//Gesture Screen Direction
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_1, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DEL)}, "Back"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_3, 
				new int[]{getKeycode(KeyEvent.KEYCODE_ENTER)}, "Enter"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_6, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_UP)}, "Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_7, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_UP)}, "Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_8, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_DOWN)}, "Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_9, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_DOWN)}, "Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_10, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_LEFT)}, "Left"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_11, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_LEFT)}, "Left"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_12, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_RIGHT)}, "Right"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.DIRECTION,UserInputTargetEnum.GESTURE_SCREEN_13, 
				new int[]{getKeycode(KeyEvent.KEYCODE_DPAD_RIGHT)}, "Right"));
		
		//Gesture Screen Speed
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED, UserInputTargetEnum.GESTURE_SCREEN_3, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_P)}, "Play"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_6, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_P)}, "Pause"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_7, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_P)}, "Pause"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_8, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_S)}, "Stop"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_9, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_S)}, "Stop"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_10, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_B)}, "Rewind"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_11, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_B)}, "Skip Back"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_12, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_SHIFT_LEFT),getKeycode(KeyEvent.KEYCODE_F)}, "Fast Forward"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.SPEED,UserInputTargetEnum.GESTURE_SCREEN_13, 
				new int[]{getKeycode(BluetoothHIDActivity.CONTROL_LEFT),getKeycode(KeyEvent.KEYCODE_F)}, "Skip Forward"));
		
		//Gesture Screen Volume
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME, UserInputTargetEnum.GESTURE_SCREEN_3, 
				new int[]{getKeycode(BluetoothHIDActivity.F8)}, "Mute"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME, UserInputTargetEnum.GESTURE_SCREEN_6, 
				new int[]{getKeycode(BluetoothHIDActivity.F10)}, "Volume Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_7, 
				new int[]{getKeycode(BluetoothHIDActivity.F10)}, "Volume Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_8, 
				new int[]{getKeycode(BluetoothHIDActivity.F9)}, "Volume Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.VOLUME,UserInputTargetEnum.GESTURE_SCREEN_9, 
				new int[]{getKeycode(BluetoothHIDActivity.F9)}, "Volume Down"));
		
		//Gesture Screen Channel
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_3, 
				new int[]{getKeycode(KeyEvent.KEYCODE_ENTER)}, "Enter"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL, UserInputTargetEnum.GESTURE_SCREEN_6, 
				new int[]{getKeycode(KeyEvent.KEYCODE_MINUS)}, "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_7, 
				new int[]{getKeycode(KeyEvent.KEYCODE_MINUS)}, "Channel Up"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_8, 
				new int[]{getKeycode(KeyEvent.KEYCODE_EQUALS)}, "Channel Down"));
		toReturn.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.CHANNEL,UserInputTargetEnum.GESTURE_SCREEN_9, 
				new int[]{getKeycode(KeyEvent.KEYCODE_EQUALS)}, "Channel Down"));
		
		toReturn.lockConfiguration();
		return toReturn;
	}
	
	
	/*
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_1, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_3, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_4, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_5, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_6, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_7, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_8, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_9, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_10, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_11, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_12, "", "");
	 * toReturn.addButtonConfiguration(UserInputTargetEnum.GESTURE_SCREEN_13, "", "");
	 */
}
