package com.vasa.Saree3;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import androidx.fragment.app.FragmentActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    List<LatLng> listPoint;
    int currentPt;
    
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

        listPoint = new ArrayList<LatLng>();
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
        map.setBuildingsEnabled(true);
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
                // map.animateCamera(CameraUpdateFactory.newLatLng(point));

                // request(point.latitude, point.longitude);
                listPoint.clear();
                listPoint.add(new LatLng(-35.016, 143.321));
                listPoint.add(new LatLng(-34.747, 145.592));
                listPoint.add(new LatLng(-34.364, 147.891));
                listPoint.add(new LatLng(-33.501, 150.217));
                listPoint.add(new LatLng(-32.306, 149.248));
                listPoint.add(new LatLng(-32.491, 147.309));

                startAnimaion();
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

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
    }
    GoogleMap.CancelableCallback mCancelableCallback = new GoogleMap.CancelableCallback() {

        @Override
        public void onFinish() {
            if(++currentPt < listPoint.size()){
                CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(listPoint.get(currentPt))
                            .bearing(45)
                            .tilt(90)
                            .zoom(17)
                            .build();

                map.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition), 
                        3000,
                        mCancelableCallback
                );
            } 
        }

        @Override
        public void onCancel() {

        }
    };
    void startAnimaion() {
        
        
        currentPt = 0;
        
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(listPoint.get(currentPt))
                        .bearing(45)
                        .tilt(90)
                        .zoom(17)
                        .build();

        map.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition), 
                3000,
                mCancelableCallback
        );
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
        map.addPolyline(options.width(10).geodesic(false).pattern(pattern));
    }

    protected Marker addMarker(LatLng position, boolean draggable) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(draggable);
        markerOptions.position(position);
        Marker pinnedMarker = map.addMarker(markerOptions);
        startDropMarkerAnimation(pinnedMarker);
        return pinnedMarker;
    }
    private void startDropMarkerAnimation(final Marker marker) {
        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (200 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}



