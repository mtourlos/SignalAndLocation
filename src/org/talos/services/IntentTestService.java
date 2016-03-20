package org.talos.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class IntentTestService extends IntentService{
	
		public IntentTestService() {
		super("TestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
	}
	
	@Override
	public void onDestroy(){
		Toast.makeText(this, "Test Service Stoped!",Toast.LENGTH_SHORT).show();
	}

	

}
