package com.vasa.Saree3;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;

public class MyNotificationExtender extends NotificationExtenderService {
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
	 //   	OverrideSettings overrideSettings = new OverrideSettings();
	 //   	overrideSettings.extender = new NotificationCompat.Extender() {
	 //   		@Override
	 //        public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
	 //        	// Sets the background notification color to Green on Android 5.0+ devices.
	 //            return builder.setColor(new BigInteger("FF00FF00", 16).intValue());
	 //        }
	 //    };

	 //    OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
		// Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

		Intent startIntent = new Intent(this, MyService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);
	    return true;
	}
}