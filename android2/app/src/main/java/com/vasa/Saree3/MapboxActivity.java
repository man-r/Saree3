package com.vasa.Saree3;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Scanner;

//import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapboxActivity extends AppCompatActivity {

    private static final String TAG = "MarkerFollowingRoute";
    private static final String DOT_SOURCE_ID = "dot-source-id";
    private static final String LINE_SOURCE_ID = "line-source-id";
    private int count = 0;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Handler handler;
    private Runnable runnable;
    private GeoJsonSource dotGeoJsonSource;
    private ValueAnimator markerIconAnimator;
    private LatLng markerIconCurrentLocation;
    private List<Point> routeCoordinateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "pk.eyJ1IjoibWFuLXIiLCJhIjoidWVOT3g2cyJ9.MBeJ47Rnt7yryhvwyvFTvw");

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_mapbox);

// Initialize the mapboxMap view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MapboxActivity.this.mapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        new LoadGeoJson(MapboxActivity.this).execute();
                    }
                });
            }
        });
    }

    /**
     * Add data to the map once the GeoJSON has been loaded
     *
     * @param featureCollection returned GeoJSON FeatureCollection from the async task
     */
    private void initData(@NonNull FeatureCollection featureCollection) {
        LineString lineString = (LineString) featureCollection.features().get(0).geometry();
        routeCoordinateList = lineString.coordinates();
        if (mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                initSources(style, featureCollection);
                initSymbolLayer(style);
                initDotLinePath(style);
                initRunnable();
            }
        }
    }

    /**
     * Set up the repeat logic for moving the icon along the route.
     */
    private void initRunnable() {
// Animating the marker requires the use of both the ValueAnimator and a handler.
// The ValueAnimator is used to move the marker between the GeoJSON points, this is
// done linearly. The handler is used to move the marker along the GeoJSON points.
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
// Check if we are at the end of the points list, if so we want to stop using
// the handler.
                if ((routeCoordinateList.size() - 1 > count)) {

                    Point nextLocation = routeCoordinateList.get(count + 1);

                    if (markerIconAnimator != null && markerIconAnimator.isStarted()) {
                        markerIconCurrentLocation = (LatLng) markerIconAnimator.getAnimatedValue();
                        markerIconAnimator.cancel();
                    }
                    if (latLngEvaluator != null) {
                        markerIconAnimator = ObjectAnimator
                                .ofObject(latLngEvaluator, count == 0 ? new LatLng(37.61501, -122.385374)
                                                : markerIconCurrentLocation,
                                        new LatLng(nextLocation.latitude(), nextLocation.longitude()))
                                .setDuration(300);
                        markerIconAnimator.setInterpolator(new LinearInterpolator());

                        markerIconAnimator.addUpdateListener(animatorUpdateListener);
                        markerIconAnimator.start();

// Keeping the current point count we are on.
                        count++;

// Once we finish we need to repeat the entire process by executing the
// handler again once the ValueAnimator is finished.
                        handler.postDelayed(this, 300);
                    }
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * Listener interface for when the ValueAnimator provides an updated value
     */
    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    if (dotGeoJsonSource != null) {
                        dotGeoJsonSource.setGeoJson(Point.fromLngLat(
                                animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                    }
                }
            };

    /**
     * Add various sources to the map.
     */
    private void initSources(@NonNull Style loadedMapStyle, @NonNull FeatureCollection featureCollection) {
        dotGeoJsonSource = new GeoJsonSource(DOT_SOURCE_ID, featureCollection);
        loadedMapStyle.addSource(dotGeoJsonSource);
        loadedMapStyle.addSource(new GeoJsonSource(LINE_SOURCE_ID, featureCollection));
    }

    /**
     * Add the marker icon SymbolLayer.
     */
    private void initSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("moving-pink-dot", BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.pink_dot)));

        loadedMapStyle.addLayer(new SymbolLayer("symbol-layer-id", DOT_SOURCE_ID).withProperties(
                iconImage("moving-pink-dot"),
                iconSize(1f),
                iconIgnorePlacement(true),
                iconAllowOverlap(true)
        ));
    }

    /**
     * Add the LineLayer for the marker icon's travel route.
     */
    private void initDotLinePath(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new LineLayer("line-layer-id", LINE_SOURCE_ID).withProperties(
                lineColor(Color.parseColor("#F13C6E")),
                lineWidth(4f)
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
// When the activity is resumed we restart the marker animating.
        if (handler != null && runnable != null) {
            handler.post(runnable);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
// Check if the marker is currently animating and if so, we pause the animation so we aren't
// using resources when the activities not in view.
        if (handler != null && runnable != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (markerIconAnimator != null) {
            markerIconAnimator.cancel();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * We want to load in the GeoJSON file asynchronous so the UI thread isn't handling the file
     * loading. The GeoJSON file we are using is stored in the assets folder, you could also get
     * this information from the Mapbox mapboxMap matching API during runtime.
     */
    private static class LoadGeoJson extends AsyncTask<Void, Void, FeatureCollection> {

        private WeakReference<MapboxActivity> weakReference;

        LoadGeoJson(MapboxActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected FeatureCollection doInBackground(Void... voids) {
            try {
                MapboxActivity activity = weakReference.get();
                if (activity != null) {
                    String json = "{\"type\": \"FeatureCollection\",\"features\": [{\"type\": \"Feature\",\"properties\": {},\"geometry\": {\"coordinates\":[],\"type\": \"LineString\"}}]}";
                    JSONObject obj = new JSONObject(json);
                    JSONObject features = (JSONObject) obj.getJSONArray("features").get(0);
                    JSONArray coordinates = features.getJSONObject("geometry").getJSONArray("coordinates");
                    GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(activity);
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();

                    Cursor cursor = db.rawQuery ("SELECT * FROM geo  WHERE CAST(geo.accuracy as decimal) < 10 AND geo.createtime >= datetime('now', '-1 days') order by geo.createtime", null);
                    cursor.moveToFirst();

                    for (int i = 0; i < cursor.getCount(); i++) {

                        cursor.moveToPosition(i);
                        double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitude")));
                        double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("latitude")));

                        coordinates.put(new JSONArray().put(longitude).put(latitude));
                    }
                    Log.d("manar", obj.toString());
                    InputStream inputStream = activity.getAssets().open("matched_route.geojson");
                    return FeatureCollection.fromJson(obj.toString());
                }
            } catch (Exception exception) {
                Log.e("manar", "exception", exception);
            }
            return null;
        }

        static String convertStreamToString(InputStream is) {
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }

        @Override
        protected void onPostExecute(@Nullable FeatureCollection featureCollection) {
            super.onPostExecute(featureCollection);
            MapboxActivity activity = weakReference.get();
            if (activity != null && featureCollection != null) {
                activity.initData(featureCollection);
            }
        }
    }

    /**
     * Method is used to interpolate the SymbolLayer icon animation.
     */
    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };
}