package com.btsd.ui.hidremote;


public class InitialState extends AbstractHIDRemoteState {

	private static InitialState instance = null;
	
	private InitialState(){}
	
	public static synchronized InitialState getInstance(){
		
		if(instance == null){
			instance = new InitialState();
		}
		
		return instance;
	}
	
}
