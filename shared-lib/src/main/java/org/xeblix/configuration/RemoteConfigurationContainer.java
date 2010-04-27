package org.xeblix.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RemoteConfigurationContainer {

	private static final String BUTTONS = "buttons";
	private static final String SCREEN = "screen";
	private static final String TARGET = "target";
	private static final String COMMAND = "command";
	private static final String LABEL = "label";
	
	private Map<String, ButtonConfiguration> singleAssignmentMap;
	private Map<String, List<ButtonConfiguration>> multiAssignmentMap;
	private boolean configurationLocked = false;
	private String label;
	private Set<ScreensEnum> configuredScreens;
	
	public RemoteConfigurationContainer(String label){
		singleAssignmentMap = new HashMap<String, ButtonConfiguration>();
		multiAssignmentMap = new HashMap<String, List<ButtonConfiguration>>();
		configuredScreens = new HashSet<ScreensEnum>();
		this.label = label;
	}
	
	/**
	 * Parses buttonConfigurations from a JSONObject and returns the associated RemoteConfiguratonContainer.
	 * @param jsonObject
	 * @param remoteLabel
	 * @return
	 */
	public static RemoteConfigurationContainer parseButtonConfiguration(JSONObject jsonObject, String remoteLabel){
		
		if(jsonObject == null || remoteLabel == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		
		JSONArray buttons = null;
		
		try{
			buttons = jsonObject.getJSONArray(BUTTONS);
		}catch(JSONException ex){
			throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
				"The RemoteConfiguration does not contain a required JSONArray of buttons.");
		}
		
		RemoteConfigurationContainer container = new RemoteConfigurationContainer(remoteLabel);
		
		for(int i=0; i < buttons.length(); i++){
			JSONObject jsonButton = null;
			try{
				jsonButton = buttons.getJSONObject(i);
			}catch(JSONException ex){
				throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
					"The buttons jsonArray must contain only JSONObjects.");
			}
			
			String screen = null;
			String target = null;
			Object command = null;
			String label = null;
			
			try{
				
				screen = jsonButton.getString(SCREEN);
				target = jsonButton.getString(TARGET);
				jsonButton.get(COMMAND);
				label = jsonButton.getString(LABEL);
				
			}catch(JSONException ex){
				throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
					"A button JSONObject must contain the following properties: " + SCREEN + ", " + 
					TARGET + ", " + COMMAND + ", and " + LABEL + ". One or more of these properties is " +
					"missing from the button configuration: " + jsonButton.toString());
			}
			
			try{
				JSONArray commands = jsonButton.getJSONArray(COMMAND);
				command = new String[commands.length()];
				String[] tempCommand = (String[])command;
				boolean ints = true;
				for(int j=0; j < commands.length(); j++){
					try{
						//the commands array should contain Strings/ints not another array
						commands.getJSONArray(j);
						throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
							"The button configuration: " + jsonButton.toString() + " must be either a String/int or " +
							"an array of String/ints.");
					}catch(JSONException ex){
						
					}
					
					try{
						//the commands array should contain Strings/ints not another array
						commands.getJSONObject(j);
						throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
							"The button configuration: " + jsonButton.toString() + " must be either a String/int or " +
							"an array of String/ints.");
					}catch(JSONException ex){
						
					}
					
					tempCommand[j] = commands.getString(j);
					try{
						commands.getInt(j);
					}catch(JSONException e){
						ints = false;
					}
				}
				
				if(ints){
					command = new int[commands.length()];
					int[] tempIntCommand = (int[])command;
					for(int j=0; j < commands.length(); j++){
						tempIntCommand[j] = commands.getInt(j);
					}
				}
			}catch(JSONException e){
				try{
					command = jsonButton.getString(COMMAND);
				}catch(JSONException ex){
					throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
						"The button configuration: " + jsonButton.toString() + " must be either a String/int or " +
						"an array of String/ints.");
				}
			}
			
			
			ScreensEnum screenEnum = ScreensEnum.getScreenEnum(screen);
			if(screenEnum == null){
				throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
					"The button configuration: " + jsonButton.toString() + " has an unknown Screen value.");
			}
			
			UserInputTargetEnum targetEnum = UserInputTargetEnum.getUserInputTargetEnum(target);
			if(targetEnum == null){
				throw new RuntimeException("The remote: " + remoteLabel + " has an invalid configuration. " +
						"The button configuration: " + jsonButton.toString() + " has an unknown Target value.");
			}
			
			container.addButtonConfiguration(new ButtonConfiguration(screenEnum, targetEnum, command, label));
		}
		
		return container;
	}
	
	/**
	 * Adds a buttonConfiguration to the Remote's configuration. An IllegalArgumentException
	 * will be thrown if the buttonConfig parameter is null or of the configuration is locked
	 * or the UserInputTargetEnum is reserved.
	 * @param buttonConfig
	 */
	public void addButtonConfiguration(ButtonConfiguration buttonConfig){
		
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
		
		//verify the screen is valid of the userInputTarget
		if(!userInputTarget.isValidScreen(screen)){
			throw new IllegalArgumentException("The UserInputTarget: " + userInputTarget.getName() + 
					" is not valid for the screen: " + screen.getName() + ".");
		}
		
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
	public void lockConfiguration(){
		if(!configurationLocked){
			configurationLocked = true;
			//to guarantee config can't change, make maps unmodifiable.
			this.singleAssignmentMap = Collections.unmodifiableMap(this.singleAssignmentMap);
			this.multiAssignmentMap = Collections.unmodifiableMap(this.multiAssignmentMap);
			this.configuredScreens = Collections.unmodifiableSet(this.configuredScreens);
		}
	}
	
	/**
	 * Returns the button assignment for the specified target or null if no 
	 * assignment can be found. An IllegalArgumentException will be thrown if the target
	 * parameter is null or is not a singleAssignment target or the configuration is not locked.
	 * @param target
	 * @return
	 */
	public ButtonConfiguration getButtonConfiguration(ScreensEnum screen, UserInputTargetEnum target){
		
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
	public List<ButtonConfiguration> getButtonConfigurations(ScreensEnum screen, UserInputTargetEnum target){
		
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
	public Set<ScreensEnum> getConfiguredScreens() {
		
		if(!configurationLocked){
			throw new IllegalArgumentException("The configuration must be locked before " +
				"accessing configuration details.");
		}
		
		return configuredScreens;
	}

	public String getLabel() {
		return label;
	}

}
