package org.talos.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.talos.beans.DataBean;
import org.talos.db.DataContract.DataEntry;
import org.talos.db.DataDbHelper;
import org.talos.db.DataDbOperations;
import org.talos.operations.UploadData;
import org.talos.services.TalosService;
import org.talos.services.TalosService.TalosBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.signalandlocation.R;

public class MainActivity extends Activity {

	public static final String KEY_PREF_ACTIVE_USER = "settings_active_user";
	public static final String KEY_PREF_SERVER_IP = "settings_server_ip";

	// network type
	private String networkType;

	// textViewers
	private TextView serverIpField;
	private TextView activeUserField;
	private TextView timestampField;
	private TextView latituteField;
	private TextView longitudeField;
	private TextView signalStrengthField;
	private TextView operatorNameField;
	private TextView networkTypeField;

	// preferences
	String activeUser;
	String serverIp;

	// broadcastReceiver
	BroadcastReceiver locationBrReceiver;

	int signalStrength;
	String operatorName;
	String networkTypeString;
	String response;
	String timestamp;
	float lat;
	float lon;

	// dbHelper
	DataDbHelper mDbHelper;
	
	TalosService mService;
    boolean mBound = false;
    ServiceConnection serviceConnection;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// load settings
		loadSettings();

		// dbHelper
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

		locationBrReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String s = intent.getStringExtra(TalosService.LOCATION_MESSAGE);
				lat = TalosService.latitude;
				lon = TalosService.longitude;
				signalStrength = TalosService.signalStrength;
				networkType = TalosService.networkType;
				operatorName = TalosService.operatorName;
				updateLocationUI(lon, lat);
				updateOperatorDetailsUI(signalStrength, networkType,
						operatorName);

			}
		};

	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(
				(locationBrReceiver),
				new IntentFilter(TalosService.LOCATION_RESULT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Tel.listen(SignalStrengthListener, PhoneStateListener.LISTEN_NONE);
		// locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkGPSStatus();

		// timestamp
		timestamp = getCurrentTimeStamp();
		timestampField.setText("Timestamp: " + timestamp);

		// load settings
		if (loadSettings()) {
			// username and active user textview
			activeUserField.setText("Active User: " + activeUser);
			serverIpField.setText("Server Ip: " + serverIp);
		}

	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				locationBrReceiver);
		super.onStop();

	}

	public static String getCurrentTimeStamp() {
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String currentTimeStamp = dateFormat.format(new Date()); // Find
																		// todays
																		// date

			return currentTimeStamp;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Loads users settings from settings.xml
	 * 
	 * @return true in order to triggers changes and display them
	 */
	public boolean loadSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		serverIp = sharedPrefs.getString(MainActivity.KEY_PREF_SERVER_IP, "");
		activeUser = sharedPrefs.getString(MainActivity.KEY_PREF_ACTIVE_USER,
				"");
		return true;

	}

	/**
	 * Sends single Data to Server
	 * 
	 * @param v
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void sData(View v) throws InterruptedException, ExecutionException {		
		UploadData upOp = new UploadData(getApplicationContext());
		upOp.uploadData();
	}

	/**
	 * Starts the StoreLocationService
	 * 
	 * @param v
	 */

	public void startService(View v) {
		Intent serviceIntent = new Intent(this, TalosService.class);
		startService(serviceIntent);
//		serviceConnection = getServiceConnection();
//		bindService(serviceIntent, serviceConnection, Context.BIND_DEBUG_UNBIND);
	}

	/**
	 * Stops the StoreLocationService
	 * 
	 * @param v
	 */
	public void stopService(View v) {
		Intent serviceIntent = new Intent(this, TalosService.class);
		stopService(serviceIntent);
	}

	/**
	 * Checks whether GPS provides is enabled and if is not prompts an
	 * AlertDialog
	 */
	private void checkGPSStatus() {
		LocationManager lmService = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = lmService.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.main_alerDialog_message).setTitle(R.string.main_alerDialog_title);
			builder.setPositiveButton(R.string.enable,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});
			builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							System.exit(0);
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	public void storePilotData(View v) {
		DataDbOperations dbOp = new DataDbOperations(v.getContext());
		dbOp.initWrite();
		dbOp.storeData();
	}

	public void loadLocalDb(View v) throws JSONException {
		JSONArray jArray = new JSONArray();
		JSONObject jObject = new JSONObject();
		DataDbOperations dbOp = new DataDbOperations(v.getContext());
		DataBean data = new DataBean();
		if (dbOp.dataExists()) {
			dbOp.moveCursorToFirst();
			data = dbOp.getData();
			jArray.put(createJsonObject(data));
			while (!dbOp.isCursorLast()) {
				dbOp.moveCursorNext();
				data = dbOp.getData();
				jArray.put(createJsonObject(data));
			}
			jObject.put("data", jArray);
			System.out.println(jObject);
		}
	}

	public void clearDb(View v) {
		DataDbOperations dbOp = new DataDbOperations(v.getContext());
		dbOp.clearData();
	}

	JSONObject createJsonObject(DataBean data) throws JSONException {
		JSONObject result = new JSONObject();

		result.put(DataEntry.TIME_STAMP, data.getTimeStamp());
		result.put(DataEntry.USER, data.getUser());
		result.put(DataEntry.OPERATOR, data.getOperator());
		result.put(DataEntry.NETWORK_TYPE, data.getNetworkType());
		result.put(DataEntry.CINR, data.getCinr());
		result.put(DataEntry.LATITUDE, data.getLatitude());
		result.put(DataEntry.LONGITUDE, data.getLongitude());
		System.out.println(result);
		return result;
	}

	private void updateLocationUI(float lon, float lat) {
		latituteField.setText("Latitute: " + lat);
		longitudeField.setText("Longitude: " + lon);

	}

	private void updateOperatorDetailsUI(int signalStrength,String networkType, String operatorName) {
		signalStrengthField.setText("Signal strength: " + signalStrength);
		networkTypeField.setText("Network type: " + networkType);
		operatorNameField.setText("Operator name: " + operatorName);
	}
	
	private ServiceConnection getServiceConnection(){
		ServiceConnection serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName className,IBinder service) {
				TalosBinder binder = (TalosBinder) service;
				mService = binder.getService();
				mBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				mBound = false;
			}
		};
		return serviceConnection;
	}
	
	 
}
