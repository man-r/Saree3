package com.vasa.Saree3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class TopTen extends ListActivity {

	Context context;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    context = this;
	    LongOperation longOp = new LongOperation();
	    longOp.execute("https://api.mongolab.com/api/1/databases/saree3/collections/saree3?apiKey=bte7Wf-HKy9jhmrjKqHaN45tzdy_08EA");
	    
	}
	
	private class LongOperation extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpclient = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(params[0]);
	        String responseString = null;
	        
	                
	        // Execute HTTP Post Request
	        try {
	        	HttpResponse response = httpclient.execute(httpGet);
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			return responseString;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = ProgressDialog.show(context, "", "please wait");
	        
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog != null)
				dialog.dismiss();
			try {
				JSONArray array = (JSONArray) new JSONTokener(result).nextValue();
				ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
			    String[] from = { "name", "speed" };
			    int[] to = { android.R.id.text1, android.R.id.text2 };

			    
			    for (int i = 0; i < array.length(); i++) {
			    	JSONObject json_data = array.getJSONObject(i);
			    	HashMap<String, String> item = new HashMap<String, String>();
			    	item.put("name", json_data.getString("device"));
			    	item.put("speed", "Top Speed: " + json_data.getString("speed"));
			    	
			    	list.add(item);
	            }
			    
			    
			    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list, android.R.layout.simple_list_item_2, from, to);
		        setListAdapter(adapter);
		        
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
