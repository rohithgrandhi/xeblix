package com.btsd.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.btsd.R;

public class ModifierButtons extends LinearLayout{

	private ImageView windowsImage;
	private ImageView alttImage;
	private ImageView ctrlImage;
	
	private ImageView windowsIndicator;
	private ImageView altIndicator;
	private ImageView ctrlIndicator;
	
	private ToggleState windowsState = ToggleState.OFF;
	private ToggleState altState = ToggleState.OFF;
	private ToggleState ctrlState = ToggleState.OFF;
	
	public ModifierButtons(final Context context, AttributeSet attribs){
		super(context,attribs);
		
		this.setOrientation(HORIZONTAL);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.modifier_buttons, this);
		
		windowsImage = (ImageView)findViewById(R.id.img_win);
		alttImage = (ImageView)findViewById(R.id.img_alt);
		ctrlImage = (ImageView)findViewById(R.id.img_ctrl);
		
		windowsIndicator = (ImageView)findViewById(R.id.ind_win);
		altIndicator = (ImageView)findViewById(R.id.ind_alt);
		ctrlIndicator = (ImageView)findViewById(R.id.ind_ctrl);
	}
	
	public void toggleWindowsKey(){
		if(windowsState == ToggleState.OFF){
			windowsState = ToggleState.ON;
			windowsImage.setImageResource(R.drawable.modifier_windows_on);
			windowsIndicator.setImageResource(R.drawable.modifier_ind_on_l);
			//the window key only supports off and on (wierd things can happen when win key 
			//stays pressed)
		/*}else if(windowsState == ToggleState.MID){
			windowsState = ToggleState.ON;
			windowsIndicator.setImageResource(R.drawable.modifier_ind_on_l);
		*/}else{
			windowsState = ToggleState.OFF;
			windowsImage.setImageResource(R.drawable.modifier_windows_off);
			windowsIndicator.setImageResource(R.drawable.modifier_ind_off_l);
		}
	}
	
	public boolean isWindowsKeyPressed(){
		if(windowsState != ToggleState.OFF){
			return true;
		}
		return false;
	}
	
	public void toggleAltKey(){
		if(altState == ToggleState.OFF){
			altState = ToggleState.MID;
			alttImage.setImageResource(R.drawable.modifier_alt_on);
			altIndicator.setImageResource(R.drawable.modifier_ind_mid_c);
		}else if(altState == ToggleState.MID){
			altState = ToggleState.ON;
			altIndicator.setImageResource(R.drawable.modifier_ind_on_c);
		}else{
			altState = ToggleState.OFF;
			alttImage.setImageResource(R.drawable.modifier_alt_off);
			altIndicator.setImageResource(R.drawable.modifier_ind_off_c);
		}
	}
	
	public boolean isAltKeyPressed(){
		if(altState != ToggleState.OFF){
			return true;
		}
		return false;
	}
	
	public void toggleCtrlKey(){
		if(ctrlState == ToggleState.OFF){
			ctrlState = ToggleState.MID;
			ctrlImage.setImageResource(R.drawable.modifier_ctrl_on);
			ctrlIndicator.setImageResource(R.drawable.modifier_ind_mid_r);
		}else if(ctrlState == ToggleState.MID){
			ctrlState = ToggleState.ON;
			ctrlIndicator.setImageResource(R.drawable.modifier_ind_on_r);
		}else{
			ctrlState = ToggleState.OFF;
			ctrlImage.setImageResource(R.drawable.modifier_ctrl_off);
			ctrlIndicator.setImageResource(R.drawable.modifier_ind_off_r);
		}
	}
	
	public boolean isCtrlKeyPressed(){
		if(ctrlState != ToggleState.OFF){
			return true;
		}
		return false;
	}
	
	/**
	 * Called when a key is set. If any of the modifier are in the "mid" state then 
	 * they will be put in the off state
	 */
	public void keyPressed(){
		
		if(windowsState == ToggleState.ON){
			windowsState = ToggleState.OFF;
			windowsImage.setImageResource(R.drawable.modifier_windows_off);
			windowsIndicator.setImageResource(R.drawable.modifier_ind_off_l);
		}
		
		if(altState == ToggleState.MID){
			altState = ToggleState.OFF;
			alttImage.setImageResource(R.drawable.modifier_alt_off);
			altIndicator.setImageResource(R.drawable.modifier_ind_off_c);
		}
		
		if(ctrlState == ToggleState.MID){
			ctrlState = ToggleState.OFF;
			ctrlImage.setImageResource(R.drawable.modifier_ctrl_off);
			ctrlIndicator.setImageResource(R.drawable.modifier_ind_off_r);
		}
		
	}
	
	private enum ToggleState{
		OFF, MID, ON;
	}
}
