package org.talos.services;

import com.example.signalandlocation.R;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class IntentTestService extends IntentService{
	
		public IntentTestService() {
		super("TestService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		
	}
	
	@Override
	public void onDestroy(){
		Toast.makeText(this, "Test Service Stoped!",Toast.LENGTH_SHORT).show();
	}

	

}
