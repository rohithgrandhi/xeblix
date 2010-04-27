package org.xeblix.server.configuration;

import org.xeblix.configuration.ButtonConfiguration;
import org.xeblix.configuration.RemoteConfigurationContainer;

/**
 * This class exists to validate the remote control configuration file. 
 * @author klewelling
 *
 */
public class RemoteConfiguration {

	private final RemoteConfigurationContainer configContainer;
	
	private RemoteConfiguration(){
		this.configContainer = new RemoteConfigurationContainer("Xeblix");
	}
	
	/**
	 * Adds a buttonConfiguration to the Remote's configuration. An IllegalArgumentException
	 * will be thrown if the buttonConfig parameter is null or of the configuration is locked
	 * or the UserInputTargetEnum is reserved.
	 * @param buttonConfig
	 */
	public final void addButtonConfiguration(ButtonConfiguration buttonConfig){
		configContainer.addButtonConfiguration(buttonConfig);
	}
	
}
