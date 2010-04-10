package com.btsd.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.btsd.AbstractRemoteActivity;
import com.btsd.BTSDApplication;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.ui.hidremote.HIDRemoteConfiguration;
import com.btsd.util.MessagesEnum;

public class RootActivity extends AbstractRemoteActivity implements DialogInterface.OnClickListener{

	private static final String TAG = "RootActivity";
	
	public static final String RemoteConfigurationBundleKey="RemoteConfiguration";
	public static final String ScreenBundleKey="SelectedScreen";
	
	private boolean[] textViewStates = new boolean[]{true, true, true, true, 
			true, true, true, true, true, true};
	private boolean buttonsRefreshed = false;
	private int[][] textViewsLocations = new int[10][4];
	private final TextView[] textViews = new TextView[10];
	private boolean dismissRetrievingConfigAlert = false;
	private Vibrator vibrator = null;
	private TextView selectedTextView = null;
	private RemoteConfiguration remoteConfiguration;
	private String remoteName;
	private AlertDialog menuDialog;
	private AlertDialog removeHIDHostDialog;
	private int selectedDialogItem;
	
	private int selectedRemoteIndex = 0;
	private List<ButtonConfiguration> configuredRemotes;
	
	private final static int ADD_HID_HOST_ID = Menu.FIRST;
	private final static int REMOVE_HID_HOST_ID = Menu.FIRST + 1;
	
	private void refreshTextViewLocations() {
		for(int i=0; i < textViews.length; i++){
			int[] xy = new int[2];
			textViews[i].getLocationOnScreen(xy);
			textViewsLocations[i][0] = xy[0];
			textViewsLocations[i][1] = xy[1];
			textViewsLocations[i][2] = xy[0] + textViews[i].getWidth();
			textViewsLocations[i][3] = xy[1] + textViews[i].getHeight();
			
			/*Log.i(TAG, "TextView: " + i + " x:" + textViewsLocations[i][0] + " y:" + 
					textViewsLocations[i][1] + " x+w:" + textViewsLocations[i][2] + 
					" y+h: " + textViewsLocations[i][3]);*/
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_layout);
		
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		int[] textViewIds = new int[]{R.id.RootTarget1, R.id.RootTarget2, R.id.RootTarget3, 
				R.id.RootTarget4, R.id.RootTarget5, R.id.RootTarget6, R.id.RootTarget7, R.id.RootTarget8, 
				R.id.RootTarget9, R.id.RootTarget10};
		
		TextView[] textViews = this.textViews;
		for(int i=0; i < textViewIds.length; i++){
			textViews[i] = (TextView)findViewById(textViewIds[i]);
		}
		
		AlertDialog alertDialog = new AlertDialog.Builder(this)
	        .setTitle(R.string.INFO)
	        .setCancelable(false)
	        //.setNegativeButton(android.R.string.cancel, this).
	        .setMessage(R.string.RETRIEVING_CONFIGURATION).create();
		this.alertDialog = alertDialog;
		alertDialog.show();
		dismissRetrievingConfigAlert = true;
		
		getBTSDApplication().getStateMachine().messageToServer(ServerMessages.getHidHosts());
		
		//setRootButtonText(UserInputTargetEnum.ROOT_FREE, vip1200,textViews[9]);
	}

	private void getRemoteConfiguration(boolean selectRemoteConfiguration) {
		
		if(dismissRetrievingConfigAlert){
			AlertDialog alertDialog = this.alertDialog;
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
				this.alertDialog = null;
				alertDialog = null; 
			}
		}
		
		this.configuredRemotes = getBTSDApplication().getRemoteConfigurationNames();
		
		if(dismissRetrievingConfigAlert || selectRemoteConfiguration){
			ButtonConfiguration selectedRemote = this.configuredRemotes.get(
				this.selectedRemoteIndex);
			RemoteConfiguration remoteConfiguration =  getBTSDApplication().getRemoteConfiguration(
				selectedRemote.getCommand().toString());
			this.remoteName = selectedRemote.getCommand().toString();
			this.remoteConfiguration = remoteConfiguration;
			
			initRootButtons(selectedRemote);
			
			dismissRetrievingConfigAlert = false;
		}
		
		JSONObject serverMessage = this.remoteConfiguration.remoteConfigurationRefreshed(this.configuredRemotes, 
				getBTSDApplication().getRemoteCache(), this);
		if(serverMessage != null){
			getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		}
	}

	private void initRootButtons(ButtonConfiguration selectedRemote) {
		
		TextView[] textViews = this.textViews;
		RemoteConfiguration remoteConfiguration = this.remoteConfiguration;
		Set<ScreensEnum> configuredScreens = remoteConfiguration.getConfiguredScreens();
		
		TextView targetView = (TextView)findViewById(R.id.RootTarget1);
		targetView.setText("Source\n" + selectedRemote.getLabel());
		
		setRootButtonText(UserInputTargetEnum.ROOT_POWER, remoteConfiguration,1, configuredScreens, ScreensEnum.ROOT);
		setRootButtonText(UserInputTargetEnum.ROOT_OK, remoteConfiguration,2, configuredScreens, ScreensEnum.ROOT);
		setRootButtonText(UserInputTargetEnum.ROOT_MENU, remoteConfiguration,3, configuredScreens, ScreensEnum.ROOT);
		setRootButtonText(UserInputTargetEnum.ROOT_DIRECTION, remoteConfiguration,4, configuredScreens, ScreensEnum.DIRECTION);
		setRootButtonText(UserInputTargetEnum.ROOT_SPEED, remoteConfiguration,5, configuredScreens, ScreensEnum.SPEED);
		setRootButtonText(UserInputTargetEnum.ROOT_VOLUME, remoteConfiguration,6, configuredScreens, ScreensEnum.VOLUME);
		setRootButtonText(UserInputTargetEnum.ROOT_CHANNEL, remoteConfiguration,7, configuredScreens, ScreensEnum.CHANNEL);
		setRootButtonText(UserInputTargetEnum.ROOT_NUMPAD, remoteConfiguration,8, configuredScreens, ScreensEnum.NUMERIC);
		setRootButtonText(UserInputTargetEnum.ROOT_FREE, remoteConfiguration,9, configuredScreens, ScreensEnum.OPTIONAL);
		
		//if no buttonConfiguration or screen configuration for ROOT_FREE set it to not show up
		ButtonConfiguration buttonConfig = remoteConfiguration.getButtonConfiguration(
			ScreensEnum.ROOT,UserInputTargetEnum.ROOT_FREE);
		if(!configuredScreens.contains(UserInputTargetEnum.ROOT_FREE) && buttonConfig == null){
			
			//no optional button defined so set its gravity to 0
			textViews[8].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0));
		}else{
			textViews[8].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
		}
		buttonsRefreshed = true;
	}
	
	private void setRootButtonText(UserInputTargetEnum targetEnum, RemoteConfiguration remoteConfig,
		int index, Set<ScreensEnum> screensConfig, ScreensEnum screen){
		TextView targetView = textViews[index];
		if(targetEnum.isSingleAssignment()){
			ButtonConfiguration buttonConfig = remoteConfig.getButtonConfiguration(
				ScreensEnum.ROOT, targetEnum);
			if(buttonConfig != null){
				targetView.setText(buttonConfig.getLabel());
			}else{
				targetView.setText(targetEnum.getName());
			}
			
			if(buttonConfig == null && !screensConfig.contains(screen)){
				//the current remote had no configuration for this root button
				//so it should be disabled
				targetView.setTextColor(Color.rgb(119,119,119));
				textViewStates[index] = false;
			}else{
				targetView.setTextColor(Color.rgb(255,255,255));
				textViewStates[index] = true;
			}
		}else{
			targetView.setText(targetEnum.getName());
			
			List<ButtonConfiguration> buttonConfigs = remoteConfig.getButtonConfigurations(
				ScreensEnum.ROOT,targetEnum);
			if(buttonConfigs.isEmpty()){
				//the current remote had no configuration for this root button
				//so it should be disabled
				targetView.setTextColor(Color.rgb(119,119,119));
				textViewStates[index] = false;
			}
		}
		
	}
	
	public void onClick(View v) {
		
		if(v == null){
			return;
		}
		
		if(v.getId() == R.id.RootTarget1){
			//go to the next remote, or if at end of list, go back to beginning
			if(selectedRemoteIndex >= this.configuredRemotes.size() - 1){
				selectedRemoteIndex = 0;
			}else{
				selectedRemoteIndex++;
			}
			
			ButtonConfiguration selectedRemote = this.configuredRemotes.get(
				selectedRemoteIndex);
			
			RemoteConfiguration remoteConfiguration =  getBTSDApplication().getRemoteConfiguration(
					selectedRemote.getCommand().toString());
			this.remoteName = selectedRemote.getCommand().toString();
			this.remoteConfiguration = remoteConfiguration;
			
			initRootButtons(selectedRemote);
			
		}else if(v.getId() == R.id.RootTarget2){
			JSONObject serverMessage = remoteConfiguration.getCommand(ScreensEnum.ROOT, 
					UserInputTargetEnum.ROOT_POWER,getBTSDApplication().getRemoteCache(), this);
			getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		}else if(v.getId() == R.id.RootTarget3){
			JSONObject serverMessage = remoteConfiguration.getCommand(ScreensEnum.ROOT, 
					UserInputTargetEnum.ROOT_OK,getBTSDApplication().getRemoteCache(), this);
			getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		}else if(v.getId() == R.id.RootTarget4){
			//menu
			List<ButtonConfiguration> menus =  remoteConfiguration.getButtonConfigurations(
				ScreensEnum.ROOT, UserInputTargetEnum.ROOT_MENU);
			if(menus.size() == 1){
				ButtonConfiguration buttonConfig = menus.get(0);
				JSONObject serverMessage = remoteConfiguration.getCommand(buttonConfig,
						getBTSDApplication().getRemoteCache(), this);
				getBTSDApplication().getStateMachine().messageToServer(serverMessage);
			}else{
				
				ArrayList<CharSequence> menusToDisplay = new ArrayList<CharSequence>();
				for(ButtonConfiguration menuConfig: menus){
					menusToDisplay.add(menuConfig.getLabel());
				}
				
				CharSequence[] menusToDisplayArray = menusToDisplay.toArray(
						new CharSequence[menusToDisplay.size()]);
				
				//default is the first item in the menu list
				selectedDialogItem = 0;;
				
				menuDialog = new AlertDialog.Builder(this)
			        .setTitle(R.string.MENU_DIALOG_TITLE)
			        .setPositiveButton(android.R.string.ok, this)
			        .setNegativeButton(android.R.string.cancel, null).
			        setSingleChoiceItems(menusToDisplayArray,
			                0, this).create();
				menuDialog.show();
			}
		}else if(v.getId() == R.id.RootTarget5){
			//direction screen
			Intent intent = new Intent("com.btsd.GestureScreenActivity");
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			intent.putExtra(ScreenBundleKey, ScreensEnum.DIRECTION.getName());
			startActivity(intent);
		}else if(v.getId() == R.id.RootTarget6){
			Intent intent = new Intent("com.btsd.GestureScreenActivity");
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			intent.putExtra(ScreenBundleKey, ScreensEnum.SPEED.getName());
			startActivity(intent);
		}else if(v.getId() == R.id.RootTarget7){
			Intent intent = new Intent("com.btsd.GestureScreenActivity");
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			intent.putExtra(ScreenBundleKey, ScreensEnum.VOLUME.getName());
			startActivity(intent);
		}else if(v.getId() == R.id.RootTarget8){
			Intent intent = new Intent("com.btsd.GestureScreenActivity");
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			intent.putExtra(ScreenBundleKey, ScreensEnum.CHANNEL.getName());
			startActivity(intent);
		}else if(v.getId() == R.id.RootTarget9){
			Intent intent = new Intent("com.btsd.GestureScreenActivity");
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			intent.putExtra(ScreenBundleKey, ScreensEnum.NUMERIC.getName());
			startActivity(intent);
		}else if(v.getId() == R.id.RootTarget10){
			ButtonConfiguration buttonConfig = remoteConfiguration.getButtonConfiguration(
				ScreensEnum.ROOT, UserInputTargetEnum.ROOT_FREE);
			Intent intent = new Intent(buttonConfig.getCommand().toString());
			intent.putExtra(RemoteConfigurationBundleKey, this.remoteName);
			startActivity(intent);
		}
		
		/*if(v.getId() == R.id.directionButton){
			Log.i(TAG, "Direction Prototype clicked");
			Intent intent = new Intent("com.btsd.DirectionPrototype");
			startActivity(intent);
		}*/
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e(TAG, "onTouchEvent:" + event.getAction());
		
		//check if the initRootButtons method has been called. If so then we
		//need to refresh the textViewLocations
		if(buttonsRefreshed){
			buttonsRefreshed = false;
			refreshTextViewLocations();
		}
		
		final float x = event.getX();
		final float y = event.getY();
		
		//Log.i(TAG, "me x:" + x + " y: " + y);
		final TextView selectedTextView = this.selectedTextView;
		TextView textView = null;
		boolean enabled = false; 
		
		for(int i=0; i < textViewsLocations.length; i++){
			if( (x >= textViewsLocations[i][0] && x < textViewsLocations[i][2]) && 
				y >= textViewsLocations[i][1] && y < textViewsLocations[i][3]){
				
				textView = textViews[i];
				enabled = textViewStates[i];
				break;
			}
		}
		
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			if(enabled){
				textView.setPressed(true);
				vibrator.vibrate(25);
			}
			this.selectedTextView = textView;
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(selectedTextView != textView){
				selectedTextView.setPressed(false);
				if(enabled){
					textView.setPressed(true);
					vibrator.vibrate(25);
				}
				this.selectedTextView = textView;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			
			//user let up from the touch screen
			this.selectedTextView.setPressed(false);
			this.selectedTextView  =null;
			if(enabled){
				onClick(textView);
			}
			break;
		default:
			break;
		}
		
		return true;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if(dialog == menuDialog){
			if(which == DialogInterface.BUTTON1){
				
				List<ButtonConfiguration> menus =  remoteConfiguration.getButtonConfigurations(ScreensEnum.ROOT, 
						UserInputTargetEnum.ROOT_MENU);
				ButtonConfiguration buttonConfig = menus.get(selectedDialogItem);
				JSONObject serverMessage = remoteConfiguration.getCommand(buttonConfig, 
						getBTSDApplication().getRemoteCache(), this);
				getBTSDApplication().getStateMachine().messageToServer(serverMessage);
			}else{
				//user just selected an option
				selectedDialogItem = which;
			}
		}else if(dialog == removeHIDHostDialog){
			
			if(which == DialogInterface.BUTTON1){
				
				Log.i(getClass().getSimpleName(), "Removing HID Host at index: " + 
						selectedDialogItem);
				ArrayList<ButtonConfiguration> hidHostNames = getBTSDApplication().
					getHIDHostConfigurationNames();
				ButtonConfiguration selectedRemote = hidHostNames.get(selectedDialogItem);
				HIDRemoteConfiguration hidRemoteConfig =  (HIDRemoteConfiguration)getBTSDApplication().
				 	getRemoteConfiguration(selectedRemote.getCommand().toString());
				
				JSONObject serverJSONMessage = new JSONObject();
				try{
					serverJSONMessage.put(Main.TYPE, Main.TYPE_UNPAIR_HID_HOST);
					serverJSONMessage.put(Main.HOST_ADDRESS, hidRemoteConfig.getHostAddress());
					serverJSONMessage.put(Main.HOST_NAME, hidRemoteConfig.getName());
				}catch(JSONException ex){
					throw new RuntimeException(ex.getMessage(), ex);
				}
				
				JSONObject serverMessage =  remoteConfiguration.
					serverInteraction(serverJSONMessage, 
					getBTSDApplication().getRemoteCache(), this);
				if(serverMessage != null){
					getBTSDApplication().getStateMachine().messageToServer(serverMessage);
				}
			}else{
				//user just selected an option
				selectedDialogItem = which;
			}
			
		}else if(dialog == alertDialog){
			JSONObject serverMessage = remoteConfiguration.alertClicked(which, getBTSDApplication().getRemoteCache(), 
				this);
			if(serverMessage != null){
				getBTSDApplication().getStateMachine().messageToServer(serverMessage);
			}
		}
		
	}
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		
		if(messagesEnum == MessagesEnum.MESSAGE_FROM_SERVER){
			
			//TODO: hack! intercept HIDHost messages
			JSONObject serverJSONMessage = (JSONObject)message;
			
			try{
				String type = (String)serverJSONMessage.get(Main.TYPE);
				if(Main.TYPE_HID_HOSTS.equalsIgnoreCase(type)){
					getBTSDApplication().updateRemoteConfiguration(serverJSONMessage);
					getRemoteConfiguration(false);
				}else{
					//if remoteConfiguration is null then have not gotten remote configuration
					//yet and the message should be ignored. 
					if(remoteConfiguration != null){
						JSONObject serverMessage =  remoteConfiguration.
							serverInteraction(serverJSONMessage, 
							getBTSDApplication().getRemoteCache(), this);
						if(serverMessage != null){
							getBTSDApplication().getStateMachine().messageToServer(serverMessage);
						}
					}
					
				}
			}catch(JSONException ex){
				throw new RuntimeException(ex);
			}
		}else{
			throw new IllegalArgumentException("Unexpected Message: " + messagesEnum.getId());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ADD_HID_HOST_ID, 0, R.string.ADD_HID_HOST);
		menu.add(Menu.NONE, REMOVE_HID_HOST_ID, 0, R.string.REMOVE_HID_HOST);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case ADD_HID_HOST_ID:
			enterPairMode();
			break;
		case REMOVE_HID_HOST_ID:
			removeHIDHostDialog();
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void removeHIDHostDialog(){
		
		ArrayList<CharSequence> hidHostsToDisplay = new ArrayList<CharSequence>();
		for(ButtonConfiguration menuConfig: getBTSDApplication().getHIDHostConfigurationNames()){
			hidHostsToDisplay.add(menuConfig.getLabel());
		}
		
		if(!hidHostsToDisplay.isEmpty()){
		
			CharSequence[] hidHostsToDisplayArray = hidHostsToDisplay.toArray(
					new CharSequence[hidHostsToDisplay.size()]);
			
			//default is the first item in the menu list
			selectedDialogItem = 0;
			
			removeHIDHostDialog = new AlertDialog.Builder(this)
		        .setTitle(R.string.REMOVE_HID_HOST_TITLE)
		        .setPositiveButton(android.R.string.ok, this)
		        .setNegativeButton(android.R.string.cancel, null).
		        setSingleChoiceItems(hidHostsToDisplayArray,
		                0, this).create();
			removeHIDHostDialog.show();
		}else{
			//don't have any hid hosts to remove
			showCancelableDialog(R.string.FAILED_REMOVE_HID_HOST_TITLE, 
				R.string.FAILED_REMOVE_HID_HOST_BODY);
		}
		
	}
	
	private void enterPairMode(){
		
		this.remoteConfiguration = getBTSDApplication().getRemoteConfiguration(
			BTSDApplication.ADD_HID_HOST_CONFIGURATION_NAME);
		
		JSONObject serverMessage = remoteConfiguration.getCommand(ScreensEnum.ROOT, 
				UserInputTargetEnum.ROOT_ADD_HID_HOST, getBTSDApplication().getRemoteCache(), 
				this);
		getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		
		//put the server into pair mode
		/*getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getPairMode());*/
		
	}
	
	@Override
	public void refreshConfiguredRemotes() {
		
		//TODO: hack for now, just get the hid hosts
		getBTSDApplication().getStateMachine().messageToServer(
			ServerMessages.getHidHosts());
	}
	
	@Override
	public void returnToPreviousRemoteConfiguration() {
		
		//TODO: right now this just goes back to the first remoteConfiguration
		//need to update to go to the previous config
		selectedRemoteIndex = 0;
		getRemoteConfiguration(true);
	}
	
	@Override
	public void selectConfiguredRemote(String name) {
		
		selectedRemoteIndex = 0;
		for(int i=0; i < this.configuredRemotes.size(); i++){
			ButtonConfiguration selectedRemote = this.configuredRemotes.get(i);
			if(selectedRemote.getLabel().equals(name)){
				selectedRemoteIndex = i;
				break;
			}
		}
		getRemoteConfiguration(true);
	}
}
