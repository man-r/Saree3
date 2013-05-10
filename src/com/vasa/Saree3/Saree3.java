package com.vasa.Saree3;

import com.vasa.Mongo.MongoPost;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Saree3 extends Activity {

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		 
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
	}

	TextView latitude;
	TextView longitude;
	TextView speedText;
	TextView kmh;
	TextView max;
	
	
	String maxLat="";
	String maxLong = "";
	
	int maxSpeed=0;
	int speed=0;
	
	String phoneNumber = null;
    
	LocationManager locationManager;
	LocationListener locationListener;
	Criteria criteria;
	
	SharedPreferences topSpeed;
	
	protected PowerManager.WakeLock mWakeLock;
	
    /** Called when the activity is first created. */
    
	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        latitude = (TextView)findViewById(R.id.latitude);
    	longitude = (TextView)findViewById(R.id.longitude);
    	speedText = (TextView)findViewById(R.id.speed);
    	kmh = (TextView)findViewById(R.id.kmh);
    	max = (TextView)findViewById(R.id.maxSpeed);
    	
    	latitude.setOnClickListener(textClick);
    	longitude.setOnClickListener(textClick);
    	speedText.setOnClickListener(textClick);
    	kmh.setOnClickListener(textClick);
    	max.setOnClickListener(textClick);
    	
    	
    	kmh.setText("");       
    	
        topSpeed = this.getSharedPreferences("topspeed", Context.MODE_PRIVATE);
		maxSpeed = topSpeed.getInt("topspeed",0);
		maxLat = topSpeed.getString("lat", "0");
		maxLong  = topSpeed.getString("long", "0");
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		criteria = new Criteria();
        
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        Typeface font = Typeface.createFromAsset(getAssets(), "d10re.ttf");
        ((TextView)findViewById(R.id.speed)).setTypeface(font);
        ((TextView)findViewById(R.id.maxSpeed)).setTypeface(font);
        ((TextView)findViewById(R.id.kmh)).setTypeface(font);
        
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        
      
        //Get a reference to the NotificationManager:
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		
		Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.number = maxSpeed;
		notification.icon = R.drawable.notification_icon;
		notification.tickerText = "Saree3";
		
		//Define the notification message and PendingIntent
		CharSequence contentTitle = "Saree3";
		CharSequence contentText = "maxSpeed= " + maxSpeed + " Km/h";
		Intent notificationIntent = new Intent(this, Saree3.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		
		//Pass the Notification to the NotificationManager
		mNotificationManager.notify(3, notification);
		
		latitude.setText("latitude: " + maxLat);
    	longitude.setText("longitude: " + maxLong);
    	speedText.setText("" + maxSpeed);
    }

	

    @SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		        
        String bestProvider = locationManager.getBestProvider(criteria, true);
       
        if ((bestProvider != null) && (bestProvider.contains("gps"))){
        	locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
        }
        else{
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("No GPS!")
        	       .setCancelable(true)
        	       .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   Intent switchIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        	               startActivityForResult(switchIntent, 0);
        	           }
        	       })
        	       .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   locationManager.removeUpdates(locationListener);
           	       		
	        	       	   String ns = Context.NOTIFICATION_SERVICE;
	        	       	   NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	        	       	   mNotificationManager.cancel(3);
        	       		
        	        	   finish();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }
           	
        //Toast.makeText(getApplicationContext(), "onStart", Toast.LENGTH_LONG).show();
	}

		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.close:
        	AlertDialog.Builder closebuilder = new AlertDialog.Builder(this);
        	closebuilder.setMessage("Are you sure you want to exit?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   
        	        	   locationManager.removeUpdates(locationListener);
        	        	   mWakeLock.release();
        	       		
	        	       	   String ns = Context.NOTIFICATION_SERVICE;
	        	       	   NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	        	       	   mNotificationManager.cancel(3);
        	       		
        	        	   finish();
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = closebuilder.create();
        	alert.show();
        	break;
        	
		case R.id.GPSSwitch:
        	Intent switchIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(switchIntent);        
            break;
            
        case R.id.share:
        	shareIt();
        	break;
        	
        case R.id.chalange:
        	final EditText textBox = new EditText(this);
        	AlertDialog.Builder chalangebuilder = new AlertDialog.Builder(this);
        	chalangebuilder.setMessage("enter your name?")
        	       .setCancelable(false)
        	       .setView(textBox)
        	       .setPositiveButton("post", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   chalenge(textBox.getText().toString());
        	           }
        	       })
        	       .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog chalangealert = chalangebuilder.create();
        	chalangealert.show();
        	break;
        	
        case R.id.top:
        	Intent topIntent = new Intent(getApplicationContext(), TopTen.class);
            startActivityForResult(topIntent, 0);
            break;
        default:
            break;
        	
		}
		return true;
	}
    
	String getDiffrence(long now, long then){
    	if(now > then)
            return DateUtils.formatElapsedTime((now - then)/1000L);
        else 
        	return DateUtils.formatElapsedTime((then - now)/1000L);
    }
	
	public void shareIt() {
				
		String message = "my top speed is:\n";
    	message = message + "speed:" + maxSpeed + "\n";
    	message = message + "http://maps.google.com/maps?q=" + maxLat + "," + maxLong + "\n";
    	message = message + "this message is sent from saree3";
    	
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "shareing subject");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	public void chalenge(String name) {
		
		String url = "https://api.mongolab.com/api/1/databases/saree3/collections/saree3?apiKey=bte7Wf-HKy9jhmrjKqHaN45tzdy_08EA";
		
		String jsonString = "{"
		        + "  \"device\": \"" + name + "\", "
		        + "  \"speed\": \"" + maxSpeed + "\", "
		        + "  \"location\": {"
		        	+ " \"lat\": " + maxLat + ", "
		        	+ " \"long\": " + maxLong
		        	+ "}"
		        + "}";
		
		new MongoPost().execute(url, jsonString);
		Toast.makeText(getApplicationContext(), "Data is uploded", Toast.LENGTH_SHORT).show();
	}
	
	public OnClickListener textClick = new OnClickListener () {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			String state = max.getText().toString();
			if (state.equals("maxSpeed")) {
				max.setText("currentSpeed");
				latitude.setText("latitude:");
				longitude.setText("longitude:");
		    	speedText.setText("0");
		    }
			else if (state.equals("currentSpeed")) {
				max.setText("maxSpeed");
				latitude.setText("latitude: " + maxLat);
		    	longitude.setText("longitude: " + maxLong);
		    	speedText.setText("" + maxSpeed);
			}
		}
		
	};
	
	public class MyLocationListener implements LocationListener{

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			if(loc.hasSpeed()){
				String state = max.getText().toString();
				speed = (int) (loc.getSpeed()* 3.6);
				if(speed>maxSpeed){
					maxSpeed=speed;
    				maxLat = "" + loc.getLatitude();
    				maxLong = "" + loc.getLongitude();
    				
    				topSpeed.edit().putString("lat", maxLat).putString("long", maxLong).putInt("topspeed", speed).apply();
					
    				//Get a reference to the NotificationManager:
    				String ns = Context.NOTIFICATION_SERVICE;
    				NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
    				
    				Notification notification = new Notification();
    				notification.flags = Notification.FLAG_ONGOING_EVENT;
    				notification.number = maxSpeed;
    				notification.icon = R.drawable.notification_icon;
    				notification.tickerText = maxSpeed + "";
    				
    				//Define the notification message and PendingIntent
    				CharSequence contentTitle = "Saree3";
    				CharSequence contentText = "maxSpeed= " + maxSpeed + " Km/h";
    				Intent notificationIntent = new Intent(getApplicationContext(), Saree3.class);
    				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
    				
    				notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
    				
    				//Pass the Notification to the NotificationManager
    				mNotificationManager.notify(3, notification);
    				
    				if (state.equals("maxSpeed")) {
    					speedText.setText("" + speed);
    				}
    				
    				latitude.setText("latitude: " + loc.getLatitude());
    				longitude.setText("longitude: " + loc.getLongitude());
    				
				}
				else {
					if (state.equals("currentSpeed")) {
    					speedText.setText("" + speed);
    					latitude.setText("latitude: " + loc.getLatitude());
        				longitude.setText("longitude: " + loc.getLongitude());
        				
    				}
    				
				}
    			
			}
			
			else{
				max.setText("No Speed Data !");
			}		
			
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Gps Esabled", Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			if(status!=2)
    			max.setText("No Gps !");
		}
		
	}
}