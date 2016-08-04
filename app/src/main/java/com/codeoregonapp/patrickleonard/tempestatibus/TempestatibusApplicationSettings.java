package com.codeoregonapp.patrickleonard.tempestatibus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codeoregonapp.patrickleonard.tempestatibus.ui.SettingsActivity;

import java.util.Locale;

/**
 * This class holds various public static fields/methods pertaining to the applications settings
 * Created by Patrick Leonard on 11/30/2015.
 */
public class TempestatibusApplicationSettings {

    public final static String TAG = TempestatibusApplication.class.getSimpleName();
    public final static String APP_THEME_PREFERENCE = "APP_THEME";
    public final static String WIDGET_THEME_PREFERENCE = "WIDGET_THEME";
    public final static String APP_UNITS_PREFERENCE = "APP_UNITS";
    public final static String HOURLY_EXTEND_APP_PREFERENCE = "APP_HOURLY_EXTEND";
    public final static boolean US_UNITS_PREFERENCE = false;
    public final static boolean SI_UNITS_PREFERENCE = true;
    public final static String SUMMER_THEME_PREFERENCE = "SUMMER";
    public final static String AUTUMN_THEME_PREFERENCE = "AUTUMN";
    public final static String SPRING_THEME_PREFERENCE = "SPRING";
    public final static String WINTER_THEME_PREFERENCE = "WINTER";
    public final static String CLEAR_BLACK_THEME_PREFERENCE = "CLEAR_BLACK";
    public final static String CLEAR_WHITE_THEME_PREFERENCE = "CLEAR_WHITE";

    private SharedPreferences mSharedPreferences;


    public TempestatibusApplicationSettings() {
    }

    public void createSharedPreferenceContext(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if(mSharedPreferences != null) {
            mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    public String getAppThemePreference() {
        return mSharedPreferences.getString(APP_THEME_PREFERENCE, WINTER_THEME_PREFERENCE);
    }

    public String getWidgetThemePreference(int widgetId) {
        return mSharedPreferences.getString(WIDGET_THEME_PREFERENCE+widgetId, CLEAR_WHITE_THEME_PREFERENCE);
    }

    public void setWidgetThemePreference(String theme,int widgetId) {
        mSharedPreferences.edit().putString(WIDGET_THEME_PREFERENCE+widgetId,theme).apply();
    }

    public void removeWidgetThemePreference(int widgetId) {
        mSharedPreferences.edit().remove(WIDGET_THEME_PREFERENCE+widgetId).apply();
    }

    public boolean getAppUnitsPreference() {
        return mSharedPreferences.getBoolean(APP_UNITS_PREFERENCE, determineDefaultUnits());
    }

    public boolean getHourlyExtendPreference() {
        return mSharedPreferences.getBoolean(HOURLY_EXTEND_APP_PREFERENCE, false);
    }

    public int getThemeId(String theme) {
        switch(theme) {
            case SUMMER_THEME_PREFERENCE: {
                return R.style.SUMMER;
            }
            case AUTUMN_THEME_PREFERENCE: {
                return R.style.AUTUMN;
            }
            case SPRING_THEME_PREFERENCE: {
                return R.style.SPRING;
            }
            case WINTER_THEME_PREFERENCE: {
                return R.style.WINTER;
            }
            default: return -1;
        }
    }

    public static int getRefreshId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_refresh;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_refresh;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_refresh;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_refresh;
            }
            default: return R.drawable.refresh;
        }

    }

    public static int getLargeDegreeId(String currentTheme) {

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_degree_large;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_degree_large;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_degree_large;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_degree_large;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.drawable.black_degree_large;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.drawable.summer_degree_large;
            }
            default: return R.drawable.summer_degree_large;
        }

    }

    public static int getSmallDegreeId(String currentTheme) {

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_degree_small;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_degree_small;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_degree_small;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_degree_small;
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return R.drawable.black_degree_small;
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return R.drawable.summer_degree_small;
            }
            default: return R.drawable.summer_degree_small;
        }

    }

    public static int getSettingsIconId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_settings_icon;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_settings_icon;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_settings_icon;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_settings_icon;
            }
            default: return R.drawable.settings_icon;
        }

    }

    public static int getSaveIconId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_save_icon;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_save_icon;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_save_icon;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_save_icon;
            }
            default: return R.drawable.summer_save_icon;
        }
    }
    
    public static int getHourglassId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_hourglass;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_hourglass;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_hourglass;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_hourglass;
            }
            default: return R.drawable.summer_hourglass;
        }
    }

    public static int getSearchIconId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.autumn_search;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.spring_search;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.summer_search;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.winter_search;
            }
            default: return R.drawable.summer_search;
        }
    }
    
    public static int getSettingsPreferenceSwitchWidgetLayout() {
        String currentTheme = SettingsActivity.CURRENT_THEME;

        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.layout.autumn_switch_widget;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.layout.spring_switch_widget;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.layout.summer_switch_widget;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.layout.winter_switch_widget;
            }
            default: return R.layout.summer_switch_widget;
        }

    }

    public static int getBackgroundDrawableId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.drawable.bg_gradient_autumn;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.drawable.bg_gradient_spring;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.drawable.bg_gradient_summer;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.drawable.bg_gradient_winter;
            }
            default: return R.drawable.bg_gradient_summer;
        }
    }

    public static int getTextColorId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.color.autumn_text_color;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.color.spring_text_color;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.color.summer_text_color;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.color.winter_text_color;
            }
            default: return R.color.summer_text_color;
        }
    }

    public static int getAlertDialogThemeId() {
        String currentTheme = SettingsActivity.CURRENT_THEME;
        switch(currentTheme) {
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE: {
                return R.style.AUTUMN_DIALOG_THEME;
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE: {
                return R.style.SPRING_DIALOG_THEME;
            }
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE: {
                return R.style.SUMMER_DIALOG_THEME;
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE: {
                return R.style.WINTER_DIALOG_THEME;
            }
            default: return R.style.SUMMER_DIALOG_THEME;
        }
    }

    public static String getForecastLanguage() {
        String lang = Locale.getDefault().getLanguage();
        String apiCallLang;
        switch(lang) {
            case("ar"): { apiCallLang = lang; break; } // (Arabic-done)
            case("bs"): { apiCallLang = lang; break; } // (Bosnian-done)
            case("de"): { apiCallLang = lang; break; } // (German-done)
            case("el"): { apiCallLang = lang; break; } // (Greek-done)
            case("en"): { apiCallLang = lang; break; } // (English-done)
            case("es"): { apiCallLang = lang; break; } // (Spanish-done)
            case("fr"): { apiCallLang = lang; break; } // (French-done)
            case("hr"): { apiCallLang = lang; break; } // (Croatian-done)
            case("it"): { apiCallLang = lang; break; } // (Italian-done)
            case("nl"): { apiCallLang = lang; break; } // (Dutch-done)
            case("pl"): { apiCallLang = lang; break; } // (Polish-done)
            case("pt"): { apiCallLang = lang; break; } // (Portuguese-done)
            case("ru"): { apiCallLang = lang; break; } // (Russian-done)
            case("sk"): { apiCallLang = lang; break; } //(Slovak-done)
            case("sv"): { apiCallLang = lang; break; } // (Swedish-done)
            case("tet"): { apiCallLang = lang; break; } // (Tetum-no Google Translate)
            case("tr"): { apiCallLang = lang; break; } //  (Turkish-done)
            case("uk"): { apiCallLang = lang; break; } // (Ukrainian-done)
            case("zh"): { apiCallLang = lang; break; } // (simplified Chinese-done)
            case("zh-tw"): { apiCallLang = lang; break; } //(Traditional Chinese-done)
            default: apiCallLang = "en";
        }
        return apiCallLang;
    }

    public static boolean determineDefaultUnits() {
        String country = Locale.getDefault().getCountry();
        if(country.equals("US")) {
            return TempestatibusApplicationSettings.US_UNITS_PREFERENCE;
        }
        else {
            return TempestatibusApplicationSettings.SI_UNITS_PREFERENCE;
        }
    }

    public static int getIconId(String iconString,String currentTheme,Context context) {

        switch(currentTheme) {
            case TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE : {
                return getSummerIconId(iconString,context);
            }
            case TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE : {
                return getSpringIconId(iconString,context);
            }
            case TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE : {
                return getAutumnIconId(iconString,context);
            }
            case TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE : {
                return getWinterIconId(iconString,context);
            }
            case TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE: {
                return getSummerIconId(iconString,context);
            }
            case TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE: {
                return getBlackIconId(iconString,context);
            }
            default : {
                return getBlankIconId(iconString,context);
            }
        }

    }

    public static int getBlankIconId(String iconString,Context context) {

        int iconId = R.drawable.clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.cloudy_night;
        }

        return iconId;
    }


    public static int getSummerIconId(String iconString,Context context) {

        int iconId = R.drawable.summer_clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.summer_clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.summer_clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.summer_rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.summer_snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.summer_sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.summer_wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.summer_fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.summer_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.summer_partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.summer_cloudy_night;
        }

        return iconId;
    }


    public static int getSpringIconId(String iconString,Context context) {

        int iconId = R.drawable.spring_clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.spring_clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.spring_clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.spring_rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.spring_snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.spring_sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.spring_wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.spring_fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.spring_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.spring_partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.spring_cloudy_night;
        }

        return iconId;
    }


    public static int getAutumnIconId(String iconString,Context context) {

        int iconId = R.drawable.autumn_clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.autumn_clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.autumn_clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.autumn_rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.autumn_snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.autumn_sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.autumn_wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.autumn_fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.autumn_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.autumn_partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.autumn_cloudy_night;
        }

        return iconId;
    }


    public static int getWinterIconId(String iconString,Context context) {

        int iconId = R.drawable.winter_clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.winter_clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.winter_clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.winter_rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.winter_snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.winter_sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.winter_wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.winter_fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.winter_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.winter_partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.winter_cloudy_night;
        }

        return iconId;
    }

    public static int getBlackIconId(String iconString,Context context) {

        int iconId = R.drawable.black_clear_day;

        if (iconString.equals(context.getString(R.string.clear_day))) {
            iconId = R.drawable.black_clear_day;
        }
        else if (iconString.equals(context.getString(R.string.clear_night))) {
            iconId = R.drawable.black_clear_night;
        }
        else if (iconString.equals(context.getString(R.string.rain))) {
            iconId = R.drawable.black_rain;
        }
        else if (iconString.equals(context.getString(R.string.snow))) {
            iconId = R.drawable.black_snow;
        }
        else if (iconString.equals(context.getString(R.string.sleet))) {
            iconId = R.drawable.black_sleet;
        }
        else if (iconString.equals(context.getString(R.string.wind))) {
            iconId = R.drawable.black_wind;
        }
        else if (iconString.equals(context.getString(R.string.fog))) {
            iconId = R.drawable.black_fog;
        }
        else if (iconString.equals(context.getString(R.string.cloudy))) {
            iconId = R.drawable.black_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_day))) {
            iconId = R.drawable.black_partly_cloudy;
        }
        else if (iconString.equals(context.getString(R.string.partly_cloudy_night))) {
            iconId = R.drawable.black_cloudy_night;
        }

        return iconId;
    }
    
}
