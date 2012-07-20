package com.vasa.Saree3;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.vasa.Saree3.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Map extends MapActivity {
	
	LocationManager locationManager;
    LocationListener locationListener;
    
	String phoneNumber = null;
	MapView mapView;
    List<Overlay> mapOverlays;
    Drawable drawable;
    MyItemizedOverlay itemizedoverlay;
    protected PowerManager.WakeLock mWakeLock;
    double latE6;
    double lonE6;
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    
	    final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        
	    // TODO Auto-generated method stub
	    
	    mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.flag);
        itemizedoverlay = new MyItemizedOverlay(drawable);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        
        final Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true);
        
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 700, 300, locationListener);
        
	    // TODO Auto-generated method stub
	}

	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        return true;
		//return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.invite:
			shareIt();
        	break;
        
		default:
			break;
		}//switch(item.getItemId())
		//return super.onOptionsItemSelected(item);
		return true;
	}
	
	public void shareIt() {
		
		String message = "com and join me:\n";
		message = message + "http://maps.google.com/maps?q=" + latE6 / 1000000;
		message = message + "," + lonE6 / 1000000 + "\n";
		message = message + "this message is sent from saree3";
    	
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "shareing subject");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		this.mWakeLock.release();
		locationManager.removeUpdates(locationListener);
		
		super.onDestroy();
	}


	class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			latE6 = (int) (loc.getLatitude() * 1000000);
    		lonE6 = (int) (loc.getLongitude() * 1000000);
    		//int latE6 = (int) (lat * 1e6);
    		//int lonE6 = (int) (lon * 1e6);
    		
    		GeoPoint point = new GeoPoint((int) latE6, (int) lonE6);
            OverlayItem overlayitem = new OverlayItem(point, null, null);
            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			if(status!=2)
    			((TextView)findViewById(R.id.speed)).setText("No Gps Signal!");
		}
		
	}
}
