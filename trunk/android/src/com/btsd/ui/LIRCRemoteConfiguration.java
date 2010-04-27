package com.btsd.ui;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xeblix.configuration.ButtonConfiguration;
import org.xeblix.configuration.RemoteConfigurationContainer;
import org.xeblix.configuration.ScreensEnum;
import org.xeblix.configuration.UserInputTargetEnum;

import com.btsd.CallbackActivity;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;

public final class LIRCRemoteConfiguration extends RemoteConfiguration {
	
	private int repeatCount;
	
	public LIRCRemoteConfiguration(JSONObject remoteConfig){
		
		String name = null;
		String label = null;
		try{
			name = remoteConfig.getString(Main.REMOTE_NAME);
			label = remoteConfig.getString(Main.REMOTE_LABEL);
			repeatCount = remoteConfig.getInt(Main.LIRC_REPEAT_COUNT);
			
		}catch(JSONException ex){
			throw new RuntimeException("Unable to parse LIRC remote configuration. " +
				"Either name, lable, or repeat count is invalid.");
		}
		
		RemoteConfigurationContainer container = RemoteConfigurationContainer.
			parseButtonConfiguration(remoteConfig, label);
		container.addButtonConfiguration(new ButtonConfiguration(ScreensEnum.ROOT,
			UserInputTargetEnum.REMOTE_NAME,name, label));
		container.lockConfiguration();
		setRemoteConfigurationContainer(container);
		
	}
	
	public final int getRepeatCount() {
		return repeatCount;
	}
	
	@Override
	public JSONObject getCommand(ScreensEnum screen, UserInputTargetEnum target,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		ButtonConfiguration remoteName = getButtonConfiguration(ScreensEnum.ROOT, 
				UserInputTargetEnum.REMOTE_NAME);
		
		ButtonConfiguration buttonConfiguration = getButtonConfiguration(screen, target);
		
		return ServerMessages.getLIRCCommand(remoteName.getCommand().toString(), 
				buttonConfiguration.getCommand().toString(), getRepeatCount());
	}

	@Override
	public JSONObject getCommand(ButtonConfiguration buttonConfiguration,
		Map<String, Object> remoteCache, CallbackActivity activity) {
		
		ButtonConfiguration remoteName = getButtonConfiguration(ScreensEnum.ROOT, 
				UserInputTargetEnum.REMOTE_NAME);
		
		return ServerMessages.getLIRCCommand(remoteName.getCommand().toString(), 
				buttonConfiguration.getCommand().toString(), getRepeatCount());
	}
	
	@Override
	public JSONObject validateState(Map<String, Object> remoteCache,
			CallbackActivity activity) {
		return null;
	}
	
	@Override
	public JSONObject serverInteraction(JSONObject messageFromServer,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		String type = null;
		try{
			type = (String)messageFromServer.get(Main.TYPE);
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		if(Main.TYPE_UNPAIR_HID_HOST.equalsIgnoreCase(type)){
			String address = null;
			try{
				address = messageFromServer.getString(Main.HOST_ADDRESS);
			}catch(JSONException ex){
				throw new RuntimeException(ex.getMessage(), ex);
			}
			activity.showCancelableDialog(R.string.INFO, R.string.REMOVING_HID_HOST);
			return ServerMessages.getRemovePairedHost(address);
		}
		
		return null;
	}
	
	@Override
	public JSONObject alertClicked(int button, Map<String, Object> remoteCache,
			CallbackActivity activity) {
		
		return null;
	}
	
	@Override
	public JSONObject remoteConfigurationRefreshed(
			List<ButtonConfiguration> remoteConfigNames,
			Map<String, Object> remoteCache, CallbackActivity activity) {
		
		activity.hideDialog();
		return null;
	}
	
}
