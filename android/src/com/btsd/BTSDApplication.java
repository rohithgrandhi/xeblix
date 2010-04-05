package com.btsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import com.btsd.bluetooth.BluetoothAccessor;
import com.btsd.bluetooth.BluetoothAdapter;
import com.btsd.ui.ButtonConfiguration;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.ScreensEnum;
import com.btsd.ui.UserInputTargetEnum;
import com.btsd.ui.configuration.TempConfigurationBuilder;
import com.btsd.ui.managehidhosts.AddHIDHostConfiguration;

public class BTSDApplication extends Application {

	private static final String TAG = "BTSDApplication";
	
	private BTScrewDriverStateMachine statemachine;
	
	private Map<String, Object> remoteCache;
	private List<RemoteConfiguration> hidHosts;
	
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
		
		if(hidHosts == null){
			hidHosts = new ArrayList<RemoteConfiguration>();
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
		
		this.hidHosts.clear();
		
		try{
			JSONArray hidHosts = remoteConfigurations.getJSONArray(Main.VALUE);
			
			for(int i=0; i < hidHosts.length(); i++){
				JSONObject jsonObject = hidHosts.getJSONObject(i);
				String address = jsonObject.getString("address");
				String hostName = jsonObject.getString("name");
				
				this.hidHosts.add(TempConfigurationBuilder.getHIDHostConfiguration(address, 
					hostName));
			}
			
		}catch(JSONException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public RemoteConfiguration getRemoteConfiguration(String name){
		
		if("vip1200".equalsIgnoreCase(name)){
			return TempConfigurationBuilder.getVIP1200Configuration();
		}else if("SONY_12_RM-YD010".equalsIgnoreCase(name)){
			return TempConfigurationBuilder.getSonyBraviaConfiguration();
		}else if(ADD_HID_HOST_CONFIGURATION_NAME.equalsIgnoreCase(name)){
			return new AddHIDHostConfiguration();
		}
		
		for(RemoteConfiguration hidRemoteConfig: this.hidHosts){
			ButtonConfiguration buttonConfig = hidRemoteConfig.getButtonConfiguration(
				ScreensEnum.ROOT, UserInputTargetEnum.REMOTE_NAME);
			if(buttonConfig.getCommand().toString().equalsIgnoreCase(name)){
				return hidRemoteConfig;
			}
		}
		
		return null;
	}
	
	public ArrayList<ButtonConfiguration> getRemoteConfigurationNames(){
		ArrayList<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,
				"vip1200", "DVR"));
		toReturn.add(new ButtonConfiguration(ScreensEnum.ROOT,UserInputTargetEnum.REMOTE_NAME,
				"SONY_12_RM-YD010", "TV"));
		
		for(RemoteConfiguration hidRemoteConfig: this.hidHosts){
			toReturn.add(hidRemoteConfig.getButtonConfiguration(
				ScreensEnum.ROOT, UserInputTargetEnum.REMOTE_NAME));
			
		}
		return toReturn;
	}

	public ArrayList<ButtonConfiguration> getHIDHostConfigurationNames(){
		ArrayList<ButtonConfiguration> toReturn = new ArrayList<ButtonConfiguration>();
		for(RemoteConfiguration hidRemoteConfig: this.hidHosts){
			toReturn.add(hidRemoteConfig.getButtonConfiguration(
				ScreensEnum.ROOT, UserInputTargetEnum.REMOTE_NAME));
			
		}
		return toReturn;
	}
	
	public Map<String, Object> getRemoteCache() {
		return remoteCache;
	}
	
}
