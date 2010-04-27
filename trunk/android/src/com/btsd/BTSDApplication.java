package com.btsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.ButtonConfiguration;
import org.xeblix.configuration.ScreensEnum;
import org.xeblix.configuration.UserInputTargetEnum;

import android.app.Application;
import android.util.Log;

import com.btsd.bluetooth.BluetoothAccessor;
import com.btsd.ui.LIRCRemoteConfiguration;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.hidremote.HIDRemoteConfiguration;
import com.btsd.ui.managehidhosts.AddHIDHostConfiguration;

public class BTSDApplication extends Application {

	private static final String TAG = "BTSDApplication";
	
	private BTScrewDriverStateMachine statemachine;
	
	private Map<String, Object> remoteCache;
	//private List<RemoteConfiguration> hidHosts;
	private List<RemoteConfiguration> remoteConfigurations;
	
	public static final String ADD_HID_HOST_CONFIGURATION_NAME = 
		"??ADD_HID_HOST_CONFIGURATION??";
	
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
		
		if(remoteConfigurations == null){
			remoteConfigurations = new ArrayList<RemoteConfiguration>();
		}
		
		//for android2.0+ need to do some initialization in a thread that has 
		//had looper.prepare called
		BluetoothAccessor.getInstance().getBluetoothAdapter(this);
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

	public void updateRemoteConfiguration(JSONObject remoteConfigurations){
		
		this.remoteConfigurations.clear();
		//this.hidHosts.clear();
		
		try{
			JSONArray remoteConfigs = remoteConfigurations.getJSONArray(Main.REMOTE_CONFIGURATION);
			
			for(int i=0; i < remoteConfigs.length(); i++){
				JSONObject jsonObject = remoteConfigs.getJSONObject(i);
				
				String remoteType = jsonObject.getString(Main.REMOTE_TYPE);
				if(Main.REMOTE_TYPE_LIRC.equalsIgnoreCase(remoteType)){
					this.remoteConfigurations.add(new LIRCRemoteConfiguration(jsonObject));
				}else if(Main.REMOTE_TYPE_HID.equalsIgnoreCase(remoteType)){
					this.remoteConfigurations.add(new HIDRemoteConfiguration(jsonObject));
				}else{
					throw new IllegalArgumentException("Unknown RemoteType: " + remoteType);
				}
				
				this.remoteConfigurations.add(new AddHIDHostConfiguration());
			}
			
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public RemoteConfiguration getRemoteConfiguration(String name){
		
		for(RemoteConfiguration config: this.remoteConfigurations){
			
			ButtonConfiguration buttonConfig = config.getButtonConfiguration(ScreensEnum.ROOT, 
				UserInputTargetEnum.REMOTE_NAME);
			if(buttonConfig == null){
				continue;
			}
			
			if(buttonConfig.getCommand().toString().equalsIgnoreCase(name)){
				return config;
			}
		}
		
		return null;
		
	}
	
	public ArrayList<ButtonConfiguration> getRemoteConfigurationNames(){
		
		ArrayList<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		
		for(RemoteConfiguration config: this.remoteConfigurations){
			ButtonConfiguration buttonConfig = config.getButtonConfiguration(ScreensEnum.ROOT, 
				UserInputTargetEnum.REMOTE_NAME);
			//the ADDHIDHostConfiguration will return null so check for null and skip it
			if(buttonConfig != null){
				toReturn.add(buttonConfig);
			}
		}

		return toReturn;
	}

	public ArrayList<ButtonConfiguration> getHIDHostConfigurationNames(){
		ArrayList<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		
		for(RemoteConfiguration config: this.remoteConfigurations){
			if(config instanceof HIDRemoteConfiguration){
				toReturn.add(config.getButtonConfiguration(ScreensEnum.ROOT, 
				UserInputTargetEnum.REMOTE_NAME));
			}
		}
		return toReturn;
	}
	
	public Map<String, Object> getRemoteCache() {
		return remoteCache;
	}
	
}
