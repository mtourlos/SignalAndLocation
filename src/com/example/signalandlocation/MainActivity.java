package com.example.signalandlocation;
//comment from macbook
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	
	public static final String KEY_PREF_ACTIVE_USER = "settings_active_user";
	public static final String KEY_PREF_SERVER_IP="settings_server_ip";
	
	//signal listeners
	TelephonyManager        Tel;
	MyPhoneStateListener    SignalStrengthListener;
	
	//location listener
	LocationManager			locationManager;
	private String 			provider;
	
	//network type
	private String networkType;
	
	
	//textViewers
	private TextView serverIpField;
	private TextView activeUserField;
	private TextView timestampField;
	private TextView latituteField;
	private TextView longitudeField;
	private TextView signalStrengthField;
	private TextView operatorNameField;
	private TextView networkTypeField;
	
	//preferences
	String activeUser;
	String serverIp;
	
	int signalStrength;
	String operatorName;
	String networkTypeString;
	String response;
	String timestamp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);

		//load settings
		loadSettings();
		//check if the gps provider is enabled
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			Toast.makeText(this, "GPS must be enabled!!!",Toast.LENGTH_SHORT).show();
		} 
		
		setContentView(R.layout.activity_main);
		latituteField = (TextView) findViewById(R.id.lat);
	    longitudeField = (TextView) findViewById(R.id.lon);
	    operatorNameField = (TextView) findViewById(R.id.operator_name);
	    timestampField = (TextView) findViewById(R.id.timestamp);
	    serverIpField = (TextView) findViewById(R.id.server_ip);
	    activeUserField = (TextView) findViewById(R.id.active_user);
	    networkTypeField = (TextView) findViewById(R.id.network_type);
	    
		//signal strength 
		SignalStrengthListener = new MyPhoneStateListener();
		Tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(SignalStrengthListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        
        //operator name
        operatorName = Tel.getNetworkOperatorName();
        operatorNameField.setText("Operator Name: " + operatorName);
        
        //network type
        networkTypeField.setText("Network Type: " + getNetworkType());
        
        //location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
          } else {
            latituteField.setText("Latitute: Location not available");
            longitudeField.setText("Longtitude: Location not available");
          }
    	
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if (id == R.id.action_settings){
			Intent i = new Intent(this,SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	
	
	@Override
	   protected void onPause(){
	      super.onPause();
	      Tel.listen(SignalStrengthListener, PhoneStateListener.LISTEN_NONE);
	      locationManager.removeUpdates(this);
	   }

	
	
	
	@Override
	protected void onResume(){
	      super.onResume();
	      LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	      boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!enabled) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				Toast.makeText(this, "GPS must be enabled!!!",Toast.LENGTH_SHORT).show();
			} 
	      Tel.listen(SignalStrengthListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	      locationManager.requestLocationUpdates(provider, 400, 1, this);
	      
	    //timestamp
    	  timestamp= getCurrentTimeStamp();
    	  timestampField.setText("Timestamp: "+timestamp);
	      

    	//load settings
  		if (loadSettings()){
  			//username and active user textview
  		    activeUserField.setText("Active User: " + activeUser);
  		    serverIpField.setText("Server Ip: " + serverIp);
  		}
	      
	   }
	
	
	private class MyPhoneStateListener extends PhoneStateListener{
		/* Get the Signal strength from the provider, each time there is an update */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrengths) 
		{
			super.onSignalStrengthsChanged(signalStrengths);
			//Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = " + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
			signalStrength = signalStrengths.getGsmSignalStrength();  
			signalStrengthField = (TextView) findViewById(R.id.signal_out);
			signalStrengthField.setText("GSM CINR= " + signalStrength );
		}
		
		
		
	};/* End of private Class */
    
    
    public void onLocationChanged(Location location) {
        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());
        latituteField.setText("Latitude: "+String.valueOf(lat));
        longitudeField.setText("Longtitude: "+String.valueOf(lng));
      
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public void onProviderEnabled(String provider) {
    	Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
    	
    }

    @Override
    public void onProviderDisabled(String provider) {
    	Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
    }
    
    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    
    public String getNetworkType(){
    	int networkType;
    	String result=null;
    	networkType = Tel.getNetworkType();
    	switch (networkType){
    	case 7:
    	    result = "1xRTT";
    	    break;      
    	case 4:
    	    result = "CDMA";
    	    break;      
    	case 2:
    	    result = "EDGE";
    	    break;  
    	case 14:
    		result = "eHRPD";
    	    break;      
    	case 5:
    		result = "EVDO rev. 0";
    	    break;  
    	case 6:
    		result = "EVDO rev. A";
    	    break;  
    	case 12:
    		result = "EVDO rev. B";
    	    break;  
    	case 1:
    		result = "GPRS";
    	    break;      
    	case 8:
    		result = "HSDPA";
    	    break;      
    	case 10:
    		result = "HSPA";
    	    break;          
    	case 15:
    		result = "HSPA+";
    	    break;          
    	case 9:
    		result = "HSUPA";
    	    break;          
    	case 11:
    		result = "iDen";
    	    break;
    	case 13:
    		result = "LTE";
    	    break;
    	case 3:
    		result = "UMTS";
    	    break;          
    	case 0:
    		result = "Unknown";
    	    break;
    	}
    	
    	return result;
    }
    
    public boolean loadSettings(){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	serverIp=sharedPrefs.getString(MainActivity.KEY_PREF_SERVER_IP, "");
    	activeUser=sharedPrefs.getString(MainActivity.KEY_PREF_ACTIVE_USER, "");
    	return true;

    }
    
    public void sData (View v){
    	WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK,this,response);
    	Location location = locationManager.getLastKnownLocation(provider);
    	String timeStamp = getCurrentTimeStamp();
    	String user = activeUser;
    	String operator = operatorName;
    	int cinr = signalStrength;
    	float lat = (float) location.getLatitude();
    	float lon = (float) location.getLongitude();
    	wst.addNameValuePair("timestamp", timeStamp);
    	wst.addNameValuePair("user", user);
    	wst.addNameValuePair("operator", operator);
    	wst.addNameValuePair("cinr",Integer.toString(cinr));
    	wst.addNameValuePair("latitude",Float.toString(lat));
    	wst.addNameValuePair("longtitude",Float.toString(lon));
    	
    	wst.execute("http://" + serverIp + ":8080/TalosServer/service/userservice/datas");
    	
    }
    

   
}
