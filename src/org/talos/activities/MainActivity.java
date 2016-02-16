package org.talos.activities;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

import org.talos.db.DataDbHelper;
import org.talos.db.DataContract.DataEntry;
import org.talos.services.IntentTestService;
import org.talos.services.TestService;
import org.talos.services.WebServiceTask;

import com.example.signalandlocation.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	public static final String KEY_PREF_ACTIVE_USER = "settings_active_user";
	public static final String KEY_PREF_SERVER_IP="settings_server_ip";
	
//	//signal listeners
//	TelephonyManager        Tel;
//	MyPhoneStateListener    SignalStrengthListener;
	
	//location listener
	//LocationManager			locationManager;
	//private String 			provider;
	
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
	
	//broadcastReceiver
	BroadcastReceiver locationBrReceiver;
	
	int signalStrength;
	String operatorName;
	String networkTypeString;
	String response;
	String timestamp;
	float lat;
	float lon;
	
	//dbHelper
	DataDbHelper mDbHelper;
	
	//test
	TestService ts = new TestService();
	
	
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);

		//load settings
		loadSettings();
		
		//dbHelper
		mDbHelper = new DataDbHelper(getBaseContext());
				
		setContentView(R.layout.activity_main);
		latituteField = (TextView) findViewById(R.id.lat);
	    longitudeField = (TextView) findViewById(R.id.lon);
	    operatorNameField = (TextView) findViewById(R.id.operator_name);
	    timestampField = (TextView) findViewById(R.id.timestamp);
	    serverIpField = (TextView) findViewById(R.id.server_ip);
	    activeUserField = (TextView) findViewById(R.id.active_user);
	    networkTypeField = (TextView) findViewById(R.id.network_type);
	    signalStrengthField = (TextView) findViewById(R.id.signal_out);
	    
		//signal strength 
//		SignalStrengthListener = new MyPhoneStateListener();
//		Tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        Tel.listen(SignalStrengthListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        
        //operator name
//        operatorName = Tel.getNetworkOperatorName();
//        operatorNameField.setText("Operator Name: " + operatorName);
        
        //network type
//        networkTypeField.setText("Network Type: " + getNetworkType());
        
        
        //location
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);
        //Location location = locationManager.getLastKnownLocation(provider);
        //if (location != null) {
        //    System.out.println("Provider " + provider + " has been selected.");
        //    onLocationChanged(location);
        //} else {
        //    latituteField.setText("Latitute: Location not available");
        //    longitudeField.setText("Longtitude: Location not available");
        //}
        
        locationBrReceiver = new BroadcastReceiver() {
            
        	@Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(TestService.LOCATION_MESSAGE);
                System.out.println("Broadcasted message cought form MainActivity");
                //lat = TestService.latitude;
                //lng = TestService.longtitude;
                //Toast.makeText(getApplicationContext(), "Lat:"+ts.getLatitude()+" Lon:"+ts.getLongtitude(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Lat:"+lat+" Lon:"+lng, Toast.LENGTH_SHORT).show();
                lat = TestService.latitude;
                lon = TestService.longitude;
                signalStrength = TestService.signalStrength;
                networkType = TestService.networkType;
                operatorName = TestService.operatorName;
                updateLocationUI(lon, lat);
                updateOperatorDetailsUI(signalStrength,networkType,operatorName);
                System.out.println(signalStrength+networkType+operatorName);
                
                
                
                
            }
        };
    	
	}
	
	@Override
	protected void onStart(){
		super.onStart();
	    LocalBroadcastManager.getInstance(this).registerReceiver((locationBrReceiver), 
	        new IntentFilter(TestService.LOCATION_RESULT)
	    );
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
	      //Tel.listen(SignalStrengthListener, PhoneStateListener.LISTEN_NONE);
	      //locationManager.removeUpdates(this);
	   }

	
	
	
	@Override
	protected void onResume(){
	      super.onResume();
	      checkGPSStatus();
	      //Tel.listen(SignalStrengthListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	      //locationManager.requestLocationUpdates(provider, 400, 1, this);
	      
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
	
	@Override
	protected void onStop(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBrReceiver);
	    super.onStop();
		
	}
	
	
//	private class MyPhoneStateListener extends PhoneStateListener{
//		/* Get the Signal strength from the provider, each time there is an update */
//		@Override
//		public void onSignalStrengthsChanged(SignalStrength signalStrengths) 
//		{
//			super.onSignalStrengthsChanged(signalStrengths);
//			//Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = " + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
//			signalStrength = signalStrengths.getGsmSignalStrength();  
//			signalStrengthField = (TextView) findViewById(R.id.signal_out);
//			signalStrengthField.setText("GSM CINR= " + signalStrength );
//		}
//		
//		
//		
//	};/* End of private Class */
    
    
    /*public void onLocationChanged(Location location) {
        lat = (float) (location.getLatitude());
        lng = (float) (location.getLongitude());
        latituteField.setText("Latitude: "+String.valueOf(lat));
        longitudeField.setText("Longtitude: "+String.valueOf(lng));
        latituteFieldService.setText("Latitude: "+ts.getLatitude());
        longitudeFieldService.setText("Longtitude: "+ts.getLongtitude());
        
      
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
    }*/
    
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
    
    /**
     * Translates network id to string for ui
     * @return network type to string
     */
//    public String getNetworkType(){
//    	int networkType;
//    	String result=null;
//    	networkType = Tel.getNetworkType();
//    	switch (networkType){
//    	case 7:
//    	    result = "1xRTT";
//    	    break;      
//    	case 4:
//    	    result = "CDMA";
//    	    break;      
//    	case 2:
//    	    result = "EDGE";
//    	    break;  
//    	case 14:
//    		result = "eHRPD";
//    	    break;      
//    	case 5:
//    		result = "EVDO rev. 0";
//    	    break;  
//    	case 6:
//    		result = "EVDO rev. A";
//    	    break;  
//    	case 12:
//    		result = "EVDO rev. B";
//    	    break;  
//    	case 1:
//    		result = "GPRS";
//    	    break;      
//    	case 8:
//    		result = "HSDPA";
//    	    break;      
//    	case 10:
//    		result = "HSPA";
//    	    break;          
//    	case 15:
//    		result = "HSPA+";
//    	    break;          
//    	case 9:
//    		result = "HSUPA";
//    	    break;          
//    	case 11:
//    		result = "iDen";
//    	    break;
//    	case 13:
//    		result = "LTE";
//    	    break;
//    	case 3:
//    		result = "UMTS";
//    	    break;          
//    	case 0:
//    		result = "Unknown";
//    	    break;
//    	}
//    	
//    	return result;
//    }
    
    /**
     * Loads users settings from settings.xml
     * @return true in order to triggers changes and display them
     */
    public boolean loadSettings(){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	serverIp=sharedPrefs.getString(MainActivity.KEY_PREF_SERVER_IP, "");
    	activeUser=sharedPrefs.getString(MainActivity.KEY_PREF_ACTIVE_USER, "");
    	return true;

    }
    
    /**
     * Sends single Data to Server
     * @param v
     */
    public void sData (View v){
    	
    	
    	
    	WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK,this,response);
    	//Location location = locationManager.getLastKnownLocation(provider);
    	String timeStamp = getCurrentTimeStamp();
    	String user = activeUser;
    	String operator = operatorName;
    	int cinr = signalStrength;
    	wst.addNameValuePair("timestamp", timeStamp);
    	wst.addNameValuePair("user", user);
    	wst.addNameValuePair("operator", operator);
    	wst.addNameValuePair("cinr",Integer.toString(cinr));
    	wst.addNameValuePair("latitude",Float.toString(lat));
    	wst.addNameValuePair("longtitude",Float.toString(lon));
    	
    	wst.execute("http://" + serverIp + ":8080/TalosServer/service/userservice/datas");
    	
    }
    
    /**
     * Starts the StoreLocationService
     * @param v
     */
    
    public void startService (View v){
    	//notificationBuild();
    	Intent intent = new Intent(this, TestService.class);
    	startService(intent);
    }
    
    /**
     * Stops the StoreLocationService
     * @param v
     */
    public void stopService (View v){
    	//notificationDestroy();
    	Intent intent = new Intent(this, TestService.class);
    	stopService(intent);
    }
    
    /**
     * Checks whether GPS provides is enabled and if is not prompts an AlertDialog
     */
    private void checkGPSStatus(){
    	LocationManager lmService = (LocationManager) getSystemService(LOCATION_SERVICE);
    	boolean enabled = lmService.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.main_alerDialog_message)
				   .setTitle(R.string.main_alerDialog_title);
			builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        	   startActivity(intent);
		           }
		       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   System.exit(0);
		           }
		       });
			AlertDialog dialog = builder.create();
			dialog.show();
		} 
    }
    
    /**
     * Builds notification for the SroreLocationService
     */
    /*
    private void notificationBuild (){
    	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    	mBuilder.setSmallIcon(R.drawable.ic_launcher);
    	mBuilder.setContentTitle("Talos");
    	mBuilder.setContentText("Talos service is now up and running!");
    	mBuilder.setOngoing(true);
    	
    	Intent resultIntent = new Intent(this, MainActivity.class);
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    	stackBuilder.addParentStack(MainActivity.class);

    	// Adds the Intent that starts the Activity to the top of the stack
    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
    	mBuilder.setContentIntent(resultPendingIntent);
    	
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
    	int notificationID=0;
		// notificationID allows you to update the notification later on.
    	mNotificationManager.notify(notificationID, mBuilder.build());
    	
    	try {
    	    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    	    r.play();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	
    	
    	
    }*/
    
    /**
     * Destroys notification for the StoreLocationService
     */
    /**
    private void notificationDestroy (){
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotificationManager.cancel(0);
    }*/
    
    /**
     * Stores pilot Data for test
     * @param v
     */
    public void storePilotData(View v){
    	SQLiteDatabase db =  mDbHelper.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(DataEntry.TIME_STAMP, getCurrentTimeStamp());
    	values.put(DataEntry.USER, activeUser);
    	values.put(DataEntry.OPERATOR, operatorName);
    	values.put(DataEntry.CINR, signalStrength );
    	values.put(DataEntry.NETWORK_TYPE, networkType);
    	values.put(DataEntry.LATITUDE, String.valueOf(lat));
    	values.put(DataEntry.LATITUDE, String.valueOf(lon));
    	long newRowId;
    	newRowId = db.insert(DataEntry.TABLE_NAME, null, values);
    	Toast.makeText(getApplicationContext(), "Data Stored in local db", Toast.LENGTH_SHORT).show();
    }
   
    /**
     * Displays some data from local db
     * @param v
     */
    public void loadLocalDb(View v){
    	SQLiteDatabase db = mDbHelper.getReadableDatabase();
    	String[] projection = {
    		    DataEntry.TIME_STAMP,
    		    DataEntry.USER,
    		    DataEntry.CINR
    		    };
    	String sortOrder =
    		    DataEntry.TIME_STAMP + " DESC";
    	
    	Cursor cursor = db.query(
    		    DataEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    null,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );
    	
    	cursor.moveToFirst();
    	String itemTimeStamp = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.TIME_STAMP));
    	String itemUser = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.USER));
    	String itemCINR = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.CINR));
    	Toast.makeText(getApplicationContext(), itemTimeStamp, Toast.LENGTH_SHORT).show();
    	cursor.moveToLast();
    	itemTimeStamp = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.TIME_STAMP));
    	itemUser = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.USER));
    	itemCINR = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.CINR));
    	Toast.makeText(getApplicationContext(), itemTimeStamp, Toast.LENGTH_SHORT).show();
    }
    
    private void updateLocationUI(float lon , float lat){
    	latituteField.setText("Latitute: "+lat);
    	longitudeField.setText("Longitude: "+lon);
    	
    }
    
    private void updateOperatorDetailsUI(int signalStrength, String networkType, String operatorName){
    	signalStrengthField.setText("Signal strength: " + signalStrength);
    	networkTypeField.setText("Network type: " + networkType);
    	operatorNameField.setText("Operator name: " + operatorName);
    }
    
}
