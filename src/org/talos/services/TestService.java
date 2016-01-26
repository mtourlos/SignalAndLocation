package org.talos.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service{
	int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    @Override
    public void onCreate() {
        // The service is being created
    	System.out.println("TestService:onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
    	System.out.println("TestService:onStartCommand");
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
    }
}