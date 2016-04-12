package com.codeoregonapp.patrickleonard.tempestatibus.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Current;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;


/**
 * Service to update the widgets as needed
 * Created by Patrick Leonard on 1/3/2016.
 */
public class WidgetForecastUpdateService extends Service {

    public static final String TAG = WidgetForecastUpdateService.class.getSimpleName();
    public static Forecast staticForecast;
    public static String staticAddress;
    private String mAddress;
    private Forecast mForecast;
    private ResultReceiver mReceiver;
    private Intent mIncomingIntent;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;
    private boolean mHasRetrievalError;
    private boolean mIsRunning;
    private boolean mLayoutHasDataTable;
    private boolean mLayoutHasSummary;
    private boolean mLayoutHasDateTime;
    private boolean mLayoutHasAddress;
    private boolean mLayoutHasGridView  ;

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        return mTempestatibusApplicationSettings;
    }

    public void setTempestatibusApplicationSettings(TempestatibusApplicationSettings tempestatibusApplicationSettings) {
        mTempestatibusApplicationSettings = tempestatibusApplicationSettings;
    }

    public boolean hasRetrievalError() {
        return mHasRetrievalError;
    }

    public void setHasRetrievalError(boolean hasRetrievalError) {
        mHasRetrievalError = hasRetrievalError;
    }

    public Intent getIncomingIntent() {
        return mIncomingIntent;
    }

    public void setIncomingIntent(Intent incomingIntent) {
        mIncomingIntent = incomingIntent;
    }

    public ResultReceiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(ResultReceiver receiver) {
        mReceiver = receiver;
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

    @Override
    public void onCreate() {
         setTempestatibusApplicationSettings(new TempestatibusApplicationSettings());
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int ids) {
        if(intent != null) {
            getTempestatibusApplicationSettings().createSharedPreferenceContext(this);
            setIncomingIntent(intent);
            boolean optionChange = getIncomingIntent().getBooleanExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS,false);
            if(!optionChange && !mIsRunning) {
                startForecastRetrievalService();
            }
            else if(optionChange) {
                updateAppWidgets(getIncomingIntent().getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS));
            }
        }
        return super.onStartCommand(intent,flags,ids);
    }

    public void updateAppWidgets(int[] allWidgetIds) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());
        for (int widgetId : allWidgetIds) {
            updateAlertGridViewDataUpdate(widgetId);
            RemoteViews remoteViews;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                remoteViews = getResizableRemoteViews(appWidgetManager, widgetId);
            }
            else {
                remoteViews = getRemoteViews(widgetId);
            }

            if(hasRetrievalError()) {
                displayError(remoteViews);
            }
            else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    remoteViews = updateResizableDynamicWidgetData(remoteViews, widgetId);
                }
                else {
                    remoteViews = updateWidgetData(remoteViews,widgetId);
                }
            }
            remoteViews = registerUpdateOnClickListener(widgetId, remoteViews);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        mIsRunning = false;
    }

    public void updateAlertGridViewDataUpdate(int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] allWidgetIds = {widgetId};
        appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds,R.id.first_widget_daily_grid_view);
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    private RemoteViews getResizableRemoteViews(AppWidgetManager appWidgetManager, int widgetId) {
        // See the dimensions and
        Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
        // Get min width and height.
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        return new RemoteViews(getApplicationContext().getPackageName(),
                getLayoutId(maxHeight,maxWidth,widgetId));
    }

    @NonNull
    private RemoteViews getRemoteViews(int widgetId) {
        return new RemoteViews(getApplicationContext().getPackageName(),
                getLayoutId(-1,-1,widgetId));
    }

    @NonNull
    private RemoteViews updateResizableDynamicWidgetData(RemoteViews remoteViews, int widgetId) {
        Current current = getForecast().getCurrent();
        // Set the text with the new data retrieved
        remoteViews = updateSmallWidgetData(remoteViews,current,widgetId);
        if(mLayoutHasSummary) {
            remoteViews = updateSummaryData(remoteViews, current);
        }
        if(mLayoutHasAddress) {
            remoteViews = updateAddressData(remoteViews);
        }
        if(mLayoutHasDateTime) {
            remoteViews = updateDateTimeData(remoteViews, current);
        }
        if(mLayoutHasDataTable) {
            remoteViews = updateDataTableData(remoteViews, current);
        }
        if(mLayoutHasGridView) {
            remoteViews = prepareDailyGrid(remoteViews, widgetId);
        }
        return remoteViews;
    }

    private RemoteViews updateWidgetData(RemoteViews remoteViews,int widgetId) {
        Current current = getForecast().getCurrent();
        remoteViews.setTextViewText(R.id.firstWidgetTemperatureValue, current.getTemperature());
        remoteViews.setImageViewResource(R.id.firstWidgetTemperatureDegreeSymbol, TempestatibusApplicationSettings
                .getSmallDegreeId(getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId)));
        remoteViews.setImageViewResource(R.id.iconImageView, current.getIconId(getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId), this));
        remoteViews.setTextViewText(R.id.firstWidgetAddressValue, getAddress());
        remoteViews.setTextViewText(R.id.firstWidgetDateTimeValue, current.getFormattedDayAndTime());
        remoteViews.setTextViewText(R.id.firstWidgetSummaryValue, current.getSummary());
        remoteViews.setTextViewText(R.id.firstWidgetPrecipitationProbabilityValue, current.getPrecipitationProbability());
        remoteViews.setTextViewText(R.id.firstWidgetWindSpeedValue,current.getWindSpeed());
        remoteViews.setTextViewText(R.id.firstWidgetHumidityValue, current.getHumidity());
        remoteViews.setTextViewText(R.id.timeUntilPrecipValue, getForecast().getTimeUntilPrecipitation());
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            remoteViews = changeWindSpeedUnitsToSI(remoteViews); //Layout defaults are US units, change to SI
            remoteViews = changeTempUnitsToSI(remoteViews); //Layout defaults are US units, change to SI
        }
        return remoteViews;
    }

    private RemoteViews updateSmallWidgetData(RemoteViews remoteViews, Current current,int widgetId) {
        remoteViews.setTextViewText(R.id.firstWidgetTemperatureValue, current.getTemperature());
        remoteViews.setImageViewResource(R.id.iconImageView, current.getIconId(getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId), this));
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            remoteViews = changeTempUnitsToSI(remoteViews); //Layout defaults are US units, change to SI
        }
        return remoteViews;
    }

    private RemoteViews updateAddressData(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.firstWidgetAddressValue, getAddress());
        return remoteViews;
    }

    private RemoteViews updateDateTimeData(RemoteViews remoteViews, Current current) {
        remoteViews.setTextViewText(R.id.firstWidgetDateTimeValue, current.getFormattedDayAndTime());
        return remoteViews;
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
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            remoteViews = changeWindSpeedUnitsToSI(remoteViews); //Layout defaults are US units, change to SI
        }
        return remoteViews;
    }

    private RemoteViews changeWindSpeedUnitsToSI(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.firstWidgetWindSpeedUnits, getString(R.string.si_units_kilometers_per_hour));
        return remoteViews;
    }

    private RemoteViews changeTempUnitsToSI(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.firstWidgetTemperatureUnits, getString(R.string.si_units_celsius));
        return remoteViews;
    }

    @NonNull
    public RemoteViews displayError(RemoteViews remoteViews) {
        // Set the an error message inside summary TextView
        remoteViews.setTextViewText(R.id.firstWidgetSummaryValue,
                getApplicationContext().getString(R.string.error_message));
        return remoteViews;
    }

    private RemoteViews registerUpdateOnClickListener(int widgetId, RemoteViews remoteViews) {
        // Register an onClickListener
        Intent clickIntent = new Intent(this.getApplicationContext(),
                TempestatibusSmallWidgetProvider.class);
        clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] allWidgetIds = {widgetId};
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                allWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.first_widget_outer_relative_layout, pendingIntent);
        return remoteViews;
    }

    private RemoteViews prepareDailyGrid(RemoteViews remoteViews,int widgetId) {
        Intent intent = new Intent(getApplicationContext(),DailyGridViewRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        Bundle forecastBundle = new Bundle();
        forecastBundle.putParcelable(Forecast.class.getSimpleName(),getForecast());
        intent.putExtra("ForecastBundle", forecastBundle);
        intent.putExtra(MainActivity.ADDRESS_EXTRA,getAddress());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                remoteViews.setRemoteAdapter(R.id.first_widget_daily_grid_view, intent);
        remoteViews.setEmptyView(R.id.first_widget_daily_grid_view, R.id.first_widget_daily_grid_empty_view);
        Intent dailyActivityIntent = new Intent(getApplicationContext(), TempestatibusLargeWidgetProvider.class);
        dailyActivityIntent.setAction(TempestatibusLargeWidgetProvider.DAY_ITEM_CLICK_ACTION);
        dailyActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        dailyActivityIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, dailyActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.first_widget_daily_grid_view,pendingIntent);
        return remoteViews;
    }

    private int getLayoutId(int maxHeight, int maxWidth, int widgetId) {
        int heightCells = getCells(maxHeight);
        int widthCells = getCells(maxWidth);
        mLayoutHasDataTable = false;
        mLayoutHasDateTime = false;
        mLayoutHasSummary = false;
        mLayoutHasAddress = false;
        mLayoutHasGridView = false;
        switch(heightCells) {
            case 1: {
                switch (widthCells) {
                    case 2: {
                        return getLayout2x1Id(widgetId);
                    }
                    case 3: {
                        mLayoutHasSummary = true;
                        return getLayout3x1Id(widgetId);
                    }
                    default: {
                        mLayoutHasSummary = true;
                        mLayoutHasDataTable = true;
                        return getLayout4x1Id(widgetId);
                    }
                }
            }
            case 2: {
                switch (widthCells) {
                    case 2: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasDataTable = true;
                        return getLayout2x2Id(widgetId);
                    }
                    default: {
                        mLayoutHasAddress = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasSummary = true;
                        mLayoutHasDataTable = true;
                        return getLayout3x2Id(widgetId);
                    }
                }
            }
            case 3: {
                switch (widthCells) {
                    case 2: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasDataTable = true;
                        return getLayout2x2Id(widgetId);
                    }
                    case 3: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasAddress = true;
                        mLayoutHasDataTable = true;
                        return getLayout3x3Id(widgetId);
                    }
                    default: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasAddress = true;
                        mLayoutHasDataTable = true;
                        mLayoutHasGridView = true;
                        return getLayout4x3Id(widgetId);
                    }
                }
            }
            default: {
                switch (widthCells) {
                    case 2: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        return getLayout2x2Id(widgetId);
                    }
                    case 3: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasAddress = true;
                        mLayoutHasDataTable = true;
                        return getLayout3x3Id(widgetId);
                    }
                    default: {
                        mLayoutHasSummary = true;
                        mLayoutHasDateTime = true;
                        mLayoutHasAddress = true;
                        mLayoutHasDataTable = true;
                        mLayoutHasGridView = true;
                        return getLayout4x3Id(widgetId);
                    }
                }
            }
        }
    }

    private int getCells(int pixels) {
        return (int)Math.floor((pixels+30)/90);
    }

    private int getLayout2x1Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_2x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_2x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_2x1_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_2x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_2x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_2x1_widget_layout;
            }
            default: return R.layout.summer_2x1_widget_layout;
        }
    }
    
    private int getLayout2x2Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_2x2_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_2x2_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_2x2_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_2x2_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_2x2_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_2x2_widget_layout;
            }
            default: return R.layout.summer_2x2_widget_layout;
        }
    }
    
    private int getLayout3x1Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_3x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_3x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_3x1_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_3x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_3x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_3x1_widget_layout;
            }
            default: return R.layout.summer_3x1_widget_layout;
        }
    }


    private int getLayout4x1Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_4x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_4x1_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_4x1_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_4x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_4x1_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_4x1_widget_layout;
            }
            default: return R.layout.summer_4x1_widget_layout;
        }
    }
    
    private int getLayout3x2Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_3x2_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_3x2_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_3x2_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_3x2_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_3x2_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_3x2_widget_layout;
            }
            default: return R.layout.summer_3x2_widget_layout;
        }
    }
    
    private int getLayout3x3Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_3x3_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_3x3_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_3x3_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_3x3_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_3x3_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_3x3_widget_layout;
            }
            default: return R.layout.summer_3x3_widget_layout;
        }
    }
    
    private int getLayout4x3Id(int widgetId) {
        String currentTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_4x3_widget_layout;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_4x3_widget_layout;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_4x3_widget_layout;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_4x3_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_4x3_widget_layout;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_4x3_widget_layout;
            }
            default: return R.layout.summer_4x3_widget_layout;
        }
    }

    protected void startForecastRetrievalService() {
        Intent intent = new Intent(this, ForecastRetrievalService.class);
        ForecastRetrievalServiceWidgetProviderResultReceiver resultReceiver = new ForecastRetrievalServiceWidgetProviderResultReceiver(new Handler(), this);
        intent.putExtra(ForecastRetrievalServiceConstants.WIDGET_RECEIVER, resultReceiver);
        intent.putExtra(ForecastRetrievalServiceConstants.CALLING_CLASS_NAME_DATA_EXTRA,
                WidgetForecastUpdateService.TAG);
        mIsRunning = true;
        startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
