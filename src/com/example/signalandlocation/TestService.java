package com.example.signalandlocation;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class TestService extends IntentService {

	public TestService() {
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
