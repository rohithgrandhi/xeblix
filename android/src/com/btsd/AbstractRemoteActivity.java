package com.btsd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View.OnClickListener;

import com.btsd.BTScrewDriverStateMachine.States;
import com.btsd.util.MessagesEnum;

public abstract class AbstractRemoteActivity extends Activity implements OnClickListener,/*OnGestureListener,*/CallbackActivity{

	private static final String TAG = "AbstractRemote";
	
	protected BTScrewDriverCallbackHandler callbackHandler;
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	public void sendMessage(Message message) {
		callbackHandler.sendMessage(message);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		BTScrewDriverCallbackHandler.focusChangedEvent(this, hasFocus);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(callbackHandler == null){
			callbackHandler = new BTScrewDriverCallbackHandler(this);
		}
		
		//register this activity so it will receive server messages
		BTSDApplication application =  (BTSDApplication)getApplication();
		application.getStateMachine().registerActivity(this);

	}
	
	protected BTSDApplication getBTSDApplication(){
		return (BTSDApplication)getApplication();
	}
	
	@Override
	public final void onMessage(MessagesEnum messagesEnum, Object message) {
		
		if(messagesEnum  == MessagesEnum.BT_CONNECTION_STATE){
			
			States state = (States)message;
			if(state == States.DISCONNECTED || state == States.CONNECTION_FAILED){
	    		
	    		BTScrewDriverAlert alert = new BTScrewDriverAlert(
					R.string.BT_SERVER_CONNECT_FAILED, true);
	    		alert.showAlert(this);
	    	}
		}else{
			onRemoteMessage(messagesEnum, message);
		}
		
	}
	
	protected abstract void onRemoteMessage(MessagesEnum messagesEnum, Object message);
}
