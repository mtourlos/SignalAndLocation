package org.talos.services;


import org.talos.activities.MainActivity;

import com.example.signalandlocation.R;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class TestService extends Service implements LocationListener{
	//broadcast strings
	public static final String LOCATION_MESSAGE = "org.talos.services.LocationService.LOCATION_CHANGED";
	public static final String LOCATION_RESULT = "org.talos.services.LocationService.REQUEST_PROCESSED";
	LocalBroadcastManager locationBroadcaster;
	// indicates how to behave if the service is killed
	int mStartMode;
	// interface for clients that bind
    IBinder mBinder;      
    // indicates whether onRebind should be used
    boolean mAllowRebind; 
    
    //locations
    public static float latitude;
    public static float longitude;
    
    //location listener
  	LocationManager locationManager;
  	private String provider;
  	
  	

    public TestService() {
  
	}
    
	@Override
    public void onCreate() {
        // The service is being created
    	System.out.println("TestService:onCreate");
    	
    	locationBroadcaster = LocalBroadcastManager.getInstance(this);
    	
    	checkGPSStatus();
    	
    	//location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
        	latitude = -1;
        	longitude = -1;
        }
    }
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
    	System.out.println("TestService:onStartCommand");
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    notificationBuild();
    	return mStartMode;

    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
    	System.out.println("TestService:bindService");
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
    	System.out.println("TestService:unbindService");
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    	System.out.println("TestService:rebindService");
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    	System.out.println("TestService:onDestroy");
    	
    	locationManager.removeUpdates(this);
    	notificationDestroy();
    }
    
	@Override
	public void onLocationChanged(Location location) {
		latitude = (float) location.getLatitude();
		longitude = (float) location.getLongitude();
		System.out.println("Location changed");
		//Toast.makeText(getApplicationContext(), "Lat:"+latitude+" Lon:"+longtitude, Toast.LENGTH_SHORT).show();
		broadcast();
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		checkGPSStatus();
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		checkGPSStatus();
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		checkGPSStatus();
		
	}
	
	public void broadcast(){
		Intent intent = new Intent(LOCATION_RESULT);
		//intent.putExtra(LOCATION_MESSAGE);
		locationBroadcaster.sendBroadcastSync(intent);
		System.out.println("Message Broadcasted from TestService");
	}
	
	private void checkGPSStatus (){
    	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			Toast.makeText(getApplicationContext(), "GPS provides must be enabled to start service", Toast.LENGTH_SHORT).show();
			stopSelf();
		} 
    }
	
	 /**
     * Builds notification for the SroreLocationService
     */
    
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
    }
    
    /**
     * Destroys notification for the StoreLocationService
     */
    private void notificationDestroy (){
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotificationManager.cancel(0);
    }
}