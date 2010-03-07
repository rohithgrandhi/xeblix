package com.btsd.prototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.btsd.R;

public class RemotePrototype extends Activity{

	//private GestureDetector gestureDetector = null;
	private static final String TAG = "RemotePrototype";
	
	private int[][] textViewsLocations = new int[9][4];
	private final TextView[] textViews = new TextView[9];
	//this is used to figure out when to caluculate textview locations, don't
	//want to calculate if we simply lost focus due to popup or lock screen
	private boolean onResumeCalled = false;
	private TextView selectedTextView = null;
	private Vibrator vibrator = null; 
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && onResumeCalled){
			int[] textViewIds = new int[]{R.id.Button01, R.id.Button02, R.id.Button03, 
					R.id.Button04, R.id.directionButton, R.id.Button06, R.id.Button07, R.id.Button08, 
					R.id.Button09};
			
			for(int i=0; i < textViewIds.length; i++){
				textViews[i] = (TextView)findViewById(textViewIds[i]);
				int[] xy = new int[2];
				textViews[i].getLocationOnScreen(xy);
				textViewsLocations[i][0] = xy[0];
				textViewsLocations[i][1] = xy[1];
				textViewsLocations[i][2] = xy[0] + textViews[i].getWidth();
				textViewsLocations[i][3] = xy[1] + textViews[i].getHeight();
				
				/*Log.i(TAG, "TextView: " + i + " x:" + textViewsLocations[i][0] + " y:" + 
						textViewsLocations[i][1] + " x+w:" + textViewsLocations[i][2] + 
						" y+h: " + textViewsLocations[i][3]);*/
			}
			onResumeCalled = false;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		onResumeCalled = true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_prototype);
		
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		//gestureDetector = new GestureDetector(this, this);
		
	}

	public void onClick(View v) {
		
		if(v == null){
			return;
		}
		
		if(v.getId() == R.id.directionButton){
			Log.i(TAG, "Direction Prototype clicked");
			Intent intent = new Intent("com.btsd.DirectionPrototype");
			startActivity(intent);
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e(TAG, "onTouchEvent:" + event.getAction());
		
		final float x = event.getX();
		final float y = event.getY();
		
		//Log.i(TAG, "me x:" + x + " y: " + y);
		final TextView selectedTextView = this.selectedTextView;
		TextView textView = null;
		
		for(int i=0; i < textViewsLocations.length; i++){
			if( (x >= textViewsLocations[i][0] && x < textViewsLocations[i][2]) && 
				y >= textViewsLocations[i][1] && y < textViewsLocations[i][3]){
				
				textView = textViews[i];
				break;
			}
		}
		
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			textView.setPressed(true);
			this.selectedTextView = textView;
			vibrator.vibrate(25);
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(selectedTextView != textView){
				selectedTextView.setPressed(false);
				textView.setPressed(true);
				this.selectedTextView = textView;
				vibrator.vibrate(25);
			}
			
			break;
		case MotionEvent.ACTION_UP:
			
			//user let up from the touch screen
			this.selectedTextView.setPressed(false);
			this.selectedTextView  =null;
			onClick(textView);
			break;
		default:
			break;
		}
		
		return true;
	}
	

	
	
}
