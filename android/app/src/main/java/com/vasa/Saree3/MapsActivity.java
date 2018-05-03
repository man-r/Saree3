package com.vasa.Saree3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity 
    implements OnMapReadyCallback {
    
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.aubergine_style_json));

            if (!success) {
                Log.e(Constants.TAGS.TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Constants.TAGS.TAG, "Can't find style. Error: ", e);
        }
        
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.animateCamera(CameraUpdateFactory.newLatLng(point));

                request(point.latitude, point.longitude);
            }
        });
        // GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(this);
        // SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Cursor cursor = db.rawQuery ("SELECT * FROM geo",null);
        // cursor.moveToFirst();
        // for (int i = 0; i < cursor.getCount() && i < 1000; i++) {
        //     double lat = Double.parseDouble(cursor.getString(1));
        //     double lon = Double.parseDouble(cursor.getString(2));
        //     int speed = (int) (Double.parseDouble(cursor.getString(4)) * 3.6);
           
        //     map.addMarker(new MarkerOptions()
        //         .position(new LatLng(lat, lon))
        //         .title(speed + " Km/h"));

        //     cursor.moveToNext();
        // }


        LatLng sydney1 = new LatLng(-33.904438,151.249852);
        LatLng sydney2 = new LatLng(-33.905823,151.252422);

        map.addMarker(new MarkerOptions().position(sydney1)
                .draggable(false).visible(true).title("Marker in Sydney 1"));
        map.addMarker(new MarkerOptions().position(sydney2)
                .draggable(false).visible(true).title("Marker in Sydney 2"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 16F));

        this.showCurvedPolyline(sydney1,sydney2, 0.5);
    }

    private void showCurvedPolyline (LatLng p1, LatLng p2, double k) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1,p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d*0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1-k*k)*d*0.5/(2*k);
        double r = (1+k*k)*d*0.5/(2*k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 -h1) / numpoints;

        for (int i=0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }

        //Draw polyline
        map.addPolyline(options.width(10).color(Color.MAGENTA).geodesic(false).pattern(pattern));
    }
    public void request(double lat, double lon){
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        // try {
        //     String jsonResponse;

        //     URL url = new URL("https://onesignal.com/api/v1/notifications");
        //     HttpURLConnection con = (HttpURLConnection)url.openConnection();
        //     con.setUseCaches(false);
        //     con.setDoOutput(true);
        //     con.setDoInput(true);

        //     con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        //     con.setRequestProperty("Authorization", "Basic MDEyNmVkZDgtZmJhOS00NDYzLWFiMTAtZDQ3ZGNkOWY5ZGZl");
        //     con.setRequestMethod("POST");

        //     String strJsonBody = "{"
        //               +   "\"app_id\": \"bc75391b-17c9-4ea0-ad32-d5832cf8f9b9\","
        //               +   "\"included_segments\": [\"All\"],"
        //               +   "\"data\": {\"foo\": \"bar\"},"
        //               +   "\"contents\": {\"en\": \"English Message\"}"
        //               + "}";

        //     Log.i(Constants.TAGS.TAG, "strJsonBody:\n" + strJsonBody);

        //     byte[] sendBytes = strJsonBody.getBytes("UTF-8");
        //     con.setFixedLengthStreamingMode(sendBytes.length);

        //     OutputStream outputStream = con.getOutputStream();
        //     outputStream.write(sendBytes);

        //     int httpResponse = con.getResponseCode();
        //     Log.i(Constants.TAGS.TAG, "httpResponse: " + httpResponse);

        //     if (  httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
        //         Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
        //         jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        //         scanner.close();
        //     } else {
        //         Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
        //         jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        //         scanner.close();
        //     }

        //     Log.i(Constants.TAGS.TAG, "jsonResponse:\n" + jsonResponse);

        // } catch(Throwable t) {
        //     Log.e(Constants.TAGS.TAG, t.toString(),t);
        // }
    }
}



