package com.vasa.Mongo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class MongoPut extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpput = new HttpPut(params[0]);
        String responseString = null;
        try {
            // Add your data
        	String jsonString = params[1];
        	
        	JSONObject json = new JSONObject(jsonString);
        	
        	StringEntity se = new StringEntity( json.toString());  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            
            httpput.setEntity(se);
            
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpput);
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
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
	}

}
