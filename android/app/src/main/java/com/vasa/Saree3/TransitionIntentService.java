package com.vasa.Saree3;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;


public class TransitionIntentService extends IntentService {

    public TransitionIntentService() {
        super("TransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                    OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                    status.getSubscriptionStatus().getUserId();
                    GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(getApplicationContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put("playerid", status.getSubscriptionStatus().getUserId());
                    values.put("act_type", event.getActivityType()+"");
                    values.put("transition_type", event.getTransitionType()+"");
                    values.put("elapsed_realtime", event.getElapsedRealTimeNanos()+"");
                    
                    
                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert("activity", null, values);
                    
                    // Toast.makeText(this, event.getTransitionType() + "-" + event.getActivityType(), Toast.LENGTH_LONG).show();
                    //7 for walking and 8 for running
                    Log.i(Constants.TAGS.TAG, "Activity Type " + event.getActivityType());

                    // 0 for enter, 1 for exit
                    Log.i(Constants.TAGS.TAG, "Transition Type " + event.getTransitionType());
                }
            }
        }
    }
}