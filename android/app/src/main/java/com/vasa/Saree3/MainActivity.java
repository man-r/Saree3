package com.vasa.Saree3;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.content.SharedPreferences;
import android.os.PowerManager;

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

public class MainActivity extends Activity {
	
	public static final String TAG = "manar";

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int GET_PERMISSION_REQUEST = 2;  // The request code
    public static final int REQUEST_CODE_EMAIL = 3;  // The request code

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        // Intent intent = new Intent(getApplicationContext(), GetPermission.class);
        // startActivityForResult(intent, GET_PERMISSION_REQUEST);

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
        
        // Typeface font = Typeface.createFromAsset(getAssets(), "d10re.ttf");
        // ((TextView)findViewById(R.id.speed)).setTypeface(font);
        // ((TextView)findViewById(R.id.maxSpeed)).setTypeface(font);
        // ((TextView)findViewById(R.id.kmh)).setTypeface(font);
        
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
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		//notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		
		//Pass the Notification to the NotificationManager
		mNotificationManager.notify(3, notification);
		
		latitude.setText("latitude: " + maxLat);
    	longitude.setText("longitude: " + maxLong);
    	speedText.setText("" + maxSpeed);

    }

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
    				Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
    				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
    				
    				//notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
    				
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
