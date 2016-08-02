package com.codeoregonapp.patrickleonard.tempestatibus.weather;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class that holds the Forecast data used by this app
 * Created by Patrick Leonard on 11/5/2015.
 */
public class Forecast implements Parcelable {

    private static Context mContext;
    private Current mCurrent;
    private List<Hour> mHourlyForecast;
    private List<Day> mDailyForecast;
    private List<WeatherAlert> mWeatherAlerts;
    private String mUnits;
    private String mTimeUntilPrecipitation;
    private Double mLatitude;
    private Double mLongitude;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        }
        return mTempestatibusApplicationSettings;
    }
    
    public String getTimeUntilPrecipitation() {
        return mTimeUntilPrecipitation;
    }

    public void setTimeUntilPrecipitation(String timeUntilPrecipitation) {
        mTimeUntilPrecipitation = timeUntilPrecipitation;
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public List<Day> getDailyForecastList() { return mDailyForecast; }

    public List<Hour> getHourlyForecastList() { return mHourlyForecast; }

    public List<WeatherAlert> getWeatherAlerts() { return mWeatherAlerts; }

    public void setHourlyForecast(List<Hour> hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public void setDailyForecast(List<Day> dailyForecast) {
        mDailyForecast = dailyForecast;
    }

    public void setWeatherAlerts(List<WeatherAlert> weatherAlerts) { mWeatherAlerts = weatherAlerts;    }

    public Forecast(String jsonData,Context context) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        setContext(context);
        setUnits(forecast.getJSONObject(getContext().getString(R.string.JSON_object_property_flags))
                .getString(getContext().getString(R.string.JSON_string_property_units)));
        setLatitude(forecast.getDouble(context.getString(R.string.JSON_double_property_latitude)));
        setLongitude(forecast.getDouble(context.getString(R.string.JSON_double_property_longitude)));
        setCurrent(new Current(getContext(),
                forecast.getJSONObject(getContext().getString(R.string.JSON_Object_name_currently)),
                getUnits()));
        if(forecast.has("minutely")) {
            setTimeUntilPrecipitation(createTimeUntilPrecipitationString(forecast.getJSONObject("minutely")));
        }
        parseJSONForecastData(forecast);
    }

    private String createTimeUntilPrecipitationString(JSONObject minutely) throws JSONException {
        JSONArray minutelyData = minutely.getJSONArray(mContext.getString(R.string.JSON_data_array));
        double maxProbability = 0;
        int maxIndex = 0;
        for(int i=0;i<minutelyData.length();++i) {
            double precipProb = minutelyData.getJSONObject(i).getDouble(mContext.getString(R.string.JSON_double_property_precip_probability));
            if(precipProb > maxProbability) {
                maxProbability = precipProb;
                maxIndex = i;
            }
        }
        if(maxProbability > 0) {
            long time = minutelyData.getJSONObject(maxIndex).getLong(mContext.getString(R.string.JSON_long_property_time));
            int minutesUntil = (int)Math.floor((time - getCurrent().getTime())/60); //API uses seconds, 60 is fine here
            int precipProbInt = (int)Math.floor(maxProbability*100);
            String precipType = minutelyData.getJSONObject(maxIndex).getString(mContext.getString(R.string.JSON_double_property_precipitationType));
            return String.format("%1s%% chance of %2s in %3s minute(s).",precipProbInt,precipType,minutesUntil);
        }
        else {
            return "No precipitation expected this hour.";
        }
    }

    public Forecast() { super(); }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCurrent, flags);
        dest.writeList(mHourlyForecast);
        dest.writeList(mDailyForecast);
        dest.writeList(mWeatherAlerts);
        dest.writeString(mUnits);
        dest.writeString(mTimeUntilPrecipitation);
    }

    protected Forecast(Parcel in) {
        mCurrent = in.readParcelable(Current.class.getClassLoader());
        mHourlyForecast = new ArrayList<>();
        in.readList(mHourlyForecast, Hour.class.getClassLoader());
        mDailyForecast = new ArrayList<>();
        in.readList(mDailyForecast, Day.class.getClassLoader());
        mWeatherAlerts = new ArrayList<>();
        in.readList(mWeatherAlerts, WeatherAlert.class.getClassLoader());
        mUnits = in.readString();
        mTimeUntilPrecipitation = in.readString();
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    private void parseJSONForecastData(JSONObject forecast) throws JSONException {
        setHourlyForecast(parseHourlyForecast(forecast));
        setDailyForecast(parseDailyForecast(forecast));
        setWeatherAlerts(parseWeatherAlerts(forecast));
    }

    private List<WeatherAlert> parseWeatherAlerts(JSONObject forecast) throws JSONException {
        JSONArray alertArray;
        List<WeatherAlert> weatherAlerts = new ArrayList<>();
        if(forecast.has("alerts")) {
            alertArray = forecast.getJSONArray("alerts");
            for(int i=0;i<alertArray.length();i++) {
                JSONObject alert = alertArray.getJSONObject(i);
                WeatherAlert weatherAlert = new WeatherAlert(alert.getString("title"),
                        alert.getLong("time"),
                        alert.getLong("expires"),
                        alert.getString("description"),
                        alert.getString("uri"));
                weatherAlerts.add(i,weatherAlert);
            }
        }
        return  weatherAlerts;
    }

    private List<Day> parseDailyForecast(JSONObject forecast) throws JSONException {
        JSONObject daily = forecast.getJSONObject(mContext.getString(R.string.JSON_object_daily));
        JSONArray data = daily.getJSONArray(mContext.getString(R.string.JSON_data_array));
        List<Day> dailyForecast = new ArrayList<>();
        for(int i=0;i<data.length();++i) {
            dailyForecast.add(i, new Day(mContext, data.getJSONObject(i),getUnits()));
        }
        return dailyForecast;
    }

    private List<Hour> parseHourlyForecast(JSONObject forecast) throws JSONException {
        JSONObject hourly = forecast.getJSONObject(mContext.getString(R.string.JSON_object_property_hourly));
        JSONArray data = hourly.getJSONArray(mContext.getString(R.string.JSON_data_array));
        List<Hour> hourlyForecast = new ArrayList<>();
        for(int i=0;i<data.length();++i) {
            hourlyForecast.add(i, new Hour(mContext, data.getJSONObject(i),getUnits()));
        }
        return hourlyForecast;
    }

    //Utility functions to determine the proper units and associated strings for the forecast data
    public String determineStormBearing(Forecast forecast) {
        String stormBearing;
        if(!forecast.getCurrent().getNearestStormDistance().equals("0")) {
            stormBearing = forecast.getCurrent().getNearestStormBearing();
        }
        else {
            stormBearing = "";
        }
        return stormBearing;
    }

    public String determineWindBearing(Forecast forecast) {
        String windBearing;
        if(!forecast.getCurrent().getWindSpeed().equals("0")) {
            windBearing = forecast.getCurrent().getWindBearing();
        }
        else {
            windBearing = "";
        }
        return windBearing;
    }

    public int getDistanceUnitsId() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(getContext());
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return R.string.si_units_kilometers;
        }
        else {
            return R.string.us_unit_miles;
        }
    }

    public int getVelocityUnitsId() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(getContext());
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return R.string.si_units_kilometers_per_hour;
        }
        else {
            return R.string.us_units_miles_per_hour;
        }
    }

    public int getTemperatureUnitsId() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(getContext());
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return R.string.si_units_celsius;
        }
        else {
            return R.string.us_units_fahrenheit;
        }
    }

    public int getPressureUnitsId() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(getContext());
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return R.string.si_units_hectoPascals;
        }
        else {
            return R.string.us_units_millibars;
        }
    }
}
