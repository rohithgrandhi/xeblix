package com.btsd.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.btsd.CallbackActivity;

import android.app.Activity;

public abstract class RemoteConfiguration {

	private Map<String, ButtonConfiguration> singleAssignmentMap;
	private Map<String, List<ButtonConfiguration>> multiAssignmentMap;
	private boolean configurationLocked = false;
	private Set<ScreensEnum> configuredScreens;
	
	
	public RemoteConfiguration(){
		singleAssignmentMap = new HashMap<String, ButtonConfiguration>();
		multiAssignmentMap = new HashMap<String, List<ButtonConfiguration>>();
		configuredScreens = new HashSet<ScreensEnum>();
	}
	
	/**
	 * Adds a buttonConfiguration to the Remote's configuration. An IllegalArgumentException
	 * will be thrown if the buttonConfig parameter is null or of the configuration is locked
	 * or the UserInputTargetEnum is reserved.
	 * @param buttonConfig
	 */
	public final void addButtonConfiguration(ButtonConfiguration buttonConfig){
		
		if(configurationLocked){
			throw new IllegalArgumentException("Can not modify a Remote's configuration after " +
				"it has been locked.");
		}
		
		if(buttonConfig == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
			
		}
		
		UserInputTargetEnum userInputTarget = buttonConfig.getUserInputTargetEnum();
		if(userInputTarget.isReserved()){
			throw new IllegalArgumentException("Can not assign a button to the Reserved UserInputTarget: " + 
					userInputTarget.getName());
		}
		
		ScreensEnum screen = buttonConfig.getScreen();
		
		if(userInputTarget.isSingleAssignment()){
			//override any existing config
			singleAssignmentMap.put(screen.getName() + "|" + userInputTarget.getName(),buttonConfig);
		}else{
			List<ButtonConfiguration> buttonList = multiAssignmentMap.get(
					screen.getName() + "|" + userInputTarget.getName());
			if(buttonList == null){
				buttonList = new ArrayList<ButtonConfiguration>();
				multiAssignmentMap.put(screen.getName() + "|" + userInputTarget.getName(), buttonList);
			}
			buttonList.add(buttonConfig);
		}
		
		configuredScreens.add(buttonConfig.getScreen());
	}
	
	/**
	 * Locks the configuration so no more changes can be made.
	 */
	public final void lockConfiguration(){
		configurationLocked = true;
		//to guarantee config can't change, make maps unmodifiable.
		this.singleAssignmentMap = Collections.unmodifiableMap(this.singleAssignmentMap);
		this.multiAssignmentMap = Collections.unmodifiableMap(this.multiAssignmentMap);
		this.configuredScreens = Collections.unmodifiableSet(this.configuredScreens);
	}
	
	/**
	 * Returns the button assignment for the specified target or null if no 
	 * assignment can be found. An IllegalArgumentException will be thrown if the target
	 * parameter is null or is not a singleAssignment target or the configuration is not locked.
	 * @param target
	 * @return
	 */
	public final ButtonConfiguration getButtonConfiguration(ScreensEnum screen, UserInputTargetEnum target){
		
		if(!configurationLocked){
			throw new IllegalArgumentException("The configuration must be locked before " +
				"accessing configuration details.");
		}
		
		if(target == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		
		if(!target.isSingleAssignment()){
			throw new IllegalArgumentException("This method only accepts single assignment Targets");
		}
		
		return singleAssignmentMap.get(screen.getName() + "|" + target.getName());
	}
	
	/**
	 * Returns an unmodifiable list of button assignments for the specified target or an empty list if not
	 * assignments can be found. An IllegalArgumentException will be thrown if the target
	 * parameter is null or is not a multiAssignment target or the configuration is not locked.
	 * @param target
	 * @return
	 */
	public final List<ButtonConfiguration> getButtonConfigurations(ScreensEnum screen, UserInputTargetEnum target){
		
		if(!configurationLocked){
			throw new IllegalArgumentException("The configuration must be locked before " +
				"accessing configuration details.");
		}
		
		if(target == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		
		if(target.isSingleAssignment()){
			throw new IllegalArgumentException("This method only accepts multi assignment Targets");
		}
		
		List<ButtonConfiguration> toReturn = multiAssignmentMap.get(screen.getName() + "|" + target.getName());
		if(toReturn == null){
			return new ArrayList<ButtonConfiguration>();
		}else{
			return Collections.unmodifiableList(toReturn);
		}
	}

	/**
	 * Returns an unmodifiable list of configured screens or an empty list if this RemoteConfiguration
	 * has no configured screens. An IllegalArgumentException will be thrown if the configuration is not locked.
	 * @return
	 */
	public final Set<ScreensEnum> getConfiguredScreens() {
		
		if(!configurationLocked){
			throw new IllegalArgumentException("The configuration must be locked before " +
				"accessing configuration details.");
		}
		
		return configuredScreens;
	}
	
	/**
	 * Returns a command to send to the BTSD Server. An IllegalArgumentException will be thrown
	 * if the screen or target parameters are null.
	 * @param screen
	 * @param target
	 * @return
	 */
	public abstract JSONObject getCommand(ScreensEnum screen, UserInputTargetEnum target,
			Map<String,Object> remoteCache, CallbackActivity activity);
	
	/**
	 * Returns a command to send to the BTSD Server. An IllegalArgumentException will be thrown
	 * if the buttonConfiguration parameter is null.
	 */
	public abstract JSONObject getCommand(ButtonConfiguration buttonConfiguration,
			Map<String,Object> remoteCache, CallbackActivity activity);
	
	/**
	 * Callback method that is called when a message is received from the server the activity doesn't know
	 * how to handle.
	 * @param messageFromServer
	 * @param remoteCache
	 * @param activity
	 */
	public abstract JSONObject serverInteraction(JSONObject messageFromServer, Map<String,Object> remoteCache, 
			CallbackActivity activity);
	
	/**
	 * This method is called when the Remote Configuration is refreshed. This allows 
	 * RemoteConfigurations to select new configurations or to be notified they 
	 * have been removed. 
	 */
	public abstract void remoteConfigurationRefreshed(List<ButtonConfiguration> remoteConfigNames, 
			Map<String,Object> remoteCache, CallbackActivity activity);
	
	/**
	 * Callback method that is invoked when a user cancels an alert dialog this RemoteConfiguration
	 * displayed
	 * @param remoteCache
	 * @param activity
	 */
	public abstract JSONObject alertClicked(int button, Map<String,Object> remoteCache, CallbackActivity activity) ;
}
