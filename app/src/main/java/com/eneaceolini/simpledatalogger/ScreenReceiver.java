package com.eneaceolini.simpledatalogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver that receives the event of screenOff/screenOn. In this way the background
 * Services knows when to close the current session and analyse the data and when to start a
 * new recording session.
 * Created By Enea Ceolini 2015
 * Contact: enea.ceolini@gmail.com
 */

public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff;
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }

            Intent i = new Intent(context, ReadingsService.class);
            i.putExtra("screen_state", screenOff);
            context.startService(i);
    }
}
