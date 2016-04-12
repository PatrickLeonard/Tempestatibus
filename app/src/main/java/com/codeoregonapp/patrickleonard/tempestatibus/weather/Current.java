package com.codeoregonapp.patrickleonard.tempestatibus.weather;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.codeoregonapp.patrickleonard.tempestatibus.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Model class for holding the "Current" forecast data
 * Created by Patrick Leonard on 10/18/2015.
 */
public class Current extends WeatherData implements Parcelable {

    public static final String TAG = Current.class.getSimpleName();

    private String mTemperature;
    private String mNearestStormDistance;
    private String mNearestStormBearing;
    private String mApparentTemperature;

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }

    public String getNearestStormDistance() {
        return mNearestStormDistance;
    }

    public void setNearestStormDistance(String nearestStormDistance) {
        mNearestStormDistance = nearestStormDistance;
    }

    public String getNearestStormBearing() {
        return mNearestStormBearing;
    }

    public void setNearestStormBearing(String nearestStormBearing) {
        mNearestStormBearing = nearestStormBearing;
    }

    public String getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(String apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return formatter.format(date);
    }

    public String getFormattedDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d hh:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return formatter.format(date);
    }

    public String getFormattedDayAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, h a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return formatter.format(date);
    }

    protected Current(Parcel in) {
        super(in);
        setTemperature(in.readString());
        setNearestStormDistance(in.readString());
        setNearestStormBearing(in.readString());
        setApparentTemperature(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getTemperature());
        dest.writeString(getNearestStormDistance());
        dest.writeString(getNearestStormBearing());
        dest.writeString(getApparentTemperature());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Current> CREATOR = new Creator<Current>() {
        @Override
        public Current createFromParcel(Parcel in) {
            return new Current(in);
        }

        @Override
        public Current[] newArray(int size) {
            return new Current[size];
        }
    };

    public Current() { super(); }

    public Current(Context context, JSONObject data,String units) throws JSONException {
        super(context,data,units);
        jsonToDataMembers(data);
    }

    protected void jsonToDataMembers(JSONObject data) throws JSONException {
        extractTemperature(data); extractNearestStormBearing(data);
        extractNearestStormDistance(data); extractApparentTemperature(data);
    }

    private void extractApparentTemperature(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_apparentTemperature))) {
            setApparentTemperature((int)Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_apparentTemperature)))+"");
        }
        else  {
            setApparentTemperature(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractNearestStormDistance(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_nearestStormDistance))) {
            setNearestStormDistance((int)Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_nearestStormDistance)))+"");
        }
        else {
            setNearestStormDistance(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractNearestStormBearing(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_nearestStormBearing))) {
            double windBearing = data.getDouble(getContext().getString(R.string.JSON_double_property_nearestStormBearing));
            setNearestStormBearing(translateBearingToDirection(windBearing));
        }
        else {
            setNearestStormBearing(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractTemperature(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_temperature))) {
            setTemperature((int)Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_temperature)))+"");
        }
        else {
            setTemperature(getContext().getString(R.string.empty_two_digit_number));
        }
    }
}
