package com.codeoregonapp.patrickleonard.tempestatibus.appwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * Service to update the widgets as needed
 * Created by Patrick Leonard on 1/3/2016.
 */
public class WidgetForecastUpdateService extends Service {

    public static final String TAG = WidgetForecastUpdateService.class.getSimpleName();
    public static Forecast staticForecast;
    public static String staticAddress;
    public static final String DELETE_WIDGET = ".widget.WidgetForecastUpdateService.DELETE_WIDGET";
    public static final String RESTORE_WIDGET = ".widget.WidgetForecastUpdateService.RESTORE_WIDGET";
    public static final String OLD_WIDGET_IDS = ".widget.WidgetForecastUpdateService.OLD_WIDGET_IDS";
    private ResultReceiver mReceiver;
    private WidgetRemoteViewsManager mWidgetRemoteViewsManager;
    private boolean mHasRetrievalError;
    private boolean mIsRunning;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;
    private BroadcastReceiver mTimeTickReceiver;

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            setTempestatibusApplicationSettings(new TempestatibusApplicationSettings());
        }
        return mTempestatibusApplicationSettings;
    }

    public void setTempestatibusApplicationSettings(TempestatibusApplicationSettings tempestatibusApplicationSettings) {
        mTempestatibusApplicationSettings = tempestatibusApplicationSettings;
    }

    public WidgetRemoteViewsManager getRemoteViewsManager() {
        if(mWidgetRemoteViewsManager == null) {
            setRemoteViewsManager(new WidgetRemoteViewsManager(this.getApplicationContext()));
        }
        return mWidgetRemoteViewsManager;
    }

    public void setRemoteViewsManager(WidgetRemoteViewsManager widgetRemoteViewsManager) {
        mWidgetRemoteViewsManager = widgetRemoteViewsManager;
    }

    public boolean hasRetrievalError() {
        return mHasRetrievalError;
    }

    public void setHasRetrievalError(boolean hasRetrievalError) {
        mHasRetrievalError = hasRetrievalError;
    }

    public ResultReceiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(ResultReceiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void onCreate() {
         mIsRunning = false;
        getTempestatibusApplicationSettings().createSharedPreferenceContext(getApplicationContext());
        startTimeTicketReceiver();
    }

    @Override
    public void onDestroy() {
        if (mTimeTickReceiver != null) {
            getApplicationContext().unregisterReceiver(mTimeTickReceiver);
        }
    }


    @Override
    public int onStartCommand(Intent intent,int flags, int ids) {
        int[] appWidgetIds;
        if(intent != null) {
            appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if(appWidgetIds != null) {
                boolean deleteWidget = intent.getBooleanExtra(WidgetForecastUpdateService.DELETE_WIDGET,false);
                boolean restoreWidget = intent.getBooleanExtra(WidgetForecastUpdateService.RESTORE_WIDGET,false);
                boolean optionChange = intent.getBooleanExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS,false);
                Log.d(WidgetForecastUpdateService.TAG,"RemoteViews Map size: " + getRemoteViewsManager().getRemoteViewsArray().size());
                if(deleteWidget) {
                    for(int widgetId : appWidgetIds) {
                       getRemoteViewsManager().deleteWidget(widgetId);
                    }
                }
                else if(restoreWidget) {
                    int[] oldWidgetIds = intent.getIntArrayExtra(WidgetForecastUpdateService.OLD_WIDGET_IDS);
                    for(int i=0;i<oldWidgetIds.length;++i) {
                        getRemoteViewsManager().restoreWidget(appWidgetIds[i], oldWidgetIds[i]);
                    }
                }
                else if (!optionChange && !mIsRunning) {
                    runUpdateService();
                } else if (optionChange) {
                    if(getRemoteViewsManager().getForecast() != null) {
                        updateAppWidgets(appWidgetIds);
                    }
                    else {
                        runUpdateService();
                    }
                }
            }
        }
        return Service.START_STICKY_COMPATIBILITY;
    }

    private void runUpdateService() {
        mIsRunning = true;
        startIndeterminateProgressBar(getApplicationContext());
        startForecastRetrievalService();
    }

    private void startIndeterminateProgressBar(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = getAllAppWidgetIdsFromAllProviders();
        for(int widgetId : appWidgetIds) {
            boolean configured = getTempestatibusApplicationSettings().getWidgetConfigPreference(widgetId);
            if(configured) {
                RemoteViews remoteViews = getRemoteViewsManager().getRemoteViewsArray().get(widgetId);
                if (remoteViews != null) {
                    Log.d(WidgetForecastUpdateService.TAG, "App Widget IDs Length: " + appWidgetIds.length);
                    Log.d(WidgetForecastUpdateService.TAG, "WidgetID should have the progress bar visible: " + widgetId);
                    Log.d(WidgetForecastUpdateService.TAG, "Spinner should now be visible.");
                    remoteViews.setViewVisibility(R.id.progressBarLayout, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.iconImageView, View.INVISIBLE);
                    appWidgetManager.updateAppWidget(widgetId, remoteViews);
                }
            }
        }
    }

    private void startTimeTicketReceiver() {
        mTimeTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = getAllAppWidgetIdsFromAllProviders();
                    for(int widgetId : appWidgetIds) {
                        boolean configured = getTempestatibusApplicationSettings().getWidgetConfigPreference(widgetId);
                        if(configured) {
                            RemoteViews remoteViews = getRemoteViewsManager().getRemoteViews(appWidgetManager, widgetId);
                            if(getRemoteViewsManager().getDynamicWidgetLayoutController().getHasDateTime()) {
                                remoteViews = getRemoteViewsManager().updateDateTimeData(remoteViews);
                                getRemoteViewsManager().getRemoteViewsArray().put(widgetId,remoteViews);
                                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                            }
                        }
                    }
                }
            }
        };
        getApplicationContext().registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void updateAppWidgets(int[] allWidgetIds) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());
        for (int widgetId : allWidgetIds) {
            boolean configured = getTempestatibusApplicationSettings().getWidgetConfigPreference(widgetId);
            if(configured) {
                RemoteViews remoteViews = getRemoteViewsManager().getRemoteViews(appWidgetManager, widgetId);
                remoteViews = manageRemoteViews(widgetId, remoteViews);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }
        }
        mIsRunning = false;
    }

    private RemoteViews manageRemoteViews(int widgetId, RemoteViews remoteViews) {
        if(hasRetrievalError()) {
            remoteViews = getRemoteViewsManager().displayError(remoteViews);
        }
        else {
                remoteViews = getRemoteViewsManager().updateResizableDynamicWidgetData(remoteViews, widgetId);
        }
        remoteViews = getRemoteViewsManager().registerUpdateOnClickListener(widgetId, remoteViews);
        remoteViews = getRemoteViewsManager().registerPoweredByForecastOnClickListener(widgetId,remoteViews);
        remoteViews = getRemoteViewsManager().registerTempestatibusOnClickListener(widgetId,remoteViews);
        if(getRemoteViewsManager().getDynamicWidgetLayoutController().getHasGridView()) {
            getRemoteViewsManager().alertGridViewDataUpdate(widgetId);
        }
        getRemoteViewsManager().getRemoteViewsArray().put(widgetId,remoteViews);
        return remoteViews;
    }


    public int[] getAllAppWidgetIdsFromAllProviders() {
        ComponentName smallWidgets = new ComponentName(getApplicationContext(),
                TempestatibusSmallWidgetProvider.class);
        int[] smallWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(smallWidgets);
        ComponentName medWidgets = new ComponentName(getApplicationContext(),
                TempestatibusMediumWidgetProvider.class);
        int[] medWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(medWidgets);
        ComponentName largeWidgets = new ComponentName(getApplicationContext(),
                TempestatibusLargeWidgetProvider.class);
        int[] largeWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(largeWidgets);
        int[] allWidgetIds = new int[smallWidgetIds.length+medWidgetIds.length+largeWidgetIds.length];
        System.arraycopy(smallWidgetIds,0,allWidgetIds,0,smallWidgetIds.length);
        System.arraycopy(medWidgetIds,0,allWidgetIds,smallWidgetIds.length,medWidgetIds.length);
        System.arraycopy(largeWidgetIds,0,allWidgetIds,smallWidgetIds.length+medWidgetIds.length,largeWidgetIds.length);
        return allWidgetIds;
    }

    protected void startForecastRetrievalService() {
        Intent intent = new Intent(this, ForecastRetrievalService.class);
        ForecastRetrievalServiceWidgetProviderResultReceiver resultReceiver = new ForecastRetrievalServiceWidgetProviderResultReceiver(new Handler(), this);
        intent.putExtra(ForecastRetrievalServiceConstants.WIDGET_RECEIVER, resultReceiver);
        intent.putExtra(ForecastRetrievalServiceConstants.CALLING_CLASS_NAME_DATA_EXTRA,
                WidgetForecastUpdateService.TAG);
        startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}