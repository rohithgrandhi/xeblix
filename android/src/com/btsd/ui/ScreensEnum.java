package com.btsd.ui;

public enum ScreensEnum {

	ROOT("Root"),
	DIRECTION("Direction"),
	SPEED("Speed"),
	VOLUME("Volume"),
	CHANNEL("Channel"),
	NUMERIC("Numeric"),
	OPTIONAL("Optional");
	
	private final String name;
	
	ScreensEnum(String name){
	
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static ScreensEnum getScreenEnum(String name){
		
		if(ROOT.getName().equalsIgnoreCase(name)){
			return ROOT;
		}else if(DIRECTION.getName().equalsIgnoreCase(name)){
			return DIRECTION;
		}else if(SPEED.getName().equalsIgnoreCase(name)){
			return SPEED;
		}else if(VOLUME.getName().equalsIgnoreCase(name)){
			return VOLUME;
		}else if(CHANNEL.getName().equalsIgnoreCase(name)){
			return CHANNEL;
		}else if(NUMERIC.getName().equalsIgnoreCase(name)){
			return NUMERIC;
		}else if(OPTIONAL.getName().equalsIgnoreCase(name)){
			return OPTIONAL;
		}
		
		return null;
	}
}
