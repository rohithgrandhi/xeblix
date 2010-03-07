package com.btsd;

import com.btsd.util.MessagesEnum;

import android.app.Activity;
import android.os.Message;

public interface CallbackActivity {

	public void sendMessage(Message message);
	
	public void onBTSDMessage(String message);
	
	public void onMessage(MessagesEnum messagesEnum, Object message);
	
	public void showCancelableDialog(int title, String message);
	
	public Activity getActivity();
}
