package com.vasa.Saree3;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyService2 extends Service {
    
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

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    private PendingIntent transitionPendingIntent;

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        Intent intent = new Intent(this, TransitionIntentService.class);
        transitionPendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                
                for (Location loc : locationResult.getLocations()) {
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

                    if(loc.hasSpeed()) {
                        int speed = (int) (loc.getSpeed()* 3.6);
                        if(speed>maxSpeed) {
                            maxSpeed=speed;
                            maxLat = "" + loc.getLatitude();
                            maxLong = "" + loc.getLongitude();

                            topSpeed.edit().putString("lat", maxLat).putString("long", maxLong).putInt("topspeed", speed).apply();
                        }
                    }
                }
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
                    .setOnlyAlertOnce(true).setDefaults(Notification.DEFAULT_ALL).setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                    .build();

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Saree3", NotificationManager.IMPORTANCE_LOW);
                    mChannel.setSound(null, null);
                    mNotificationManager.createNotificationChannel(mChannel);
                }
            };
        };
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
            Log.i(Constants.TAGS.TAG, "Received Start Foreground Intent ");


            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent enableGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            PendingIntent penableGPSIntent = PendingIntent.getService(this, 0, enableGPSIntent, 0);

            stopIntent = new Intent(this, MyService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);

            pstopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Saree3")
                .setTicker("Saree3 Tracker")
                .setContentText("maxSpeed= " + maxSpeed + " Km/h")
                .setSmallIcon(R.drawable.notification_icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_next, "Stop", pstopIntent)
                .setChannelId(CHANNEL_ID).setDefaults(Notification.DEFAULT_ALL).setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .build();

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Saree3", NotificationManager.IMPORTANCE_LOW);
                mChannel.setSound(null, null);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            startLocationUpdates();
            startActivityTransactionUpdates();
            Toast.makeText(this, "requestLocationUpdates", Toast.LENGTH_SHORT).show();
            

        } else if (intent.getAction().equals(Constants.ACTION.ENABLEGPS_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(Constants.TAGS.TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Toast.makeText(this, "Received Stop Foreground Intent", Toast.LENGTH_SHORT).show();
            Log.i(Constants.TAGS.TAG, "Received Stop Foreground Intent");
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
        Log.i(Constants.TAGS.TAG, "In onDestroy");
        stopLocationUpdates();
        stopActivityTransactionUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.LOCATION.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(Constants.LOCATION.MIN_DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    private void startActivityTransactionUpdates() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.IN_VEHICLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.IN_VEHICLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.ON_BICYCLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.ON_BICYCLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());


        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.ON_FOOT)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.ON_FOOT)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());
        
        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.RUNNING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.RUNNING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());
        
        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.STILL)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.STILL)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());
        
        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.TILTING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.TILTING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());
        
        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.UNKNOWN)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.UNKNOWN)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.WALKING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build());

        transitions.add(
          new ActivityTransition.Builder()
            .setActivityType(DetectedActivity.WALKING)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        Task<Void> task =
          ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, transitionPendingIntent);

          task.addOnSuccessListener(
            new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void result) {
                // Handle success
              }
            }
          );

          task.addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(Exception e) {
                // Handle error
              }
            }
          );
    }

    private void stopActivityTransactionUpdates() {
        Task<Void> task = ActivityRecognition.getClient(this).removeActivityTransitionUpdates(transitionPendingIntent);

        task.addOnSuccessListener(
          new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
              transitionPendingIntent.cancel();
            }
          });

        task.addOnFailureListener(
          new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
              Log.e("MYCOMPONENT", e.getMessage());
            }
          });
    }

}