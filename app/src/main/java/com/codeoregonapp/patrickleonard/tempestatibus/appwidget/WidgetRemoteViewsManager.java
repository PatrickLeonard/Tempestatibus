package com.codeoregonapp.patrickleonard.tempestatibus.appwidget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Current;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class manages the assignment of data to remote views, maintains the RemoteViews SparseArray,
 * Utilizes the TempestatibusApplicationSettings and DynamicWidgetLayoutController classes to dynamically
 * maintain the theme and layout of the app widgets on the home screen.
 * Created by Patrick Leonard on 8/14/2016.
 */
public class WidgetRemoteViewsManager {
    
    public static final String TAG = WidgetRemoteViewsManager.class.getSimpleName();
    private Context mContext;
    private String mAddress;
    private Forecast mForecast;
    private DynamicWidgetLayoutController mDynamicWidgetLayoutController;
    private SparseArray<RemoteViews> mRemoteViewsArray;

    public DynamicWidgetLayoutController getDynamicWidgetLayoutController() {
        if(mDynamicWidgetLayoutController == null) {
            setDynamicWidgetLayoutController(new DynamicWidgetLayoutController(mContext));
        }
        return mDynamicWidgetLayoutController;
    }

    public void setDynamicWidgetLayoutController(DynamicWidgetLayoutController dynamicWidgetLayoutController) {
        mDynamicWidgetLayoutController = dynamicWidgetLayoutController;
    }

    public Forecast getForecast() {
        return mForecast;
    }

    public void setForecast(Forecast forecast) {
        mForecast = forecast;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public SparseArray<RemoteViews> getRemoteViewsArray() {
        return mRemoteViewsArray;
    }

    public WidgetRemoteViewsManager(Context context) {
        mContext = context; mRemoteViewsArray = new SparseArray<>();
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().createSharedPreferenceContext(mContext);
    }

    @NonNull
    public RemoteViews getRemoteViews(AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews remoteViews;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            remoteViews = getResizableRemoteViews(appWidgetManager, widgetId);
        }
        else {
            remoteViews = getStaticRemoteViews(widgetId);
        }
        return remoteViews;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    private RemoteViews getResizableRemoteViews(AppWidgetManager appWidgetManager, int widgetId) {
        // See the dimensions and
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        // Get min width and height.
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int layoutId = getDynamicWidgetLayoutController()
                .setWidgetInformation(widgetId,maxHeight,maxWidth)
                .getLayoutId();
        return new RemoteViews(mContext.getPackageName(),
                layoutId);
    }

    @NonNull
    private RemoteViews getStaticRemoteViews(int widgetId) {
        int height = -1;
        int width = -1;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        String providerClassName = appWidgetManager.getAppWidgetInfo(widgetId).provider.getClassName();
        if(providerClassName.equals(TempestatibusSmallWidgetProvider.NAME)) {
            height = TempestatibusSmallWidgetProvider.CELL_HEIGHT;
            width = TempestatibusSmallWidgetProvider.CELL_WIDTH;
        }
        else if(providerClassName.equals(TempestatibusMediumWidgetProvider.NAME)) {
            height = TempestatibusMediumWidgetProvider.CELL_HEIGHT;
            width = TempestatibusMediumWidgetProvider.CELL_WIDTH;
        }
        else if(providerClassName.equals(TempestatibusLargeWidgetProvider.NAME)) {
            height = TempestatibusLargeWidgetProvider.CELL_HEIGHT;
            width = TempestatibusLargeWidgetProvider.CELL_WIDTH;
        }
        else {
            //Do nothing
        }
        int layoutId = getDynamicWidgetLayoutController()
                .setWidgetInformation(widgetId,height,width)
                .getLayoutId();
        return new RemoteViews(mContext.getPackageName(),
                layoutId);
    }

    @NonNull
    public RemoteViews toggleLoadingLayoutVisibilities(RemoteViews remoteViews, int widgetId) {
            Log.d(WidgetForecastUpdateService.TAG, "WidgetID should have the progress bar visible: " + widgetId);
            Log.d(WidgetForecastUpdateService.TAG, "Spinner should now be visible.");
            remoteViews.setViewVisibility(R.id.loadingLayout, View.GONE);
            remoteViews.setViewVisibility(R.id.first_widget_outer_relative_layout, View.VISIBLE);
            return remoteViews;
    }


    @NonNull
    public RemoteViews updateResizableDynamicWidgetData(RemoteViews remoteViews, int widgetId) {
        Current current = getForecast().getCurrent();
        // Set the text with the new data retrieved
        remoteViews = updateSmallWidgetData(remoteViews,current,widgetId);
        if(getDynamicWidgetLayoutController().getHasSummary()) {
            remoteViews = updateSummaryData(remoteViews, current);
        }
        if(getDynamicWidgetLayoutController().getHasAddress()) {
            remoteViews = updateAddressData(remoteViews);
        }
        if(getDynamicWidgetLayoutController().getHasDateTime()) {
            remoteViews = updateDateTimeData(remoteViews);
        }
        if(getDynamicWidgetLayoutController().getHasDataTable()) {
            remoteViews = updateDataTableData(remoteViews, current);
        }
        if(getDynamicWidgetLayoutController().getHasGridView()) {
            remoteViews = prepareDailyGrid(remoteViews, widgetId);
        }
        return remoteViews;
    }

    public void alertGridViewDataUpdate(int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] allWidgetIds = {widgetId};
        appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds,R.id.first_widget_daily_grid_view);
    }

    private RemoteViews updateSmallWidgetData(RemoteViews remoteViews, Current current,int widgetId) {
        remoteViews.setTextViewText(R.id.firstWidgetTemperatureValue, current.getTemperature());
        remoteViews.setTextViewText(R.id.firstWidgetTemperatureUnits, mContext.getString(getForecast().getTemperatureUnitsId()));
        remoteViews.setImageViewResource(R.id.iconImageView, current.getIconId(getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId), mContext));
        remoteViews.setViewVisibility(R.id.progressBarLayout, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.iconImageView,View.VISIBLE);
        Log.d(WidgetRemoteViewsManager.TAG,"WidgetID should have the progress bar invisible: " + widgetId);
        Log.v(WidgetRemoteViewsManager.TAG,"Spinner should now be invisible");
        return remoteViews;
    }

    private RemoteViews updateAddressData(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.firstWidgetAddressValue, getAddress());
        return remoteViews;
    }

    public RemoteViews updateDateTimeData(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.firstWidgetDateTimeValue,getFormattedDayAndTime());
        return remoteViews;
    }

    private String getFormattedDayAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, h:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private RemoteViews updateSummaryData(RemoteViews remoteViews, Current current) {
        remoteViews.setTextViewText(R.id.firstWidgetSummaryValue, current.getSummary());
        return remoteViews;
    }

    private RemoteViews updateDataTableData(RemoteViews remoteViews, Current current) {
        remoteViews.setTextViewText(R.id.firstWidgetPrecipitationProbabilityValue, current.getPrecipitationProbability());
        remoteViews.setTextViewText(R.id.firstWidgetWindSpeedValue,current.getWindSpeed());
        remoteViews.setTextViewText(R.id.firstWidgetHumidityValue, current.getHumidity());
        remoteViews.setTextViewText(R.id.timeUntilPrecipValue, getForecast().getTimeUntilPrecipitation());
        remoteViews.setTextViewText(R.id.firstWidgetWindSpeedUnits,mContext.getString(getForecast().getVelocityUnitsId()));
        return remoteViews;
    }

    @NonNull
    public RemoteViews displayError(RemoteViews remoteViews) {
        // Set the an error message inside summary TextView
        remoteViews.setTextViewText(R.id.firstWidgetSummaryValue,
                mContext.getString(R.string.error_message));
        remoteViews.setViewVisibility(R.id.progressBarLayout, View.GONE);
        Log.v(WidgetRemoteViewsManager.TAG,"Spinner should now be invisible");
        return remoteViews;
    }

    public RemoteViews registerUpdateOnClickListener(int widgetId, RemoteViews remoteViews) {
        // Register an onClickListener to update the widgets using this service
        Intent clickIntent = new Intent(mContext,WidgetForecastUpdateService.class);
        int[] allWidgetIds = {widgetId};
        Log.d(WidgetRemoteViewsManager.TAG,"WidgetID having listener registered: " + widgetId);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                allWidgetIds);
        clickIntent.putExtra(WidgetForecastUpdateServiceConstants.PROVIDER_UPDATE_REQUEST,false);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.first_widget_outer_relative_layout, pendingIntent);
        return remoteViews;
    }

    @NonNull
    private Intent getProviderIntent(int widgetId) {
        Intent clickIntent;AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        String providerClassName = appWidgetManager.getAppWidgetInfo(widgetId).provider.getClassName();
        if(providerClassName.equals(TempestatibusSmallWidgetProvider.NAME)) {
            clickIntent = new Intent(mContext,TempestatibusSmallWidgetProvider.class);
        }
        else if(providerClassName.equals(TempestatibusMediumWidgetProvider.NAME)) {
            clickIntent = new Intent(mContext,TempestatibusMediumWidgetProvider.class);
        }
        else if(providerClassName.equals(TempestatibusLargeWidgetProvider.NAME)) {
            clickIntent = new Intent(mContext,TempestatibusLargeWidgetProvider.class);
        }
        else {
            clickIntent = new Intent(mContext,TempestatibusSmallWidgetProvider.class);
        }
        return clickIntent;
    }

    public RemoteViews registerPoweredByForecastOnClickListener(int widgetId, RemoteViews remoteViews) {
        // Register an onClickListener
        //Create an intent to open the default browser and travel to http://forecast.io
        Log.d(WidgetRemoteViewsManager.TAG,"Registering Powered by Forecast listener");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.forecast_io_url)));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, widgetId, browserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.poweredByForecast, pendingIntent);
        return remoteViews;
    }

    public RemoteViews registerTempestatibusOnClickListener(int widgetId, RemoteViews remoteViews) {
        // Register an onClickListener
        //Create an intent to open the default browser and travel to http://forecast.io
        Log.d(WidgetRemoteViewsManager.TAG,"Registering Tempestatibus onClickListener");
        Intent appIntent = new Intent(mContext, MainActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, widgetId, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tempestatibus, pendingIntent);
        return remoteViews;
    }

    private RemoteViews prepareDailyGrid(RemoteViews remoteViews,int widgetId) {
        Intent intent = new Intent(mContext,DailyGridViewRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        Bundle forecastBundle = new Bundle();
        forecastBundle.putParcelable(Forecast.class.getSimpleName(),getForecast());
        intent.putExtra("ForecastBundle", forecastBundle);
        intent.putExtra(MainActivity.ADDRESS_EXTRA,getAddress());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.first_widget_daily_grid_view, intent);
        remoteViews.setEmptyView(R.id.first_widget_daily_grid_view, R.id.first_widget_daily_grid_empty_view);
        Intent dailyActivityIntent = getProviderIntent(widgetId);
        dailyActivityIntent.setAction(TempestatibusLargeWidgetProvider.DAY_ITEM_CLICK_ACTION);
        dailyActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        dailyActivityIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, dailyActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.first_widget_daily_grid_view,pendingIntent);
        return remoteViews;
    }

    public void deleteWidget(int widgetId) {
        getRemoteViewsArray().remove(widgetId);
        Log.d(WidgetRemoteViewsManager.TAG, "RemoteViews Map size: " + mRemoteViewsArray.size());
        Log.d(WidgetRemoteViewsManager.TAG, "WidgetId: " + widgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetThemePreference(widgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetConfigPreference(widgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetDisplayPreference(widgetId);

    }

    public void restoreWidget(int newWidgetId, int oldWidgetId) {
        RemoteViews remoteViews = getRemoteViewsArray().get(oldWidgetId);
        getRemoteViewsArray().remove(oldWidgetId);
        getRemoteViewsArray().put(newWidgetId,remoteViews);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings()
                .setWidgetThemePreference(getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().getWidgetThemePreference(oldWidgetId), newWidgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings()
                .setWidgetConfigPreference(newWidgetId,false);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings()
                .setWidgetDisplayPreference(newWidgetId,false);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetThemePreference(oldWidgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetConfigPreference(oldWidgetId);
        getDynamicWidgetLayoutController().getTempestatibusApplicationSettings().removeWidgetDisplayPreference(oldWidgetId);
    }
}
