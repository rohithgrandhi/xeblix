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
	 * Clears any alerts created from the showCancelableDialog, or does nothing if
	 * there is no alert
	 */
	public void hideCancelableDialog();
	
	public Activity getActivity();
}
