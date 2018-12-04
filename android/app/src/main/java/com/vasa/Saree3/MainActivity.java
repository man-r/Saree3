package com.vasa.Saree3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.Manifest;

public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback {
	
	public static final String PREFS_NAME = "MyPrefsFile";

    private DrawerLayout mDrawerLayout;

		
	TextView currentspeed;
	TextView currentspeedlatitude;
	TextView currentspeedlongitude;

	TextView maxspeed;
	TextView maxspeedlatitude;
	TextView maxspeedlongitude;
	
	double currentSpeed = 0.0;

	String maxLat = "";
	String maxLong = "";
	double maxSpeedValue = 0;
	
	
	String phoneNumber = null;
    
	LocationManager locationManager;
	LocationListener locationListener;
	Criteria criteria;
	
	SharedPreferences savedMaxSpeed;
	
	AlertDialog.Builder closebuilder;
	AlertDialog.Builder chalangebuilder;
	
	protected PowerManager.WakeLock mWakeLock;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

    	currentspeed = (TextView)findViewById(R.id.currentspeed);
		currentspeedlatitude = (TextView)findViewById(R.id.currentspeedlatitude);
		currentspeedlongitude = (TextView)findViewById(R.id.currentspeedlongitude);

		maxspeed = (TextView)findViewById(R.id.maxspeed);
		maxspeedlatitude = (TextView)findViewById(R.id.maxspeedlatitude);
		maxspeedlongitude = (TextView)findViewById(R.id.maxspeedlongitude);


    	// Typeface font = Typeface.createFromAsset(getAssets(), "d10re.ttf");
     	// ((TextView)findViewById(R.id.speed)).setTypeface(font);
    	// ((TextView)findViewById(R.id.maxSpeed)).setTypeface(font);
    	// ((TextView)findViewById(R.id.kmh)).setTypeface(font);
    	// latitude.setOnClickListener(textClick);
    	// longitude.setOnClickListener(textClick);
    	// speedText.setOnClickListener(textClick);
    	// kmh.setOnClickListener(textClick);
    	// max.setOnClickListener(textClick);
    	
    	
    	// kmh.setText("");       
    	
        savedMaxSpeed = this.getSharedPreferences("savedMaxSpeed", Context.MODE_PRIVATE);
		maxSpeedValue = savedMaxSpeed.getInt("savedMaxSpeed",0);
		maxLat = savedMaxSpeed.getString("lat", "0");
		maxLong  = savedMaxSpeed.getString("long", "0");


        GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery ("SELECT * FROM geo",null);
		TextView geopoints = (TextView)findViewById(R.id.geopoints);
		geopoints.setText(cursor.getCount() + "");


		maxspeed.setText("" + maxSpeedValue);
		maxspeedlatitude.setText("Lat: " + maxLat);
		maxspeedlongitude.setText("Lon: " + maxLong);


		closebuilder = new AlertDialog.Builder(this);
        closebuilder.setMessage("Are you sure you want to exit?")
	       .setCancelable(false)
	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
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
							message = message + "speed:" + maxSpeedValue + "\n";
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
				        	
				        case R.id.pip:
				        	//Intent topIntent = new Intent(getApplicationContext(), TopTen.class);
				            //startActivityForResult(topIntent, 0);
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
								enterPictureInPictureMode();
							} else {
								// Toast.makeText(getApplicationContext(), "feature avialble in android oreo and above", Toast.LENGTH_SHORT).show();
							}

				            break;
				        case R.id.map:
				        	Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
				            startActivityForResult(mapIntent, 0);
				            break;
				        case R.id.camera:
				        	Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
				            startActivityForResult(cameraIntent, 0);
				            break;
				        case R.id.fingerprint:
				        	Intent fingerprintIntent = new Intent(getApplicationContext(), FingerprintActivity.class);
				            fingerprintIntent.setFlags(fingerprintIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
				            startActivityForResult(fingerprintIntent, 0);
				            break;
			            case R.id.dbview:
				        	Intent dbviewIntent = new Intent(getApplicationContext(), GeoViewActivity.class);
				            startActivityForResult(dbviewIntent, 0);
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
            }
        );

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Constants.NOTIFICATION.CHANNEL_ID)
        	.setSmallIcon(R.drawable.notification_icon)
	        .setContentTitle("Saree3")
	        .setContentText("maxSpeed= " + maxSpeedValue + " Km/h")
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText("maxSpeed= " + maxSpeedValue + " Km/h"))
	        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
	        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
			.setOnlyAlertOnce(true)
	        .setOngoing(false)
            .setChannelId(Constants.NOTIFICATION.CHANNEL_ID)
            .build();

		// check for permission
    	if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    		getPermissions();
    	} else {
	    	Intent startIntent = new Intent(MainActivity.this, MyService.class);
	  		startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
			startService(startIntent);
    	}

    }

    @Override
    protected void onResume() {
    	super.onResume();
    	Log.d(Constants.TAGS.SED_BROADCAST, "registerReceiver");
		registerReceiver(
			mMessageReceiver, 
			new IntentFilter(Constants.ACTION.LOCATION_CHANGED_ACTION));
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d(Constants.TAGS.SED_BROADCAST, "unregisterReceiver");
		unregisterReceiver(mMessageReceiver);
    }

    void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION.ACCESS_FINE_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION.ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    	if (requestCode == Constants.PERMISSION.ACCESS_FINE_LOCATION) {
    		// BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            // Check if the only required permission has been granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            	// Camera permission has been granted, preview can be displayed
                Intent startIntent = new Intent(MainActivity.this, MyService.class);
			  	startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
				startService(startIntent);
			} else {
				Log.i(Constants.TAGS.TAG, "ACCESS_FINE_LOCATION permission was NOT granted.");
                finish();
              }
		}
    }

    public OnClickListener textClick = new OnClickListener () {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(getApplicationContext());
	        SQLiteDatabase db = mDbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery ("SELECT * FROM geo",null);
			TextView geopoints = (TextView)findViewById(R.id.geopoints);
			geopoints.setText(cursor.getCount() + "");

			cursor.close();
		}
		
	};

	BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(Constants.TAGS.SED_BROADCAST, "mMessageReceiver");

			// Get extra data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got message: " + message);

			double alt = intent.getDoubleExtra("altitude",0);
            double lat = intent.getDoubleExtra("latitude",0);
            double lon = intent.getDoubleExtra("longitude",0);
            long time = intent.getLongExtra("time",0);
            double speed = intent.getDoubleExtra("speed",0);

            Log.d(Constants.TAGS.SED_BROADCAST, "speed: " + speed);
			
			if(speed > 0.0) {
				speed = (int) (speed * 3.6);
				if(speed > maxSpeedValue){
					maxSpeedValue = speed;
    				maxLat = "" + lat;
    				maxLong = "" + lon;
    				
    				savedMaxSpeed.edit().putString("lat", maxLat).putString("long", maxLong).putInt("savedMaxSpeed", (int)speed).apply();

				}
			}

            currentspeed.setText("" + speed);
            currentspeedlatitude.setText("lat: " + lat);
            currentspeedlongitude.setText("lon: " + lon);

            maxspeed.setText("" + maxSpeedValue);
            maxspeedlatitude.setText("" + maxLat);
            maxspeedlongitude.setText("" + maxLong);

		}
	};
}
