package com.btsd.prototype;

import android.app.Activity;
import android.content.Context;
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
import com.btsd.ServerMessages;
import com.btsd.util.MessagesEnum;

public final class DirectionPrototype extends AbstractRemoteActivity implements OnGestureListener{

	private static final String TAG = "DirectionPrototype";
	private GestureDetector gestureDetector = null;
	private boolean newGesture;
	private VelocityTracker velocityTracker;
	private int mMinimumFlingVelocity;
	
	private float lastY;
	private float lastX;
	
	private LinearLayout tapForEnterLayout = null;
	private final String tapForEnterCommand = "OK";
	private boolean onResumeCalled = false;
	private int[][] textViewsLocations = new int[2][4];
	private final View[] textViews = new View[2];
	private final String[] commands = new String[]{"BACK"};
	private Vibrator vibrator = null; 
	
	private static final int MIN_MOVE_THRESHOLD = 50;
	
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
			int[] textViewIds = new int[]{R.id.directionBackButton};
			
			for(int i=0; i < textViewIds.length; i++){
				textViews[i] = (View)findViewById(textViewIds[i]);
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
		setContentView(R.layout.prototype_direction);
		
		gestureDetector = new GestureDetector(this, this);
		
		TextView textView = (TextView)findViewById(R.id.DirectionArrowUp);
		textView.setText("Swipe for Up.\nFling for Page Up");
		
		textView = (TextView)findViewById(R.id.DirectionArrowLeft);
		textView.setText("Swipe for Left.\nFling for Back 24 Hours");
		
		textView = (TextView)findViewById(R.id.DirectionArrowRight);
		textView.setText("Swipe for Right.\nFling for Forward 24 Hours");
		
		textView = (TextView)findViewById(R.id.DirectionArrowDown);
		textView.setText("Swipe for Down.\nFling for Page Down");
		
		tapForEnterLayout = (LinearLayout)findViewById(R.id.TapForEnterLayout);
		
		 final ViewConfiguration configuration = ViewConfiguration.get(this);
		 mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity() * 5;
		 
		 vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
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
				vibrator.vibrate(25);
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getLIRCCommand("vip1200", "FF", 2));
				Log.e(TAG, "send Fast Forward command");
			}else{
				//left
				vibrator.vibrate(25);
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getLIRCCommand("vip1200", "REW", 2));
				Log.e(TAG, "send Rewind command");
			}
		}else{
			if(velocityY >0){
				//down
				vibrator.vibrate(25);
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getLIRCCommand("vip1200", "CHPG-", 2));
				Log.e(TAG, "send PageDown command");
			}else{
				//up
				vibrator.vibrate(25);
				getBTSDApplication().getStateMachine().messageToServer(
						ServerMessages.getLIRCCommand("vip1200", "CHPG+", 2));
				Log.e(TAG, "send PageUp command");
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
			if(sendCommand("vip1200", "UP", 2)){
				lastY = e2.getY();
				lastX = e2.getX();
			}
		}else if(e2.getY() - e1.getY() > 0 && e2.getY() - lastY > MIN_MOVE_THRESHOLD){
			if(sendCommand("vip1200", "DOWN", 2)){
				lastY = e2.getY();
				lastX = e2.getX();
			}
		}
		
		if(e1.getX() - e2.getX() > 0 && lastX - e2.getX() > MIN_MOVE_THRESHOLD){
			if(sendCommand("vip1200", "LEFT", 2)){
				lastX = e2.getX();
				lastY = e2.getY();
			}
		}else if(e2.getX() - e1.getX() > 0 && e2.getX() - lastX > MIN_MOVE_THRESHOLD){
			if(sendCommand("vip1200", "RIGHT", 2)){
				lastX = e2.getX();
				lastY = e2.getY();
			}
			
		}
		
		return false;
	}

	private boolean sendCommand(String remote, String command, int count){

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
		
		vibrator.vibrate(25);
		getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getLIRCCommand(remote, command, count));
		Log.e(TAG, "send " + command + " command");
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
		View view = tapForEnterLayout;
		String command = tapForEnterCommand;
		for(int i=0; i < textViewsLocations.length; i++){
			if( (x >= textViewsLocations[i][0] && x < textViewsLocations[i][2]) && 
				y >= textViewsLocations[i][1] && y < textViewsLocations[i][3]){
				
				view = textViews[i];
				command = commands[i];
				break;
			}
		}
		
		Log.e(TAG, "onSingleTapUp");
		vibrator.vibrate(25);
		getBTSDApplication().getStateMachine().messageToServer(
				ServerMessages.getLIRCCommand("vip1200", command, 2));
		Log.e(TAG, "send " + command + " command");
		view.setPressed(true);
		handler.sendMessageDelayed(Message.obtain(), 100);
		return false;
	}
	
	@Override
	public void onBTSDMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showCancelableDialog(int title, String message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}
}
