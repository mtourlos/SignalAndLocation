package org.talos.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.talos.beans.DataBean;
import org.talos.db.DataContract.DataEntry;
import org.talos.db.DataDbHelper;
import org.talos.db.DataDbOperations;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class WebServiceTask extends AsyncTask<String, Void, String> {

	public static final int GET_TASK = 1;
	public static final int POST_TASK = 2;
	// connection parameters
	private static final int CONN_TIMEOUT = 3000;
	private static final int SOCKET_TIMEOUT = 5000;
	public static final String TAG = "WebServiceTask";

	private int taskType = GET_TASK;
	private Context mContext = null;
	
	@SuppressWarnings("unused")
	private String response = "Response...";
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

	DataDbHelper mDbHelper;

	public WebServiceTask(int taskType, Context mContext, String response) {
		this.taskType = taskType;
		this.mContext = mContext;
		this.response = response;
	}

	public void addNameValuePair(String name, String value) {

		params.add(new BasicNameValuePair(name, value));

	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... urls) {
		String url = urls[0];
		String result = "";

		HttpResponse response = doResponse(url);

		if (response == null) {
			return result;
		} else {
			try {
				result = inputStreamToString(response.getEntity().getContent());
//				System.out.println(result);

			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String response) {
		System.out.println(response);
	}

	// Connection Establishment
	public HttpParams getHttpParams() {

		HttpParams http = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(http, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(http, SOCKET_TIMEOUT);

		return http;
	}

	public HttpResponse doResponse(String url) {

		// Use our connection and data timeouts as parameters for our
		// DefaultHttpClient
		HttpClient httpclient = new DefaultHttpClient(getHttpParams());

		HttpResponse response = null;

		try {
			switch (taskType) {

			case POST_TASK:
				HttpPost httppost = new HttpPost(url);
				String entity = getEntity().toString();
				System.out.println(entity);
				StringEntity se = new StringEntity(entity);
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httppost.setHeader("Content-type", "application/json");
				httppost.setEntity(se);
				response = httpclient.execute(httppost);
				break;
			case GET_TASK:
				HttpGet httpget = new HttpGet(url);
				response = httpclient.execute(httpget);
				break;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return response;
	}

	public String inputStreamToString(InputStream is) {

		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			// Read response until the end
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		// Return full string
		return total.toString();
	}

	public JSONObject getEntity() throws JSONException {
		JSONArray jArray = new JSONArray();
		JSONObject result = new JSONObject();
		DataDbOperations dbOp = new DataDbOperations(mContext);
		DataBean data = new DataBean();
		dbOp.initRead();
		dbOp.moveCursorToFirst();
		data = dbOp.getData();
		jArray.put(createJsonObject(data));
		while (!dbOp.isCursorLast()) {
			dbOp.moveCursorNext();
			data = dbOp.getData();
			jArray.put(createJsonObject(data));
		}
		result.put("data", jArray);
		return result;
	}

	JSONObject createJsonObject(DataBean data) throws JSONException {
		JSONObject result = new JSONObject();
		//TODO To timestamp den pernaei stin vasi
		result.put(DataEntry.TIME_STAMP, data.getTimeStamp());
		result.put(DataEntry.USER, data.getUser());
		result.put(DataEntry.OPERATOR, data.getOperator());
		result.put(DataEntry.NETWORK_TYPE, data.getNetworkType());
		result.put(DataEntry.CINR, data.getCinr());
		result.put(DataEntry.LATITUDE, data.getLatitude());
		result.put(DataEntry.LONGITUDE, data.getLongitude());
		return result;
	}

}
