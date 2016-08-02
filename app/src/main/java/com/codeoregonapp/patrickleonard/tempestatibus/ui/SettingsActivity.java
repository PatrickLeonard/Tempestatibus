package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsActivity.class.getSimpleName();
    public static  boolean RECREATED;
    public static String CURRENT_THEME;
    public static boolean PREFERENCES_CHANGED;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SettingsActivity.RECREATED) {
            SettingsActivity.PREFERENCES_CHANGED = true;
        }
        else {
            SettingsActivity.PREFERENCES_CHANGED = false;
            SettingsActivity.RECREATED = false;
        }
        mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        mTempestatibusApplicationSettings.createSharedPreferenceContext(this);
        mTempestatibusApplicationSettings.registerListener(this);
        setTheme(mTempestatibusApplicationSettings.getThemeId(mTempestatibusApplicationSettings.getAppThemePreference()));
        setActionBarAppearance();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TempestatibusSettingsFragment())
                .commit();
    }

    private void setActionBarAppearance() {
        android.app.ActionBar bar = getActionBar();
        if(bar != null) {
            bar.setBackgroundDrawable(getBackgroundDrawable());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bar.setHomeAsUpIndicator(getBackArrowId());
                bar.setDisplayShowTitleEnabled(false);
            }
            else {
                bar.setDisplayShowTitleEnabled(true);
            }
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home: {
                //MainActivity has finished, restart the app
                Intent i = getBaseContext().getPackageManager().
                        getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setActivityTheme(AppCompatActivity activity) {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(activity);
        SettingsActivity.CURRENT_THEME = tempestatibusApplicationSettings.getAppThemePreference();
        activity.setTheme(tempestatibusApplicationSettings.getThemeId(SettingsActivity.CURRENT_THEME));
    }

    public static void setPreferenceActivityTheme(PreferenceActivity activity) {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(activity);
        SettingsActivity.CURRENT_THEME = tempestatibusApplicationSettings.getAppThemePreference();
        activity.setTheme(tempestatibusApplicationSettings.getThemeId(SettingsActivity.CURRENT_THEME));
    }

    public static boolean getUnitsPreference(PreferenceActivity activity) {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(activity);
        return tempestatibusApplicationSettings.getAppUnitsPreference();
    }

    public static boolean getHourlyExtendPreference(PreferenceActivity activity) {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(activity);
        return tempestatibusApplicationSettings.getHourlyExtendPreference();
    }

    @Override
    protected void onResume() {
        SettingsActivity.setPreferenceActivityTheme(this);
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SettingsActivity.PREFERENCES_CHANGED = true;
        if(key.equals(TempestatibusApplicationSettings.APP_THEME_PREFERENCE)) {
            SettingsActivity.CURRENT_THEME = sharedPreferences.getString(TempestatibusApplicationSettings.APP_THEME_PREFERENCE,
                    TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE);
            SettingsActivity.RECREATED = true;
            recreate();
        }
    }

    private Drawable getBackgroundDrawable() {
        switch (mTempestatibusApplicationSettings.getAppThemePreference()) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return ContextCompat.getDrawable(this,R.drawable.bg_gradient_autumn);
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return ContextCompat.getDrawable(this,R.drawable.bg_gradient_spring);
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return ContextCompat.getDrawable(this,R.drawable.bg_gradient_summer);
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return ContextCompat.getDrawable(this,R.drawable.bg_gradient_winter);
            }
            default:
                return null;
        }
    }

    private int getBackArrowId() {
        switch(mTempestatibusApplicationSettings.getAppThemePreference()) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_back_arrow;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_back_arrow;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_back_arrow;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_back_arrow;
            }
            default: return -1;
        }
    }
}
