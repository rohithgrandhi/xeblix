package com.btsd.ui;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.xeblix.configuration.ButtonConfiguration;
import org.xeblix.configuration.RemoteConfigurationContainer;
import org.xeblix.configuration.ScreensEnum;
import org.xeblix.configuration.UserInputTargetEnum;

import com.btsd.CallbackActivity;

public abstract class RemoteConfiguration {

	protected RemoteConfigurationContainer configContainer;
	
	
	public RemoteConfiguration(){
	}
	
	protected void setRemoteConfigurationContainer(RemoteConfigurationContainer container){
		this.configContainer = container;
	}
	
	/**
	 * Returns the button assignment for the specified target or null if no 
	 * assignment can be found. An IllegalArgumentException will be thrown if the target
	 * parameter is null or is not a singleAssignment target or the configuration is not locked.
	 * @param target
	 * @return
	 */
	public final ButtonConfiguration getButtonConfiguration(ScreensEnum screen, UserInputTargetEnum target){
		return configContainer.getButtonConfiguration(screen, target);
	}
	
	/**
	 * Returns an unmodifiable list of button assignments for the specified target or an empty list if not
	 * assignments can be found. An IllegalArgumentException will be thrown if the target
	 * parameter is null or is not a multiAssignment target or the configuration is not locked.
	 * @param target
	 * @return
	 */
	public final List<ButtonConfiguration> getButtonConfigurations(ScreensEnum screen, UserInputTargetEnum target){
		
		return configContainer.getButtonConfigurations(screen, target);
	}

	/**
	 * Returns an unmodifiable list of configured screens or an empty list if this RemoteConfiguration
	 * has no configured screens. An IllegalArgumentException will be thrown if the configuration is not locked.
	 * @return
	 */
	public final Set<ScreensEnum> getConfiguredScreens() {
		return configContainer.getConfiguredScreens();
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
	public abstract JSONObject remoteConfigurationRefreshed(List<ButtonConfiguration> 
		remoteConfigNames, Map<String,Object> remoteCache, CallbackActivity activity);
	
	/**
	 * Callback method that is invoked when a user cancels an alert dialog this RemoteConfiguration
	 * displayed
	 * @param remoteCache
	 * @param activity
	 */
	public abstract JSONObject alertClicked(int button, Map<String,Object> remoteCache, 
			CallbackActivity activity) ;
	
	/**
	 * Validates the underlying RemoteConfiguration's state and returns a message to 
	 * send to the server or null if no message is neccessary.
	 * @return
	 */
	public abstract JSONObject validateState(Map<String,Object> remoteCache, 
			CallbackActivity activity); 
	
	/**
	 * Returns the label for this remote
	 */
	public String getLabel() {
		return this.configContainer.getLabel();
	}
}
