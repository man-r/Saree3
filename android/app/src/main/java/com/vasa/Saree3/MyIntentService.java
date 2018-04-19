package com.vasa.Saree3;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    
    public static final String TAG = "manar";

    public static final String CHANNEL_ID = "manar";

    public static final String PREFS_NAME = "MyPrefsFile";

    SharedPreferences topSpeed;
    
    LocationManager locationManager;
    LocationListener locationListener;
    Criteria criteria;

    String maxLat="";
    String maxLong = "";
    
    int maxSpeed=0;
    int speed=0;


    public MyIntentService() {
        super("MyIntentService");
    }

    /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
    @Override
    protected void onHandleIntent(Intent intent) {
        topSpeed = this.getSharedPreferences("topspeed", Context.MODE_PRIVATE);
        maxSpeed = topSpeed.getInt("topspeed",0);
        maxLat = topSpeed.getString("lat", "0");
        maxLong  = topSpeed.getString("long", "0");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        criteria = new Criteria();
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);


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

    }



    public class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            // TODO Auto-generated method stub
            if(loc.hasSpeed()){
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
                }                
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
                Toast.makeText(getApplicationContext(), "No Gps !", Toast.LENGTH_SHORT).show();
        }
        
    }
}
