package com.btsd.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.btsd.AbstractRemoteActivity;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.util.MessagesEnum;
import com.btsd.view.ModifierButtons;

public class KeyboardActivity extends AbstractRemoteActivity {

	private InputMethodManager inputMethodManager;
	
	public static HashMap<Integer, Integer> keyMapping;
	private final static int MENU_WINDOWS_KEY = Menu.FIRST;
	private final static int MENU_ESC_KEY = MENU_WINDOWS_KEY + 1;
	private final static int MENU_TAB_KEY = MENU_ESC_KEY + 1;
	private final static int MENU_BACKSPACE_KEY = MENU_TAB_KEY + 1;
	private final static int MENU_WINDOWS_KEY_LOCK = MENU_BACKSPACE_KEY + 1;
	private final static int MENU_ALT_KEY_LOCK = MENU_WINDOWS_KEY_LOCK + 1;
	private final static int MENU_CTRL_KEY_LOCK = MENU_ALT_KEY_LOCK + 1;	
	
	public static final int CONTROL_LEFT = -1;
	public static final int F8 = -4;
	public static final int F9 = -2;
	public static final int F10 = -3;
	public static final int WINDOWS_KEY_LEFT = -4;
	public static final int ESCAPE = -5;
	public static final int BACKSPACE = -6;
	
	static{
		keyMapping = new HashMap<Integer, Integer>();
		keyMapping.put(KeyEvent.KEYCODE_A, 4);
		keyMapping.put(KeyEvent.KEYCODE_B, 5);
		keyMapping.put(KeyEvent.KEYCODE_C, 6);
		keyMapping.put(KeyEvent.KEYCODE_D, 7);
		keyMapping.put(KeyEvent.KEYCODE_E, 8);
		keyMapping.put(KeyEvent.KEYCODE_F, 9);
		keyMapping.put(KeyEvent.KEYCODE_G, 10);
		keyMapping.put(KeyEvent.KEYCODE_H, 11);
		keyMapping.put(KeyEvent.KEYCODE_I, 12);
		keyMapping.put(KeyEvent.KEYCODE_J, 13);
		keyMapping.put(KeyEvent.KEYCODE_K, 14);
		keyMapping.put(KeyEvent.KEYCODE_L, 15);
		keyMapping.put(KeyEvent.KEYCODE_M, 16);
		keyMapping.put(KeyEvent.KEYCODE_N, 17);
		keyMapping.put(KeyEvent.KEYCODE_O, 18);
		keyMapping.put(KeyEvent.KEYCODE_P, 19);
		keyMapping.put(KeyEvent.KEYCODE_Q, 20);
		keyMapping.put(KeyEvent.KEYCODE_R, 21);
		keyMapping.put(KeyEvent.KEYCODE_S, 22);
		keyMapping.put(KeyEvent.KEYCODE_T, 23);
		keyMapping.put(KeyEvent.KEYCODE_U, 24);
		keyMapping.put(KeyEvent.KEYCODE_V, 25);
		keyMapping.put(KeyEvent.KEYCODE_W, 26);
		keyMapping.put(KeyEvent.KEYCODE_X, 27);
		keyMapping.put(KeyEvent.KEYCODE_Y, 28);
		keyMapping.put(KeyEvent.KEYCODE_Z, 29);
		
		keyMapping.put(KeyEvent.KEYCODE_1, 30);
		keyMapping.put(KeyEvent.KEYCODE_2, 31);
		keyMapping.put(KeyEvent.KEYCODE_3, 32);
		keyMapping.put(KeyEvent.KEYCODE_4, 33);
		keyMapping.put(KeyEvent.KEYCODE_5, 34);
		keyMapping.put(KeyEvent.KEYCODE_6, 35);
		keyMapping.put(KeyEvent.KEYCODE_7, 36);
		keyMapping.put(KeyEvent.KEYCODE_8, 37);
		keyMapping.put(KeyEvent.KEYCODE_9, 38);
		keyMapping.put(KeyEvent.KEYCODE_0, 39);
		
		keyMapping.put(KeyEvent.KEYCODE_ENTER, 40);
		keyMapping.put(ESCAPE, 41);
		keyMapping.put(KeyEvent.KEYCODE_TAB, 43);
		keyMapping.put(KeyEvent.KEYCODE_SPACE, 44);
		keyMapping.put(KeyEvent.KEYCODE_MINUS, 45);
		keyMapping.put(KeyEvent.KEYCODE_EQUALS, 46);
		keyMapping.put(KeyEvent.KEYCODE_LEFT_BRACKET, 47);
		keyMapping.put(KeyEvent.KEYCODE_RIGHT_BRACKET, 48);
		keyMapping.put(KeyEvent.KEYCODE_BACKSLASH, 49);
		keyMapping.put(KeyEvent.KEYCODE_SEMICOLON, 51);
		keyMapping.put(KeyEvent.KEYCODE_APOSTROPHE, 52);
		keyMapping.put(KeyEvent.KEYCODE_GRAVE, 53);
		keyMapping.put(KeyEvent.KEYCODE_COMMA, 54);
		keyMapping.put(KeyEvent.KEYCODE_PERIOD, 55);
		keyMapping.put(KeyEvent.KEYCODE_SLASH, 56);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_RIGHT, 79);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_LEFT, 80);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_DOWN, 81);
		keyMapping.put(KeyEvent.KEYCODE_DPAD_UP, 82);
		keyMapping.put(KeyEvent.KEYCODE_DEL, 76);
		keyMapping.put(BACKSPACE, 42);
		keyMapping.put(CONTROL_LEFT, 224); 
		keyMapping.put(KeyEvent.KEYCODE_SHIFT_LEFT, 225);
		keyMapping.put(KeyEvent.KEYCODE_ALT_LEFT, 226);
		keyMapping.put(KeyEvent.KEYCODE_SEARCH, 227);
		keyMapping.put(F8, 65);
		keyMapping.put(F9, 66);
		keyMapping.put(F10, 67);
		keyMapping.put(WINDOWS_KEY_LEFT, 227);
		
		
	}
	
	public static Integer getKeycode(int keycode){
		return keyMapping.get(keycode);
	}
	
	private RemoteConfiguration remoteConfiguration;
	private ScreensEnum selectedScreen;
	private ImageView keyboardButton;
	private ModifierButtons modifierButtons;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.keyboard_mouse);
		
		Bundle extras = getIntent().getExtras();
		String remoteConfig = extras.getString(RootActivity.RemoteConfigurationBundleKey);
		String selectedScreenString = extras.getString(RootActivity.ScreenBundleKey);
		this.selectedScreen = ScreensEnum.getScreenEnum(selectedScreenString);
		RemoteConfiguration remoteConfiguration = getBTSDApplication().
			getRemoteConfiguration(remoteConfig);
		this.remoteConfiguration = remoteConfiguration;
		
		//have the remoteConfiguration validate its state
		JSONObject messageToServer = this.remoteConfiguration.validateState(
				getBTSDApplication().getRemoteCache(), this);
		if(messageToServer != null){
			getBTSDApplication().getStateMachine().messageToServer(messageToServer);
		}
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		keyboardButton = (ImageView)findViewById(R.id.keyboard_button);
		keyboardButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				inputMethodManager.toggleSoftInputFromWindow(keyboardButton.getWindowToken(), 
						InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		});
		
		modifierButtons = (ModifierButtons)findViewById(R.id.modifier_buttons);
		
		Configuration config = getResources().getConfiguration();
		if(config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO){
			keyboardButton.setVisibility(View.GONE); 
		}
		
		/*
		inputMethodManager.showSoftInput(findViewById(R.id.KeyboardView), InputMethodManager.SHOW_FORCED);*/
		
		/*
		 *  private void configureOrientation() {
                String rotateDefault;
                if (getResources().getConfiguration().keyboard == Configuration.KEYBOARD_NOKEYS)
                        rotateDefault = PreferenceConstants.ROTATION_PORTRAIT;
                else
                        rotateDefault = PreferenceConstants.ROTATION_LANDSCAPE;

                String rotate = prefs.getString(PreferenceConstants.ROTATION, rotateDefault);
                if (PreferenceConstants.ROTATION_DEFAULT.equals(rotate))
                        rotate = rotateDefault;

                // request a forced orientation if requested by user
                if (PreferenceConstants.ROTATION_LANDSCAPE.equals(rotate)) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        forcedOrientation = true;
                } else if (PreferenceConstants.ROTATION_PORTRAIT.equals(rotate)) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        forcedOrientation = true;
                } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        forcedOrientation = false;
                }
        }

		 */
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Integer hidKeyCode = null;
		
		//Integer.MIN_VALUE indicates a modifier was pressed not a normal key
		if(keyCode != Integer.MIN_VALUE){
			hidKeyCode = keyMapping.get(keyCode);
		}
		
		if(hidKeyCode == null && keyCode != Integer.MIN_VALUE){
			//not a key we care about
			return super.onKeyDown(keyCode, event);
		}
		//Log.e(TAG, "keyCode: " + keyCode + " hidKeyCode: " + keyCode);
		
		boolean keyEventAltPressed = false;
		boolean keyEventShiftPressed = false;
		if(event != null){
			keyEventAltPressed = event.isAltPressed();
			keyEventShiftPressed = event.isShiftPressed();
		}
		
		if(hidKeyCode == null || keyEventAltPressed || keyEventShiftPressed || 
			modifierButtons.isAltKeyPressed() || modifierButtons.isCtrlKeyPressed() || 
			modifierButtons.isWindowsKeyPressed()){
			
			ArrayList<Integer> hidKeyCodes = new ArrayList<Integer>();
			if(hidKeyCode != null){
				hidKeyCodes.add(hidKeyCode);
			}
			
			ArrayList<Integer> modifiersDown = new ArrayList<Integer>();
			
			//0==left ctrl
			//1==left shift
			//2==left alt
			//3==windows key
			//4==right ctrl
			//5==right shift
			//6==right alt
			
			
			if(keyEventAltPressed || modifierButtons.isAltKeyPressed()){
				modifiersDown.add(2);
			}
			
			if(keyEventShiftPressed){
				modifiersDown.add(1);
			}
			
			if(modifierButtons.isCtrlKeyPressed()){
				modifiersDown.add(0);
			}
			
			if(modifierButtons.isWindowsKeyPressed()){
				modifiersDown.add(3);
			}
			
			//if hidKeyCode is null then means a modifier was pressed not a key so don't
			//release the state
			if(hidKeyCode != null){
				modifierButtons.keyPressed();
			}
			
			ArrayList<Integer> modifiersUp = new ArrayList<Integer>();
			
			if(modifierButtons.isAltKeyPressed()){
				modifiersUp.add(2);
			}
			
			if(modifierButtons.isCtrlKeyPressed()){
				modifiersUp.add(0);
			}
			
			if(modifierButtons.isWindowsKeyPressed()){
				modifiersUp.add(3);
			}
			
			getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getKeycodes(hidKeyCodes, modifiersDown,modifiersUp));
			
		}else if(hidKeyCode != null){
			getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getKeyCode(hidKeyCode));
		}
		return true;
	}
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		if(messagesEnum == MessagesEnum.MESSAGE_FROM_SERVER){
			JSONObject serverMessage =  remoteConfiguration.
				serverInteraction((JSONObject)message, 
				getBTSDApplication().getRemoteCache(), this);
			if(serverMessage != null){
				getBTSDApplication().getStateMachine().messageToServer(serverMessage);
			}
		}else{
			throw new IllegalArgumentException("Unexpected Message: " + messagesEnum.getId());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(Menu.NONE, MENU_WINDOWS_KEY, 0, R.string.WINDOWS_KEY_LABEL);
		menu.add(Menu.NONE, MENU_ESC_KEY, 0, R.string.ESCAPE_KEY_LABEL);
		menu.add(Menu.NONE, MENU_TAB_KEY, 0, R.string.TAB_KEY_LABEL);
		menu.add(Menu.NONE, MENU_BACKSPACE_KEY, 0, R.string.BACKSPACE_KEY_LABEL);
		menu.add(Menu.NONE, MENU_WINDOWS_KEY_LOCK, 0, R.string.WINDOWS_KEY_LOCK_LABEL);
		menu.add(Menu.NONE, MENU_ALT_KEY_LOCK, 0, R.string.ALT_KEY_LOCK_LABEL);
		menu.add(Menu.NONE, MENU_CTRL_KEY_LOCK, 0, R.string.CTRL_KEY_LOCK_LABEL);
		
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_WINDOWS_KEY:
			getBTSDApplication().getStateMachine().messageToServer(
					ServerMessages.getKeyCode(getKeycode(WINDOWS_KEY_LEFT)));
			break;
		case MENU_WINDOWS_KEY_LOCK:
			modifierButtons.toggleWindowsKey();
			onKeyDown(Integer.MIN_VALUE, null);
			break;
		case MENU_ALT_KEY_LOCK:
			modifierButtons.toggleAltKey();
			onKeyDown(Integer.MIN_VALUE, null);
			break;
		case MENU_CTRL_KEY_LOCK:
			modifierButtons.toggleCtrlKey();
			onKeyDown(Integer.MIN_VALUE, null);
			break;
		case MENU_ESC_KEY:
			onKeyDown(ESCAPE, null);
			break;
		case MENU_TAB_KEY:
			onKeyDown(KeyEvent.KEYCODE_TAB, null);
			break;
		case MENU_BACKSPACE_KEY:
			onKeyDown(BACKSPACE, null);
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onClick(View v) {
		throw new IllegalArgumentException("Implement me");
	}

	@Override
	public void refreshConfiguredRemotes() {
		throw new IllegalArgumentException("Implement me");
	}

	@Override
	public void returnToPreviousRemoteConfiguration() {
		throw new IllegalArgumentException("Implement me");
	}

	@Override
	public void selectConfiguredRemote(String name) {
		throw new IllegalArgumentException("Implement me");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog == alertDialog){
			remoteConfiguration.alertClicked(which, getBTSDApplication().getRemoteCache(), 
				this);
		}
	}

}
