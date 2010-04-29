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
import android.util.Pair;

import com.btsd.bluetooth.BluetoothAccessor;
import com.btsd.ui.LIRCRemoteConfiguration;
import com.btsd.ui.RemoteConfiguration;
import com.btsd.ui.hidremote.HIDRemoteConfiguration;
import com.btsd.ui.managehidhosts.AddHIDHostConfiguration;

public class BTSDApplication extends Application {

	private static final String TAG = "BTSDApplication";
	
	private BTScrewDriverStateMachine statemachine;
	
	private Map<String, Object> remoteCache;
	private List<Pair<String, String>> hidHosts;
	private List<RemoteConfiguration> remoteConfigurations;
	private JSONObject hidTemplateConfiguration;
	
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
		
		if(hidHosts == null){
			hidHosts = new ArrayList<Pair<String,String>>();
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

	public void updateRemoteConfiguration(JSONObject remoteConfigurationsJSON){
		
		List<RemoteConfiguration> remoteConfigurations = this.remoteConfigurations;
		JSONObject hidTemplateConfiguration = this.hidTemplateConfiguration;
		
		
		List<Pair<String, String>> hidHosts = this.hidHosts;
		
		
		try{
			
			String type = remoteConfigurationsJSON.getString(Main.TYPE);
			
			if(Main.TYPE_HID_HOSTS.equalsIgnoreCase(type)){
				hidHosts.clear();
				
				//remove any HIDRemoteConfigurations if any exists
				ArrayList<RemoteConfiguration> toRemove = new ArrayList<RemoteConfiguration>();
				for(RemoteConfiguration config: remoteConfigurations){
					if(config instanceof HIDRemoteConfiguration){
						toRemove.add(config);
					}
				}
				
				for(RemoteConfiguration config: toRemove){
					remoteConfigurations.remove(config);
				}
				
				JSONArray hidHostsJSON = remoteConfigurationsJSON.getJSONArray(Main.VALUE);
				
				for(int i=0; i < hidHostsJSON.length(); i++){
					JSONObject jsonObject = hidHostsJSON.getJSONObject(i);
					String address = jsonObject.getString("address");
					String hostName = jsonObject.getString("name");
					
					hidHosts.add(Pair.create(address, hostName));
				
					//if the HIDtemplate config exists, go ahead and create the HIDRemoteConfigurations
					if(hidTemplateConfiguration != null){
						remoteConfigurations.add(new HIDRemoteConfiguration(hidTemplateConfiguration,
							address, hostName));
					}
				}
			}else if(Main.TYPE_REMOTE_CONFIGURATION.equalsIgnoreCase(type)){
				JSONArray remoteConfigs = remoteConfigurationsJSON.getJSONArray(Main.REMOTE_CONFIGURATION);
				
				remoteConfigurations.clear();
				
				for(int i=0; i < remoteConfigs.length(); i++){
					JSONObject jsonObject = remoteConfigs.getJSONObject(i);
					
					String remoteType = jsonObject.getString(Main.REMOTE_TYPE);
					if(Main.REMOTE_TYPE_LIRC.equalsIgnoreCase(remoteType)){
						remoteConfigurations.add(new LIRCRemoteConfiguration(jsonObject));
					}else if(Main.REMOTE_TYPE_HID.equalsIgnoreCase(remoteType)){
						this.hidTemplateConfiguration = jsonObject;
						hidTemplateConfiguration = jsonObject;
					}else{
						throw new IllegalArgumentException("Unknown RemoteType: " + remoteType);
					}
					
					remoteConfigurations.add(new AddHIDHostConfiguration());
					
				}
				//if the HIDHosts were received before the remote configuration then go ahead
				//and create the HIDRemoteConfigurations
				if(!hidHosts.isEmpty()){
					for(Pair<String,String> hidHost: hidHosts){
						remoteConfigurations.add(new HIDRemoteConfiguration(hidTemplateConfiguration,
								hidHost.first, hidHost.second));
					}
				}
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
			if(!ADD_HID_HOST_CONFIGURATION_NAME.equalsIgnoreCase(buttonConfig.getLabel())){
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
