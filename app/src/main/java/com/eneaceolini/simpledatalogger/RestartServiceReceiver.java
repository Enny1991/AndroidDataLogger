package com.eneaceolini.simpledatalogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This BroadcastReceiver makes sure that the service in the background is sticky, in other words
 it relaunches it in case of ANRs, stop from the OS or stop from the user.
 * Created By Enea Ceolini 2015
 * Contact: enea.ceolini@gmail.com
 */

public class RestartServiceReceiver extends BroadcastReceiver
{
    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), ReadingsService.class));
    }
}
