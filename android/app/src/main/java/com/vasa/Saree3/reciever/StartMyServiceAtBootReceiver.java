package com.vasa.Saree3.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vasa.Saree3.Constants;
import com.vasa.Saree3.MyService;
import com.vasa.Saree3.MyService2;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent startIntent = new Intent(context, MyService.class);
	  		startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
			context.startService(startIntent);
        }
    }
}
