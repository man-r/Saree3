package com.vasa.Saree3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import android.location.LocationListener;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyService extends Service {
    public static final String TAG = "manar";
    SharedPreferences topSpeed;
    int maxSpeed;
    String maxLat;
    String maxLong;

    String CHANNEL_ID = "manar";// The id of the channel.
    int notifyID = 1; 
    
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    Bitmap icon;

    PendingIntent pendingIntent;
    Intent stopIntent;
    PendingIntent pstopIntent;

    LocalBroadcastManager localBroadcastManager;
    LocationManager locationManager;
    LocationListener locationListener;
    Criteria criteria;


    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        topSpeed = this.getSharedPreferences("topspeed", Context.MODE_PRIVATE);
        maxSpeed = topSpeed.getInt("topspeed",0);
        maxLat = topSpeed.getString("lat", "0");
        maxLong  = topSpeed.getString("long", "0");

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Toast.makeText(this, "Received Start Foreground Intent ", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Received Start Foreground Intent ");


            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent enableGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            PendingIntent penableGPSIntent = PendingIntent.getService(this, 0, enableGPSIntent, 0);

            Intent playIntent = new Intent(this, MyService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);

            PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

            Intent nextIntent = new Intent(this, MyService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);

            PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            stopIntent = new Intent(this, MyService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);

            pstopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

            
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            criteria = new Criteria();
            criteria.setSpeedRequired(true);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, locationListener);
            }

            //if GPS Enabled get lat/long using GPS Services
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0, locationListener);
            }

            // if (locationManager != null){
            //     Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            //     if (loc !=null) {
                    
            //         JSONObject obj = getLocationObject(loc);
            //         mCallbackContext.success(obj.toString());
            //     }

            // }
            //criteria.setSpeedRequired(true);
            //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            
            String bestProvider = locationManager.getBestProvider(criteria, true);

            if ((bestProvider != null) && (bestProvider.contains("gps"))){
                CharSequence name = "Saree3";// The user-visible name of the channel.
            
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("Saree3")
                    .setTicker("Saree3 Tracker")
                    .setContentText("maxSpeed= " + maxSpeed + " Km/h")
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_next, "Stop", pstopIntent)
                    .setChannelId(CHANNEL_ID)
                    .build();

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(mChannel);
                }
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
                locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
                Toast.makeText(this, "requestLocationUpdates", Toast.LENGTH_SHORT).show();
            }
            else{
                CharSequence name = "Saree3";// The user-visible name of the channel.
            
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("No GPS!")
                    .setTicker("No GPS!")
                    .setContentText("Click to Enable GPS")
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0))
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_next, "Stop", pstopIntent)
                    .setChannelId(CHANNEL_ID)
                    .build();

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(mChannel);
                }
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            }


        } else if (intent.getAction().equals(Constants.ACTION.ENABLEGPS_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Toast.makeText(this, "Received Stop Foreground Intent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Received Stop Foreground Intent");
            locationManager.removeUpdates(locationListener);
            stopForeground(true);
            stopSelf();
        }

        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        super.onDestroy();
        Log.i(TAG, "In onDestroy");
    }



    public class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            // Gets the data repository in write mode
            
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            status.getSubscriptionStatus().getUserId();
            GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put("playerid", status.getSubscriptionStatus().getUserId());
            values.put("latitude", loc.getLatitude()+"");
            values.put("longitude", loc.getLongitude()+"");
            values.put("altitude", loc.getAltitude()+"");
            values.put("timestamp", loc.getTime() + "");
            values.put("speed", loc.getSpeed() + "");
            
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert("geo", null, values);
            //Toast.makeText(getApplicationContext(), newRowId + "newRowId inserted", Toast.LENGTH_LONG).show();
            

            // Create intent with action
            Intent localIntent = new Intent(Constants.ACTION.LOCATION_CHANGED_ACTION);
            localIntent.putExtra("altitude", loc.getAltitude());
            localIntent.putExtra("latitude", loc.getLatitude());
            localIntent.putExtra("longitude", loc.getLongitude());
            localIntent.putExtra("time", loc.getTime());
            localIntent.putExtra("speed", loc.getSpeed());
            
            if(loc.hasSpeed()){
                
                int speed = (int) (loc.getSpeed()* 3.6);
                if(speed>maxSpeed){
                    maxSpeed=speed;
                    maxLat = "" + loc.getLatitude();
                    maxLong = "" + loc.getLongitude();
                    
                    topSpeed.edit().putString("lat", maxLat).putString("long", maxLong).putInt("topspeed", speed).apply();
                    
                    CharSequence name = "Saree3";// The user-visible name of the channel.
                
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle("Saree3")
                        .setTicker("Saree3 Tracker")
                        .setContentText("maxSpeed= " + maxSpeed + " Km/h")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .addAction(android.R.drawable.ic_media_next, "Stop", pstopIntent)
                        .setChannelId(CHANNEL_ID)
                        .setOnlyAlertOnce(true)
                        .build();

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                        mNotificationManager.createNotificationChannel(mChannel);
                    }
                        
                }
                else {
                    
                }
                
            }
            
            else{
                //max.setText("No Speed Data !");
            }

            // Send local broadcast
            localBroadcastManager.sendBroadcast(localIntent);
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
            Intent startIntent = new Intent(getApplicationContext(), MyService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "Gps Esabled", Toast.LENGTH_SHORT).show();
            Intent startIntent = new Intent(getApplicationContext(), MyService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            if(status!=2)
                Toast.makeText(getApplicationContext(), "No Gps !", Toast.LENGTH_SHORT).show();
        }
        
    }
}