package com.codeoregonapp.patrickleonard.tempestatibus.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver to catch the ACTION_SHUTDOWN event for when the phone 
 * is turned off. 
 * Created by Patrick Leonard on 8/23/2016.
 */
public class WidgetShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            Log.d(WidgetForecastUpdateService.TAG, "Shutting Down, calling onDestroy in WidgetUpdateService");
            context.stopService(new Intent(context,WidgetForecastUpdateService.class));
        }
    }


}
