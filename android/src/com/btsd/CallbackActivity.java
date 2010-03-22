package com.btsd;

import android.app.Activity;
import android.os.Message;

import com.btsd.util.MessagesEnum;

public interface CallbackActivity {

	public void sendMessage(Message message);
	
	public void onMessage(MessagesEnum messagesEnum, Object message);
	
	public void showCancelableDialog(int title, int message);
	
	public void showCancelableDialog(int title, String message);
	
	/**
	 * TODO: replace show*Dialog with a way to create dialogs that don't require
	 * UI elements (don't want to create UI elements outside of activities)
	 * This method is a hack, replace ASAP
	 */
	public void showPinCodeDialog();
	
	/**
	 * Returns the user entered pincode, or null if the user didn't enter a pincode
	 * @return
	 */
	public String getPincode();
	
	/**
	 * Clears any alerts created from the showCancelableDialog, or does nothing if
	 * there is no alert
	 */
	public void hideDialog();
	
	public Activity getActivity();
	
	/**
	 * Programmatically change the current remote configuration to the previous 
	 * selection. This is used my the Add HID Host and Manage HID Hosts configuration
	 * when they are finished performing tasks or are canceled. This method will
	 * remove any HIDRemote alerts. 
	 */
	public void returnToPreviousRemoteConfiguration();
	
	/**
	 * Refreshes the configured remote from the server. A message will be sent to the
	 * RemoteConfigurations once the refresh has completed.
	 */
	public void refreshConfiguredRemotes();
	
	/**
	 * Programmatically change the configured remote. This method will remove 
	 * any HIDRemote alerts.This is used my the Add HID Host and Manage HID Hosts configuration
	 * when they are finished performing tasks.
	 * @param name
	 */
	public void selectConfiguredRemote(String name);
}
