package org.talos.operations;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsHolder {
	String serverIp;
	String user;
	Context context;
	
	public static final String KEY_PREF_ACTIVE_USER = "settings_active_user";
	public static final String KEY_PREF_SERVER_IP = "settings_server_ip";
	
	 
	
	public SettingsHolder(Context context) {
		super();
		this.context = context;
		loadPrefs();
	}

	void loadPrefs(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		serverIp = sharedPrefs.getString(KEY_PREF_SERVER_IP, "");
		serverIp = sharedPrefs.getString(KEY_PREF_SERVER_IP, "");
		
	}
	
	public String getUser(){
		return user;
	}
	
	public String getServerIp(){
		return serverIp;
	}
}
