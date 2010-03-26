package com.btsd.ui;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.btsd.CallbackActivity;
import com.btsd.ServerMessages;

public final class LIRCRemoteConfiguration extends RemoteConfiguration {
	
	private int repeatCount;
	
	public LIRCRemoteConfiguration(int repeatCount){
		this.repeatCount = repeatCount;
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
	public JSONObject serverInteraction(JSONObject messageFromServer,
			Map<String, Object> remoteCache, CallbackActivity activity) {
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
		return null;
	}
}
