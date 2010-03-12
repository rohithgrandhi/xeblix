package com.btsd.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.btsd.AbstractRemoteActivity;
import com.btsd.R;
import com.btsd.util.MessagesEnum;

public class GestureScreenActivity extends AbstractRemoteActivity implements OnGestureListener,DialogInterface.OnClickListener{

	private static final String TAG = "GestureScreenActivity";
	
	private GestureDetector gestureDetector = null;
	private boolean newGesture;
	private VelocityTracker velocityTracker;
	private int mMinimumFlingVelocity;
	
	private float lastY;
	private float lastX;
	
	private LinearLayout tapForEnterLayout = null;
	private boolean onResumeCalled = false;
	private int[][] textViewsLocations = new int[4][4];
	private boolean[] textViewStates = new boolean[]{true, true, true, true};
	private final View[] textViews = new View[4];
	
	private RemoteConfiguration remoteConfiguration;
	private ScreensEnum selectedScreen;

	private static final int MIN_MOVE_THRESHOLD = 50;
	
	private Vibrator vibrator = null; 
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(tapForEnterLayout != null && tapForEnterLayout.isPressed()){
				tapForEnterLayout.setPressed(false);
			}else{
				for(View pressedView: textViews){
					if(pressedView.isPressed()){
						pressedView.setPressed(false);
						break;
					}
				}
			}
			
		}
	};
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && onResumeCalled){
			for(int i=0; i < textViews.length; i++){
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
		
		setContentView(R.layout.gesture_screen);
		
		gestureDetector = new GestureDetector(this, this);
		
		Bundle extras = getIntent().getExtras();
		String remoteConfig = extras.getString(RootActivity.RemoteConfigurationBundleKey);
		String selectedScreenString = extras.getString(RootActivity.ScreenBundleKey);
		this.selectedScreen = ScreensEnum.getScreenEnum(selectedScreenString);
		RemoteConfiguration remoteConfiguration = getBTSDApplication().
			getRemoteConfiguration(remoteConfig);
		this.remoteConfiguration = remoteConfiguration;
		
		configLayout();
		
		tapForEnterLayout = (LinearLayout)findViewById(R.id.TapForEnterLayout);
		
		final ViewConfiguration configuration = ViewConfiguration.get(this);
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity() * 5;
		 
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		int[] textViewIds = new int[]{R.id.GS1, R.id.GS2, R.id.GS4, R.id.GS5};
		
		View[] textViews = this.textViews;
		for(int i=0; i < textViewIds.length; i++){
			textViews[i] = (TextView)findViewById(textViewIds[i]);
		}
		
	}
	
	private void configLayout(){
		
		//if the buttons are defined fill in the text
		TextView textView = (TextView)findViewById(R.id.GS2);
		textView.setText("Exit");
		
		if(!setButtonText(UserInputTargetEnum.GESTURE_SCREEN_1, R.id.GS1)){
			textViewStates[0] = false;
		}
		setButtonText(UserInputTargetEnum.GESTURE_SCREEN_3, R.id.GS3_5);
		if(!setButtonText(UserInputTargetEnum.GESTURE_SCREEN_4, R.id.GS4)){
			textViewStates[2] = false;
		}
		if(!setButtonText(UserInputTargetEnum.GESTURE_SCREEN_5, R.id.GS5)){
			textViewStates[3] = false;
		}
		
		//next check the gesture arrows
		setButtonText(UserInputTargetEnum.GESTURE_SCREEN_6, 
			UserInputTargetEnum.GESTURE_SCREEN_7, R.id.GS6);
		setButtonText(UserInputTargetEnum.GESTURE_SCREEN_8, 
			UserInputTargetEnum.GESTURE_SCREEN_9, R.id.GS8);
		setButtonText(UserInputTargetEnum.GESTURE_SCREEN_10, 
			UserInputTargetEnum.GESTURE_SCREEN_11, R.id.GS10);
		setButtonText(UserInputTargetEnum.GESTURE_SCREEN_12, 
			UserInputTargetEnum.GESTURE_SCREEN_13, R.id.GS12);
		
	}
	
	private boolean setButtonText(UserInputTargetEnum targetEnum, int viewId){
		
		ButtonConfiguration buttonConfig = remoteConfiguration.
			getButtonConfiguration(selectedScreen, targetEnum);
		if(buttonConfig != null){
			
			TextView textView = (TextView)findViewById(viewId);
			textView.setText(buttonConfig.getLabel());
			return true;
		}
		return false;
	}
	
	private void setButtonText(UserInputTargetEnum scrollTarget, 
		UserInputTargetEnum flingTarget, int viewId){
		
		ButtonConfiguration scrollConfig = remoteConfiguration.
			getButtonConfiguration(selectedScreen, scrollTarget);
		
		ButtonConfiguration flingConfig = remoteConfiguration.
			getButtonConfiguration(selectedScreen, flingTarget);
		
		TextView textView = (TextView)findViewById(viewId);
		
		if(scrollConfig == null && flingConfig == null){
			textView.setBackgroundDrawable(null);
			textView.setText(null);
			return;
		}
		
		if(scrollConfig != null && flingConfig == null){
			flingConfig = scrollConfig;
		}else if(scrollConfig == null && flingConfig != null){
			scrollConfig = flingConfig;
		}
		
		if(scrollConfig.getLabel().equalsIgnoreCase(flingConfig.getLabel())){
			textView.setText("Swipe or Fling\n for " + scrollConfig.getLabel() + 
				".\n");
		}else{
			textView.setText("Swipe for " + scrollConfig.getLabel() + 
					".\nFling for " + flingConfig.getLabel());
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		if(messagesEnum == MessagesEnum.MESSAGE_FROM_SERVER){
			JSONObject serverMessage =  remoteConfiguration.
				serverInteraction((JSONObject)message, 
				getBTSDApplication().getRemoteCache(), this);
			if(serverMessage != null){
				getBTSDApplication().getStateMachine().messageToServer(serverMessage);
			}
		}else{
			throw new IllegalArgumentException("Unexpected Message: " + messagesEnum.getId());
		}
	}

	@Override
	public void onClick(View v) {
		

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog == alertDialog){
			if(which == DialogInterface.BUTTON2){
				remoteConfiguration.alertCanceled(getBTSDApplication().getRemoteCache(), this);
			}
		}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		Log.e(TAG, "onDown");
		newGesture = true;
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		this.velocityTracker.recycle();
		this.velocityTracker = null;
		Log.e(TAG, "velocityX: " + velocityX + " velocityY: " + velocityY);
		//check if the gesture was fast enough
		if(Math.abs(velocityX) < mMinimumFlingVelocity && 
			Math.abs(velocityY) < mMinimumFlingVelocity){
			
			return false;
		}
		if(Math.abs(velocityX) > Math.abs(velocityY)){
			if(velocityX > 0){
				//right
				sendCommand(UserInputTargetEnum.GESTURE_SCREEN_13);
			}else{
				//left
				sendCommand(UserInputTargetEnum.GESTURE_SCREEN_11);
			}
		}else{
			if(velocityY >0){
				//down
				sendCommand(UserInputTargetEnum.GESTURE_SCREEN_9);
			}else{
				//up
				sendCommand(UserInputTargetEnum.GESTURE_SCREEN_7);
			}
		}
		return false;//onScroll(e1, e2, 0, 0);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.e(TAG, "onLongPress");
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		if(newGesture){
			lastY = e1.getY();
			lastX = e1.getX();
			newGesture = false;
			Log.e(TAG, "newGesture");
		}
		
		
		
		if(e1.getY() - e2.getY() > 0 && lastY - e2.getY() > MIN_MOVE_THRESHOLD){
			if(sendScrollCommand(UserInputTargetEnum.GESTURE_SCREEN_6)){
				lastY = e2.getY();
				lastX = e2.getX();
			}
		}else if(e2.getY() - e1.getY() > 0 && e2.getY() - lastY > MIN_MOVE_THRESHOLD){
			if(sendScrollCommand(UserInputTargetEnum.GESTURE_SCREEN_8)){
				lastY = e2.getY();
				lastX = e2.getX();
			}
		}
		
		if(e1.getX() - e2.getX() > 0 && lastX - e2.getX() > MIN_MOVE_THRESHOLD){
			if(sendScrollCommand(UserInputTargetEnum.GESTURE_SCREEN_10)){
				lastX = e2.getX();
				lastY = e2.getY();
			}
		}else if(e2.getX() - e1.getX() > 0 && e2.getX() - lastX > MIN_MOVE_THRESHOLD){
			if(sendScrollCommand(UserInputTargetEnum.GESTURE_SCREEN_12)){
				lastX = e2.getX();
				lastY = e2.getY();
			}
			
		}
		
		return false;
	}

	private boolean sendScrollCommand(UserInputTargetEnum target){
		
		//if the user has started a fling then don't send the non-fling 
		//command
		final VelocityTracker tempVelocityTracker = velocityTracker;
		tempVelocityTracker.computeCurrentVelocity(1000);
        final float velocityY = tempVelocityTracker.getYVelocity();
        final float velocityX = tempVelocityTracker.getXVelocity();
		
		if ((Math.abs(velocityY) > mMinimumFlingVelocity)|| 
			(Math.abs(velocityX) > mMinimumFlingVelocity)){
            
			return false;
        }
		
		return sendCommand(target);
	}

	private boolean sendCommand(UserInputTargetEnum target) {
		vibrator.vibrate(25);
		
		JSONObject serverMessage = this.remoteConfiguration.getCommand(
				this.selectedScreen, target,getBTSDApplication().getRemoteCache(), this);
		getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		Log.e(TAG, "send serverMessage:" + serverMessage.toString());
		return true;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {
		Log.e(TAG, "onShowPress");
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		//figure out which button was pressed
		final float x = e.getX();
		final float y = e.getY();
		
		//tapForEnterLayout is the default selection
		boolean enabled = true;
		View view = tapForEnterLayout;
		for(int i=0; i < textViewsLocations.length; i++){
			if( (x >= textViewsLocations[i][0] && x < textViewsLocations[i][2]) && 
				y >= textViewsLocations[i][1] && y < textViewsLocations[i][3]){
				
				view = textViews[i];
				enabled = textViewStates[i];
				break;
			}
		}
		
		JSONObject serverMessage = null;
		if(view.getId() == R.id.GS1){
			serverMessage = this.remoteConfiguration.getCommand(
					this.selectedScreen, UserInputTargetEnum.GESTURE_SCREEN_1,
					getBTSDApplication().getRemoteCache(), this);
		}else if(view.getId() == R.id.GS2){
			this.finish();
			return false;
		}else if(view.getId() == R.id.GS4){
			serverMessage = this.remoteConfiguration.getCommand(
					this.selectedScreen, UserInputTargetEnum.GESTURE_SCREEN_4,
					getBTSDApplication().getRemoteCache(), this);
		}else if(view.getId() == R.id.GS5){
			serverMessage = this.remoteConfiguration.getCommand(
					this.selectedScreen, UserInputTargetEnum.GESTURE_SCREEN_5,
					getBTSDApplication().getRemoteCache(), this);
		}else{
			//selected the default "OK" button
			serverMessage = this.remoteConfiguration.getCommand(
					this.selectedScreen, UserInputTargetEnum.GESTURE_SCREEN_3,
					getBTSDApplication().getRemoteCache(), this);
		}
		
		if(!enabled){
			//if a button is not enabled then the default "OK" button was 
			//selected
			serverMessage = this.remoteConfiguration.getCommand(
					this.selectedScreen, UserInputTargetEnum.GESTURE_SCREEN_3,
					getBTSDApplication().getRemoteCache(), this);
		}
		
		Log.e(TAG, "onSingleTapUp");
		vibrator.vibrate(25);
		getBTSDApplication().getStateMachine().messageToServer(serverMessage);
		Log.e(TAG, "send serverMessage:" + serverMessage.toString());
		view.setPressed(true);
		handler.sendMessageDelayed(Message.obtain(), 100);
		return false;
		
	}
	
}
