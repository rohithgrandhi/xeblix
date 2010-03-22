package com.btsd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.btsd.BTScrewDriverStateMachine.States;
import com.btsd.util.MessagesEnum;

public abstract class AbstractRemoteActivity extends Activity implements OnClickListener,/*OnGestureListener,*/CallbackActivity,DialogInterface.OnClickListener{

	private static final String TAG = "AbstractRemote";
	
	protected BTScrewDriverCallbackHandler callbackHandler;
	
	protected AlertDialog alertDialog;
	private EditText pincodeView = null;
	
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
	
	@Override
	public void showCancelableDialog(int title, int message) {
		
		String messageString = getString(message);
		showCancelableDialog(title, messageString);
		
	}
	
	@Override
	public void hideDialog() {
		AlertDialog alertDialog = this.alertDialog;
		if(alertDialog != null){
			if(alertDialog.isShowing()){
				alertDialog.dismiss();
			}
			alertDialog = null;
			this.alertDialog = null;
		}
		
		if(pincodeView != null){
			pincodeView = null;
		}
	}
	
	@Override
	public void showCancelableDialog(int title, String message) {
		AlertDialog alertDialog = this.alertDialog;
		
		if(alertDialog != null){
			if(alertDialog.isShowing()){
				alertDialog.dismiss();
			}
			alertDialog = null;
			this.alertDialog = null;
		}
		
		if(alertDialog == null){
			alertDialog = new AlertDialog.Builder(this)
		        .setTitle(title)
		        .setNegativeButton(android.R.string.cancel, this).
		        setMessage(message).create();
			this.alertDialog = alertDialog;
		}else{
			if(alertDialog.isShowing()){
				alertDialog.hide();
			}
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
		}
		
		alertDialog.show();
	}
	
	@Override
	public void showPinCodeDialog() {
		
		if(pincodeView == null){
			pincodeView = new EditText(this);
		}
		
		if(alertDialog != null){
			if(alertDialog.isShowing()){
				alertDialog.dismiss();
			}
			alertDialog = null;
		}
		alertDialog = new AlertDialog.Builder(this)
			.setCancelable(false)
	        .setTitle(R.string.PINCODE_DIALOG_TITLE)
	        .setPositiveButton(android.R.string.ok, this)
	        .setNegativeButton(android.R.string.cancel, this).
	        setView(pincodeView).create();
		alertDialog.show();
		
	}
	
	@Override
    public String getPincode() {
    	if(pincodeView != null){
    		String toReturn = pincodeView.getText().toString();
    		if(toReturn.trim().equals("")){
    			return null;
    		}else{
    			return toReturn;
    		}
    	}
    	
    	return null;
    }
	
	@Override
	public Activity getActivity() {
		return this;
	}
}
