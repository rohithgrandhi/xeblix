package com.btsd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class BTScrewDriverAlert {

	private int textId;
	private boolean cancelable;
	private AlertDialog alert;
	
	public BTScrewDriverAlert(int textId, boolean cancelable){
		this.textId = textId;
		this.cancelable = cancelable;
	}
	
	public void showAlert(final Activity activity){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(textId).setCancelable(false);
		
		if(cancelable){
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					activity.finish();
				}
	
			});
		}
		alert = builder.create();
		alert.show();
	}
	
	public void dismisAlert(){
		
		if(alert != null){
			alert.dismiss();
		}
		
	}

	public synchronized boolean isUserCancelable() {
		return cancelable;
	}
	
}
