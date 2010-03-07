package com.btsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;
import com.btsd.ui.configuration.TempConfigurationBuilder;

public class BTSDApplication extends Application {

	private static final String TAG = "BTSDApplication";
	
	private BTScrewDriverStateMachine statemachine;
	
	private Map<String, Object> remoteCache;
	
	/*@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		Log.e(TAG, "onConfigurationChanged");
		
	}*/

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.e(TAG, "onCreate");
		
		if(statemachine == null){
			statemachine = new BTScrewDriverStateMachine();
			statemachine.start();
			statemachine.onStart(this);
		}
		
		if(remoteCache == null){
			remoteCache = new HashMap<String, Object>();
		}
	}

	public BTScrewDriverStateMachine getStateMachine(){
		return this.statemachine;
	}
	
	/*@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		Log.e(TAG, "onLowMemory");
	}*/

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		Log.e(TAG, "onTerminate");
		
		if(statemachine != null){
			statemachine.shutdownStateMachine();
			statemachine = null;
		}
	}

	public RemoteConfiguration getRemoteConfiguration(String name){
		
		if("vip1200".equalsIgnoreCase(name)){
			return TempConfigurationBuilder.getVIP1200Configuration();
		}else if("SONY_12_RM-YD010".equalsIgnoreCase(name)){
			return TempConfigurationBuilder.getSonyBraviaConfiguration();
		}else if("MEDIA-PC".equalsIgnoreCase(name)){
			return TempConfigurationBuilder.getMediaPCConfiguration();
		}
		
		return null;
	}
	
	public ArrayList<ButtonConfiguration> getRemoteConfigurationNames(){
		ArrayList<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,
				"vip1200", "DVR"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,
				"SONY_12_RM-YD010", "TV"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,
				"MEDIA-PC", "MEDIA-PC"));
		return toReturn;
	}

	public Map<String, Object> getRemoteCache() {
		return remoteCache;
	}
	
}
