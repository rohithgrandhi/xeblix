package com.btsd.ui.managehidhosts;


public final class InitialState extends AbstractHIDRemoteState {

	private static InitialState instance = null;
	
	private InitialState(){}
	
	public static synchronized InitialState getInstance(){
		
		if(instance == null){
			instance = new InitialState();
		}
		
		return instance;
	}
	
	
}
