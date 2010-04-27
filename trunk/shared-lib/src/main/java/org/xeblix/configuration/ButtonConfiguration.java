package org.xeblix.configuration;


/**
 * Immutable ButtonConfiguration.
 * @author klewelling
 *
 */
public final class ButtonConfiguration {

	private final UserInputTargetEnum userInputTargetEnum;
	private final ScreensEnum screen;
	private final Object command;
	private final String label;

	public ButtonConfiguration(ScreensEnum screen, UserInputTargetEnum userInputTargetEnum, 
		Object command, String label){
		
		if(label == null || userInputTargetEnum == null || screen == null){
			throw new IllegalArgumentException("This method does not accept null parameters.");
		}
		
		this.userInputTargetEnum = userInputTargetEnum;
		this.command = command;
		this.label = label;
		this.screen = screen;
	}
	
	public UserInputTargetEnum getUserInputTargetEnum() {
		return userInputTargetEnum;
	}

	public Object getCommand() {
		return command;
	}

	public String getLabel() {
		return label;
	}

	public ScreensEnum getScreen() {
		return screen;
	}
	
}
