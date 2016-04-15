package com.codeoregonapp.patrickleonard.tempestatibus.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.codeoregonapp.patrickleonard.tempestatibus.ui.DailyForecastActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Day;

/**
 * App widget provider for the first widget of this application
 * Created by Patrick Leonard on 1/2/2016.
 */
public class TempestatibusMediumWidgetProvider extends AppWidgetProvider {
    public static final String TAG = TempestatibusMediumWidgetProvider.class.getSimpleName();
    public static final String DAY_ITEM_CLICK_ACTION = ".widget.Tempestatibus.DAY_ITEM_CLICK_ACTION";
    public static final String DAY_BUNDLE = ".widget.Tempestatibus.DAY_BUNDLE";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                WidgetForecastUpdateService.class);
        // Custom Extra Boolean to signify NOT an option change
        intent.putExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, false);
        // Update the widgets via the service
        context.startService(intent);
    }

    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager,
                                           int appWidgetId, Bundle newOptions) {
        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                WidgetForecastUpdateService.class);
        int[] widgetIds = {appWidgetId};
        //Only need to update the widget that has had it's options changed/resized
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        // Custom Extra Boolean to signify an option change
        intent.putExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, true);
        // Update the widgets via the service
        context.startService(intent);
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is DAY_ITEM_CLICK_ACTION. If it is
    // the DailyForecastActivity should begin.
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DAY_ITEM_CLICK_ACTION)) {
            //Get Day class from a Bundle to work around NoClassDefFound Exception
            Bundle dayBundle = intent.getBundleExtra(TempestatibusMediumWidgetProvider.DAY_BUNDLE);
            dayBundle.setClassLoader(Day.class.getClassLoader());
            Day clickedDay =  dayBundle.getParcelable(Day.TAG);
            //Don't forget the address
            String address = intent.getStringExtra(MainActivity.ADDRESS_EXTRA);
            Intent dayIntent = new Intent(context, DailyForecastActivity.class);
            dayIntent.putExtra(MainActivity.ADDRESS_EXTRA, address);
            dayIntent.putExtra(MainActivity.DAILY_FORECAST_PARCEL, clickedDay);
            dayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start the activity as a "New Task" not connected to the "parent" MainActivity
            context.startActivity(dayIntent);
        }
        super.onReceive(context, intent);
    }
}