package com.vasa.Saree3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.os.Build;
import android.support.design.widget.NavigationView;

public class MainActivity extends AppCompatActivity {
	
	public static final String TAG = "manar";

	public static final String CHANNEL_ID = "manar";

    public static final String PREFS_NAME = "MyPrefsFile";
    
    private static final int ACCESS_NETWORK_STATE = 2;
	private static final int ACCESS_FINE_LOCATION = 3;
	private static final int WAKE_LOCK = 4;
	private static final int INTERNET = 5;
	private static final int READ_PHONE_STATE = 6;
	private static final int WRITE_EXTERNAL_STORAGE = 7;
	private static final int CAMERA = 8;
	private static final int RECORD_AUDIO = 9;
    //public static final int REQUEST_CODE_EMAIL = 3;  // The request code

    private DrawerLayout mDrawerLayout;

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
	
	AlertDialog.Builder closebuilder;
	AlertDialog.Builder chalangebuilder;
	
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
        
        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // final EditText textBox = );
								
        chalangebuilder = new AlertDialog.Builder(this);
    	chalangebuilder.setMessage("enter your name?")
    	       .setCancelable(false)
    	       .setView(new EditText(this))
    	       .setPositiveButton("post", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   // chalenge(textBox.getText().toString());
    	           }
    	       })
    	       .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	
    	closebuilder = new AlertDialog.Builder(this);
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
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        switch(menuItem.getItemId()){
							case R.id.close:
					        	
					        	AlertDialog alert = closebuilder.create();
					        	alert.show();
					        	break;
					        	
							case R.id.GPSSwitch:
					        	Intent switchIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					            startActivity(switchIntent);        
					            break;
					            
					        case R.id.share:
					        	String message = "my top speed is:\n";
								message = message + "speed:" + maxSpeed + "\n";
								message = message + "http://maps.google.com/maps?q=" + maxLat + "," + maxLong + "\n";
								message = message + "this message is sent from saree3";

								Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
								sharingIntent.setType("text/plain");
								sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "shareing subject");
								sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

								startActivity(Intent.createChooser(sharingIntent, "Share via"));
					        	break;
					        	
					        case R.id.chalange:
					        	AlertDialog chalangealert = chalangebuilder.create();		        	
					        	chalangealert.show();
					        	break;
					        	
					        case R.id.top:
					        	//Intent topIntent = new Intent(getApplicationContext(), TopTen.class);
					            //startActivityForResult(topIntent, 0);
					            break;
					        case R.id.map:
					        	Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
					            startActivityForResult(mapIntent, 0);
					            break;
					        case R.id.camera:
					        	Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
					            startActivityForResult(cameraIntent, 0);
					            break;
					        default:
					            break;
					        	
							}
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        Intent intent = new Intent(getApplicationContext(), GetPermission.class);
        intent.putExtra("permission", ACCESS_FINE_LOCATION);
        startActivityForResult(intent, ACCESS_FINE_LOCATION);

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
        
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();


        

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle("Saree3")
        .setContentText("maxSpeed= " + maxSpeed + " Km/h")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText("maxSpeed= " + maxSpeed + " Km/h"))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
		.setOnlyAlertOnce(true)
        .setOngoing(false);

        
		// notificationId is a unique int for each notification that you must define
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(3, mBuilder.build());
		

        latitude.setText("latitude: " + maxLat);
    	longitude.setText("longitude: " + maxLong);
    	speedText.setText("" + maxSpeed);

    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode){
			case ACCESS_NETWORK_STATE:
	        	if (resultCode == RESULT_OK) {

	        	}
	        	break;
	        	
			case ACCESS_FINE_LOCATION:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case WAKE_LOCK:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case INTERNET:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case READ_PHONE_STATE:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case WRITE_EXTERNAL_STORAGE:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case CAMERA:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
			case RECORD_AUDIO:
	        	if (resultCode == RESULT_OK) {
	        		
	        	}
	        	break;
	        default:
	            break;
	        	
			}
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
					
    				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
			        .setSmallIcon(R.drawable.notification_icon)
			        .setContentTitle("Saree3")
			        .setContentText("maxSpeed= " + maxSpeed + " Km/h")
			        .setStyle(new NotificationCompat.BigTextStyle()
			        .bigText("maxSpeed= " + maxSpeed + " Km/h"))
			        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
			        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
			        .setOnlyAlertOnce(true)
			        .setOngoing(false);

			        
					// notificationId is a unique int for each notification that you must define
					NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
					notificationManager.notify(3, mBuilder.build());
    				
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
