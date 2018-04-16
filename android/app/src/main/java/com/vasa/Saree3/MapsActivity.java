package com.vasa.Saree3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity 
    implements OnMapReadyCallback {
    public static final String CHANNEL_ID = "manar";

    private GoogleMap map;


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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        
    }



    public class MyLocationListener implements LocationListener{

        @SuppressLint("NewApi")
        @SuppressWarnings("deprecation")
        public void onLocationChanged(Location loc) {
            // TODO Auto-generated method stub
            LatLng myLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
            // Move the camera instantly to Sydney with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomIn());

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
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
                Toast.makeText(getApplicationContext(), "No Gps ", Toast.LENGTH_SHORT).show();
        }
        
    }
}



