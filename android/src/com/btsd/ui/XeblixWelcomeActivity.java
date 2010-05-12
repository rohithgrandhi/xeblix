package com.btsd.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.btsd.AbstractRemoteActivity;
import com.btsd.BTSDApplication;
import com.btsd.BTScrewDriverAlert;
import com.btsd.Main;
import com.btsd.R;
import com.btsd.ServerMessages;
import com.btsd.BTScrewDriverStateMachine.States;
import com.btsd.bluetooth.BluetoothAccessor;
import com.btsd.bluetooth.BluetoothAdapter;
import com.btsd.bluetooth.BluetoothDevice;
import com.btsd.util.MessagesEnum;
import com.btsd.view.ServerSearchView;
import com.btsd.view.ServersListView;
import com.btsd.view.ServerSearchView.OnServerSearchClick;
import com.btsd.view.ServersListView.OnServerSelect;

public class XeblixWelcomeActivity extends AbstractRemoteActivity implements OnServerSearchClick, OnServerSelect{

	private BluetoothAdapter adapter;
	
	private ServersListView serversListView;
	private AlertDialog searchingDialog;
	private AlertDialog connectingDialog;
	private String selectedServerName;
	private String selectedServerAddress;
	private boolean sentVersionRequest = false;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.xeblix_welcome);
		
		adapter = BluetoothAccessor.getInstance().getDefaultAdapter();
		
		ServerSearchView serverSearch = (ServerSearchView)findViewById(R.id.server_search);
		serverSearch.setOnClickServerSearch(this);
		
		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(backport.android.bluetooth.BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(backport.android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
        this.serversListView = (ServersListView)findViewById(R.id.servers_list);
        this.serversListView.setOnServerSelect(this);
        
        this.searchingDialog = new AlertDialog.Builder(this).setTitle(R.string.WELCOME_SEARCHING_TITLE).
        	setMessage(R.string.WELCOME_SEARCHING).
        	setNegativeButton(android.R.string.cancel, this).create();
        
        this.connectingDialog = new AlertDialog.Builder(this).
        	setTitle(R.string.CONNECTING_TO_BT_SERVER_TITLE).
        	setNegativeButton(android.R.string.cancel, this).create();
	}

	@Override
	public void onServerSearchClick() {
		
		Log.i(getClass().getSimpleName(), "Starting discovery");
		
		this.searchingDialog.show();
		
		serversListView.clearDiscoveredServers();
		adapter.startDiscovery();
		
	}

	@Override
	public void onServerSelect(String name, String address) {
		
		this.selectedServerName = name;
		this.selectedServerAddress = address;
		String prefix = getResources().getString(R.string.CONNECTING_TO_BT_SERVER_PREFIX);
		this.connectingDialog.setMessage(prefix + " " +name);
		this.connectingDialog.show();
		
		getBTSDApplication().getStateMachine().connectToServer(name, address);
	}
	
	@Override
	protected boolean onServerConnectionStateChange(States state) {
		
		if(state == States.CONNECTION_FAILED || 
			(state == States.DISCONNECTED && sentVersionRequest)){
			if(this.connectingDialog.isShowing()){
				String failedMessage = getResources().getString(R.string.CONNECTION_FAILED);
				this.connectingDialog.setMessage(failedMessage + " " + selectedServerName);
			}
			
			//connection failed so uncheck the selected server
			this.serversListView.clearSelectedServer();
			sentVersionRequest = false;
			return true;
		}else if(state == States.CONNECTED){
			
			//ok we are connected, lets make sure we got the right version
			sentVersionRequest = true;
    		BTSDApplication application =  (BTSDApplication)getApplication();
    		application.getStateMachine().messageToServer(ServerMessages.getVersionRequest());
    		return true;
		}else if(state == States.DISCONNECTED && !sentVersionRequest){
			//during connection we disconnect from any existing server connection. If we get
			//the disconnected state change and we have not sent a version request then this
			//state change is expected and can be ignored
			return true;
		}else{
			Log.w(getClass().getSimpleName(), "Received unexpected state change notification. " +
				"State change: " + state.getName());
			return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(adapter != null){
			adapter.cancelDiscovery();
		}
		
		// Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void onRemoteMessage(MessagesEnum messagesEnum, Object message) {
		
		if(messagesEnum == MessagesEnum.MESSAGE_FROM_SERVER){
			JSONObject response = (JSONObject)message;
    		try{
	    		if(response.getString(Main.TYPE).equalsIgnoreCase("VersionRequest")){
	    			if(!response.getString(Main.VALUE).equalsIgnoreCase("1.0")){
	    				String failedMessage = getResources().getString(R.string.SERVER_VERSION_MISMATCH);
	    				this.connectingDialog.setMessage(failedMessage);
	    			}
	    			
	    			//connected and have the correct version, make sure the selected server
	    			//is saved and go to the Root remote screen 
	    			this.serversListView.saveSelectedServer(selectedServerName, selectedServerAddress);
	    			
	    			
	    			this.connectingDialog.hide();
	    			Log.i(getClass().getSimpleName(), "Connected to server: " + selectedServerName + 
	    				" address: " + selectedServerAddress);
	    			Intent intent = new Intent("com.btsd.RootActivity");
	    			startActivity(intent);
	    			
	    		}else{
	    			Log.w(getClass().getSimpleName(), "Unexpected message from server: " + message.toString());
	    		}
    		}catch(JSONException ex){}
		}
		
	}

	@Override
	public void onClick(View v) {
		
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if(dialog == this.searchingDialog){
			if(which == DialogInterface.BUTTON2){
				Log.e(getClass().getSimpleName(), "User has cancled the Server Search");
				if(adapter != null){
					adapter.cancelDiscovery();
				}
			}
		}else if(dialog == this.connectingDialog){
			this.serversListView.clearSelectedServer();
			getBTSDApplication().getStateMachine().serverDisconnect();
		}
		
	}

	@Override
	public void refreshConfiguredRemotes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnToPreviousRemoteConfiguration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectConfiguredRemote(String name) {
		// TODO Auto-generated method stub
		
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			if (backport.android.bluetooth.BluetoothDevice.
				ACTION_FOUND.equals(action)) {
				
				BluetoothDevice device = BluetoothAccessor.getInstance().getBluetoothDeviceFromIntent(intent);
				
				serversListView.addDiscoveredServer(device.getName(), device.getAddress());
				
			} else if (backport.android.bluetooth.BluetoothAdapter.
				ACTION_DISCOVERY_FINISHED.equals(action)) {
				
				Log.i(getClass().getSimpleName(), "Discovery finished");
				searchingDialog.hide();
			}
			
		}
	};
	 
}
