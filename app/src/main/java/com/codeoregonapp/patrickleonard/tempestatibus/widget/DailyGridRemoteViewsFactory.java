package com.codeoregonapp.patrickleonard.tempestatibus.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Day;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

import java.util.List;

/**
 * This class creates the view items for the GridView in the app widget
 * Created by Patrick Leonard on 1/10/2016.
 */
public class DailyGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public final String TAG =  DailyGridRemoteViewsFactory.class.getSimpleName();
    private List<Day> mDays;
    private String mTheme;
    private Context mContext;
    private String mAddress;
    private int mAppWidgetId;

    public DailyGridRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        return (mDays==null ?  0 : mDays.size() );
    }

    @Override
    public RemoteViews getViewAt(int position) {
        int layoutId = getLayoutId();
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), layoutId);
        Day day =  mDays.get(position);
        remoteViews.setImageViewResource(R.id.dailyGridIconImageView, day.getIconId(mTheme, mContext));
        remoteViews.setImageViewResource(R.id.dailyGridMMaxTempSmallDegreeImageView, TempestatibusApplicationSettings.getSmallDegreeId(mTheme));
        remoteViews.setImageViewResource(R.id.dailyGridMMinTempSmallDegreeImageView, TempestatibusApplicationSettings.getSmallDegreeId(mTheme));
        remoteViews.setTextViewText(R.id.dailyGridMaxTemperatureLabel, String.format("%s", day.getTemperatureMax()));
        remoteViews.setTextViewText(R.id.dailyGridMinTemperatureLabel, String.format("%s", day.getTemperatureMin()));
        remoteViews.setTextViewText(R.id.dayAbbreviationLabel, String.format("%s", day.getDayOfTheWeekAbbreviation()));
        //onclick Intent for this Grid View Item to open up the DailyForecastActivity with that Day's data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Day.TAG,day);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(TempestatibusSmallWidgetProvider.DAY_BUNDLE,bundle);
        fillInIntent.putExtra(MainActivity.ADDRESS_EXTRA, mAddress);
        remoteViews.setOnClickFillInIntent(R.id.grid_item_layout, fillInIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() { return null;  }

    @Override
    public int getViewTypeCount() {
        return (mDays==null ?  0 : mDays.size() );
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Forecast forecast = WidgetForecastUpdateService.staticForecast;
        mAddress = WidgetForecastUpdateService.staticAddress;
        mDays = forecast.getDailyForecastList();
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(mContext);
        mTheme = tempestatibusApplicationSettings.getWidgetThemePreference(mAppWidgetId);
    }

    @Override
    public void onDestroy() {

    }

    private int getLayoutId() {
        TempestatibusApplicationSettings settings = new TempestatibusApplicationSettings();
        settings.createSharedPreferenceContext(mContext);
        String currentTheme = settings.getWidgetThemePreference(mAppWidgetId);
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_daily_grid_item;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_daily_grid_item;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_daily_grid_item;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_daily_grid_item;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.layout.clear_black_daily_grid_item;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.layout.clear_white_daily_grid_item;
            }
            default: return R.layout.summer_daily_grid_item;
        }
    }
}
