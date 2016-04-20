package org.talos.services;

import java.util.concurrent.ExecutionException;

import org.talos.operations.UploadData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
 
public class NetworkChangeReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(final Context context, final Intent intent) {
 
        String status = getConnectivityStatusString(context);
 
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
    
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
            	UploadData up = new UploadData(context);
            	try {
					up.uploadData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            		
                return 1;
            }
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 2;
        } 
        return 3;
    }
    
    
    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == 1) {
            status = "Wifi enabled";
        } else if (conn == 2) {
            status = "Mobile data enabled";
        } else if (conn == 3) {
            status = "Not connected to Internet";
        }
        return status;
    }
}
