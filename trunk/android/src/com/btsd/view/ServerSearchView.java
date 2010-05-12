package com.btsd.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.btsd.R;

public class ServerSearchView extends LinearLayout implements OnClickListener{

	private Button serverSearch;
	private OnServerSearchClick serverSearchListener;
	
	public ServerSearchView(final Context context, AttributeSet attribs){
		super(context,attribs);
		
		this.setOrientation(VERTICAL);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.server_search_buttons, this);
		
		serverSearch = (Button)findViewById(R.id.serverSearch);
		serverSearch.setOnClickListener(this);
	}
	
	public void setOnClickServerSearch(OnServerSearchClick onServerSearchClick){
		this.serverSearchListener = onServerSearchClick;
	}
	
	@Override
	public void onClick(View v) {
		if(serverSearchListener != null){
			serverSearchListener.onServerSearchClick();
		}
	}
	
	public interface OnServerSearchClick{
		
		/**
		 * Called when a user presses the ServerSearch button
		 */
		public void onServerSearchClick();
	}
}
