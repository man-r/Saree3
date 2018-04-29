package com.vasa.Saree3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
        
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.animateCamera(CameraUpdateFactory.newLatLng(point));

                request(point.latitude, point.longitude);
            }
        });
        GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery ("SELECT * FROM geo",null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount() && i < 1000; i++) {
            double lat = Double.parseDouble(cursor.getString(1));
            double lon = Double.parseDouble(cursor.getString(2));
            int speed = (int) (Double.parseDouble(cursor.getString(4)) * 3.6);
           
            map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(speed + " Km/h"));

            cursor.moveToNext();
        }
    }


    public void request(double lat, double lon){
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic MDEyNmVkZDgtZmJhOS00NDYzLWFiMTAtZDQ3ZGNkOWY5ZGZl");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                      +   "\"app_id\": \"bc75391b-17c9-4ea0-ad32-d5832cf8f9b9\","
                      +   "\"included_segments\": [\"All\"],"
                      +   "\"data\": {\"foo\": \"bar\"},"
                      +   "\"contents\": {\"en\": \"English Message\"}"
                      + "}";

            Log.i(Constants.TAGS.TAG, "strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            Log.i(Constants.TAGS.TAG, "httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }

            Log.i(Constants.TAGS.TAG, "jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            Log.e(Constants.TAGS.TAG, t.toString(),t);
        }
    }
}



