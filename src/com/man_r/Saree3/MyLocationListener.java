package com.man_r.Saree3;

import com.man_r.Saree3.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocationListener extends Activity implements LocationListener {

	String maxLat="";
	String maxLong = "";
	
	int maxSpeed=0;
	int speed=0;
	
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		if(loc.hasSpeed()){
			
			speed = (int) (loc.getSpeed()* 3.6);
			if(speed>maxSpeed){
				maxSpeed=speed;
				maxLat = "" + loc.getLatitude();
				maxLong = "" + loc.getLongitude();
				((TextView)findViewById(R.id.maxlatitude)).setText("maxlatitude: " + maxLat);
				((TextView)findViewById(R.id.maxlongitude)).setText("maxlongitude: " + maxLong);
			}//if(speed>maxSpeed)
			
			if (speed == 0)
				((TextView)findViewById(R.id.speed)).setText("000");
			else if (speed < 10)
				((TextView)findViewById(R.id.speed)).setText("00" + speed);
			else if (speed < 100)
				((TextView)findViewById(R.id.speed)).setText("0" + speed);
			else
				((TextView)findViewById(R.id.speed)).setText(speed);
			
			((TextView)findViewById(R.id.maxSpeed)).setText("max= " + maxSpeed + " Km/h");
			
			
			
			
			//Get a reference to the NotificationManager:
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
			
			Notification notification = new Notification();
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			notification.number = speed;
			notification.icon = R.drawable.flag;
			
			//Define the notification message and PendingIntent
			CharSequence contentTitle = "BatteryInfo";
			CharSequence contentText = "maxSpeed= " + maxSpeed;
			Intent notificationIntent = new Intent(this, Saree3.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			
			notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
			
			//Pass the Notification to the NotificationManager
			mNotificationManager.notify(3, notification);
			
		}//if(loc.hasSpeed())
		
		else{
			((TextView)findViewById(R.id.maxSpeed)).setText("No Signal !");
    		((TextView)findViewById(R.id.latitude)).setText("latitude: " + loc.getLatitude());
			((TextView)findViewById(R.id.longitude)).setText("longitude: " + loc.getLongitude());
		}//else
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
			((TextView)findViewById(R.id.maxSpeed)).setText("No Gps !");
	}

}
