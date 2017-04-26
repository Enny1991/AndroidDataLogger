package com.eneaceolini.simpledatalogger;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created By Enea Ceolini 2015
 * Contact: enea.ceolini@gmail.com
 */

/**
 * Simple BroadcastReceiver that starts the background service when the phone is turned on and makes
 * sure the alarm for the synchronization is set correctly
 */
public class StartOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Start on boot", "received");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Intent serviceIntent = new Intent(context, ReadingsService.class);
            context.startService(serviceIntent);
        }

    }
}