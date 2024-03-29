package com.btsd.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.btsd.R;

public class ServersListView extends LinearLayout implements OnItemClickListener, OnCreateContextMenuListener{

	public static final String XEBLIX_SERVERS = "XEBLIX_SERVERS";
	public static final String XEBLIX_SERVER_LABEL = "LABEL";
	public static final String XEBLIX_SERVER_ADDRESS = "ADDRESS";
	public static final String XEBLIX_DEFAULT_SERVER = "DEFAULT";
	
	private final List<String> servers = Collections.synchronizedList(new ArrayList<String>());
	private final List<String> addresses = Collections.synchronizedList(new ArrayList<String>()); 
	
	private ListView serversListView = null;
	private String selectedServer = null;
	private CheckBox selectedCheckbox = null;
	private OnServerSelect serverSelectListener;
	
	public ServersListView(final Context context, AttributeSet attribs){
		super(context,attribs);
		
		//this.setOrientation(VERTICAL);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		/*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		editor.remove("XEBLIX_SERVERS");
		editor.putString("XEBLIX_SERVERS", "[{LABEL:Test Server 1, ADDRESS: \"AA:BB:11:22\", DEFAULT:true}," +
				"{LABEL: Test Server 2, ADDRESS: \"11:22:CC:DD\", DEFAULT:false}," +
				"{LABEL: Test Server 3, ADDRESS: \"33:44:55:66\", DEFAULT:false}]");
		editor.commit();*/
		
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.servers_list, this);
		
		serversListView = (ListView)findViewById(R.id.serversListView);
		serversListView.setAdapter(new ServerAdapter());
		serversListView.setOnItemClickListener(this);
		serversListView.setOnCreateContextMenuListener(this);
	}
	
	public void setOnServerSelect(OnServerSelect serverSelectListener){
		this.serverSelectListener = serverSelectListener;
	}
	
	public synchronized void clearSelectedServer(){
		
		selectedServer = null;
		selectedCheckbox.setChecked(false);
		ServerAdapter adapter = (ServerAdapter)serversListView.getAdapter();
		adapter.notifyDataSetChanged();
	}
	
	public synchronized void saveSelectedServer(String name, String address){
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String serversString = prefs.getString(XEBLIX_SERVERS, "[]");
		Editor editor = prefs.edit();
		editor.remove("XEBLIX_SERVERS");
		boolean newServer = true;
		try{
			JSONArray jsonServers = new JSONArray(serversString);
			for(int i=0; i < jsonServers.length(); i++){
				
				JSONObject jsonServer = jsonServers.getJSONObject(i);
				
				//the selectedServer is the new default so clear previous
				if(jsonServer.getBoolean(XEBLIX_DEFAULT_SERVER)){
					jsonServer.put(XEBLIX_DEFAULT_SERVER, false);
				}
				
				String tempAddress = jsonServer.getString(XEBLIX_SERVER_ADDRESS);
				if(address.equalsIgnoreCase(tempAddress)){
					//server already known, mark it as default
					newServer = false;
					jsonServer.put(XEBLIX_DEFAULT_SERVER, true);
				}
			}
			
			if(newServer){
				
				JSONObject newJSONServer = new JSONObject();
				newJSONServer.put(XEBLIX_SERVER_LABEL, name);
				newJSONServer.put(XEBLIX_SERVER_ADDRESS, address);
				newJSONServer.put(XEBLIX_DEFAULT_SERVER, true);
				
				jsonServers.put(newJSONServer);
			}
			
			editor.putString("XEBLIX_SERVERS",jsonServers.toString());
			editor.commit();
		}catch(JSONException ex){
			throw new RuntimeException("Failed to parser serverStrings value: " + 
				serversString + " " + ex.getMessage());
		}
		
	}
	
	public synchronized void clearDiscoveredServers(boolean fireServerSelect){
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String serversString = prefs.getString(XEBLIX_SERVERS, "[]");
		String selectedServerName = null;
		try{
			JSONArray jsonServers = new JSONArray(serversString);
			servers.clear();
			addresses.clear();
			for(int i=0; i < jsonServers.length(); i++){
				
				JSONObject jsonServer = jsonServers.getJSONObject(i);
				
				String tempName = jsonServer.getString(XEBLIX_SERVER_LABEL);
				servers.add(tempName);
				String tempAddress = jsonServer.getString(XEBLIX_SERVER_ADDRESS);
				addresses.add(tempAddress);
				if(jsonServer.getBoolean(XEBLIX_DEFAULT_SERVER)){
					selectedServer = tempAddress;
					selectedServerName = tempName;  
				}
				
			}
		}catch(JSONException ex){
			throw new RuntimeException("Failed to parser serverStrings value: " + 
				serversString + " " + ex.getMessage());
		}
		ServerAdapter adapter = (ServerAdapter)serversListView.getAdapter();
		adapter.notifyDataSetChanged();
		
		if(fireServerSelect && serverSelectListener != null && selectedServer != null){
			serverSelectListener.onServerSelect(selectedServerName, 
					selectedServer);
		}
	}
	
	public synchronized void addDiscoveredServer(String name, String address){
		
		//only add to list if new
		if(!addresses.contains(address)){
		
			servers.add(name);
			addresses.add(address);
			
			ServerAdapter adapter = (ServerAdapter)serversListView.getAdapter();
			adapter.notifyDataSetChanged();
		}
		
	}
	
	public synchronized void removedSavedServer(int position){
		
		if(position < 0 || position > addresses.size()){
			Log.w(getClass().getSimpleName(), "Invalid position: " + position + 
					". Valid range is 0 to "+ addresses.size() );
			return;
		}
		String address = addresses.get(position);
		
		if(address == null){
			Log.w(getClass().getSimpleName(), "Failed to find saved server with position: " + position);
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String serversString = prefs.getString(XEBLIX_SERVERS, "[]");
		Editor editor = prefs.edit();
		editor.remove("XEBLIX_SERVERS");
		
		try{
			JSONArray jsonServers = new JSONArray(serversString);
			JSONArray newJSONServers = new JSONArray();
			for(int i=0; i < jsonServers.length(); i++){
				
				JSONObject jsonServer = jsonServers.getJSONObject(i);
				
				String tempAddress = jsonServer.getString(XEBLIX_SERVER_ADDRESS);
				if(!address.equalsIgnoreCase(tempAddress)){
					newJSONServers.put(jsonServer);
				}
			}
		
			editor.putString("XEBLIX_SERVERS",newJSONServers.toString());
		
		}catch(JSONException ex){
			throw new RuntimeException(ex.getMessage());
		}
		
		editor.commit();
		
		addresses.remove(position);
		servers.remove(position);
		ServerAdapter adapter = (ServerAdapter)serversListView.getAdapter();
		adapter.notifyDataSetChanged();
	}
	
	private Context getServersListViewContext(){
		return getContext();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		Log.e(getClass().getSimpleName(), "onListItemClick");
		
		String tempSelected = addresses.get(position);
		if(!tempSelected.equalsIgnoreCase(selectedServer)){
			if(selectedCheckbox != null){
				selectedCheckbox.setChecked(false);
			}
			selectedServer = tempSelected;	
			selectedCheckbox = (CheckBox)arg1.findViewById(R.id.selected);
			selectedCheckbox.setChecked(true);
			
			if(serverSelectListener != null){
				serverSelectListener.onServerSelect(servers.get(position), 
					selectedServer);
			}
			
		}else{
			//the selected item was selected again, so uncheck it
			selectedServer = null;
			selectedCheckbox.setChecked(false);
			selectedCheckbox = null;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "bad menuInfo", e);
            return;
        }
		
        String name = this.servers.get(info.position);
        String address = this.servers.get(info.position);
        
        if(name != null){
        	menu.setHeaderTitle(name);
        }else{
        	menu.setHeaderTitle(address);
        }
        menu.add(0, info.position, 0, R.string.REMOVE_XEBLIX_SERVER);
	}
	
	public int getSelectedListItemPosition(){
		
		return this.serversListView.getSelectedItemPosition();
		
	}
	
	private final class ServerAdapter extends ArrayAdapter{
		public ServerAdapter() {
			super(getServersListViewContext(), R.layout.server_row, 
				servers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View row = convertView;
			
			if(row == null){
				LayoutInflater inflater = (LayoutInflater)getServersListViewContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.server_row, parent, false) ;
			}
			
			
			TextView serverName = (TextView)row.findViewById(R.id.server_name);
			TextView btAddress = (TextView)row.findViewById(R.id.btaddress);
			CheckBox selected = (CheckBox)row.findViewById(R.id.selected);
			
			serverName.setText(servers.get(position));
			btAddress.setText(addresses.get(position));
			
			if(addresses.get(position).equalsIgnoreCase(selectedServer)){
				selected.setChecked(true);
				selectedCheckbox = selected;
			}else{
				selected.setChecked(false);
			}
			
			return row;
		}
		
		
	}
	
	public interface OnServerSelect{
		
		/**
		 * Called when a user checks a server
		 */
		public void onServerSelect(String name, String address);
	}
	
}
