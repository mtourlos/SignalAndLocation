package com.example.signalandlocation;


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
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



public class WebServiceTask extends AsyncTask<String,Void,String>{

	public static final int GET_TASK=1;
	public static final int POST_TASK=2;
	//connection parameters
	private static final int CONN_TIMEOUT=3000;
	private static final int SOCKET_TIMEOUT=5000;
	public static final String TAG="WebServiceTask";
	
	private int taskType = GET_TASK;
	private Context mContext = null;
	private String processMessage = "Processing...";
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	
	public WebServiceTask(int taskType, Context mContext, String processMessage){
		this.taskType = taskType;
		this.mContext = mContext;
		this.processMessage = processMessage; 
	}
	
	 public void addNameValuePair(String name, String value) {
		 
         params.add(new BasicNameValuePair(name, value));

     }
	
	@Override
	protected void onPreExecute(){
		
	}

	@Override
	protected String doInBackground(String... urls) {
		String url = urls[0];
		String result="";
		
		HttpResponse response = doResponse(url);
		
		if (response == null){
			return result;
		}else{
			try{
				result = inputStreamToString(response.getEntity().getContent());
				System.out.println(result);
			} catch (IllegalStateException e){
				Log.e(TAG, e.getLocalizedMessage(), e);
			}catch (IOException e){
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
		return result;
	}
	
	@Override 
	protected void onPostExecute(String response){
		/*if (response!=null) {
			Context context = getApplicationContext();
			CharSequence text = response;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text,duration);
			toast.show();	
		}*/
	}
	
	//Connection Establishment
	public HttpParams getHttpParams(){
		
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
                
                // Add parameters
                String entity = "{ \"data\":[ { \""+params.get(0).getName()+"\":\""+params.get(0).getValue()+"\", \""+params.get(1).getName()+"\":\""+params.get(1).getValue()+"\", \""+params.get(2).getName()+"\":\""+params.get(2).getValue()+"\", \""+params.get(3).getName()+"\":\""+params.get(3).getValue()+"\", \""+params.get(4).getName()+"\":\""+params.get(4).getValue()+"\", \""+params.get(5).getName()+"\":\""+params.get(5).getValue()+"\" } ] }" ;
                StringEntity se = new StringEntity(entity);
                System.out.println(entity);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                httppost.setHeader("Content-type","application/json");
                httppost.setEntity(se);
                
                //httppost.setEntity(new UrlEncodedFormEntity(params));

                response = httpclient.execute(httppost);
                //System.out.println("****"+response);
                break;
            case GET_TASK:
                HttpGet httpget = new HttpGet(url);
                response = httpclient.execute(httpget);
                //System.out.println("****"+response);
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
	
}
	

