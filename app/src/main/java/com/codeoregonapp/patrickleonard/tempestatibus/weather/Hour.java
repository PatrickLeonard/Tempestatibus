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
 * Model class that holds the forecast data for the given hour
 * Created by Patrick Leonard on 11/5/2015.
 */
public class Hour extends WeatherData implements Parcelable {

    public static final String TAG = Hour.class.getSimpleName();

    protected String mTemperature;
    protected String mApparentTemperature;
    protected String mPrecipitationAccumulation;

    public String getPrecipitationAccumulation() {
        return mPrecipitationAccumulation;
    }

    public void setPrecipitationAccumulation(String precipitationAccumulation) {
        mPrecipitationAccumulation = precipitationAccumulation;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }

    public String getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(String apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public String getHour() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh a",Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime() * 1000);
        return dateFormat.format(date);
    }

    public String getMonthAndDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime() * 1000);
        return dateFormat.format(date);

    }

    public boolean isMidnight() {
        //Get the number of seconds in a day
        Long secondsInADay = 24L * 60L * 60L;
        //Find out what the time zone offset is from GMT (epoch time is based on GMT)
        //Get the offset milliseconds into positive seconds
        Long gmtOffsetSeconds = Math.abs((TimeZone.getDefault().getOffset(getTime() * 1000) / 1000L));
        //If modulus mTime with secondsInADay gives the gmtOffsetSeconds, then it is midnight
        //The incoming time for hours has no minutes, seconds, or lower dimensions of time.
        return (getTime() % secondsInADay == gmtOffsetSeconds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(getTemperature());
        dest.writeString(getApparentTemperature());
        dest.writeString(getPrecipitationAccumulation());
    }

    public Hour()  { super(); }

    public Hour(Parcel parcel) {
        super(parcel);
        setTemperature(parcel.readString());
        setApparentTemperature(parcel.readString());
        setPrecipitationAccumulation(parcel.readString());
    }

    public Hour(Context context,JSONObject data,String units) throws JSONException {
        super(context, data, units);
        jsonToDataMembers(data);
    }

    public static final Creator<Hour> CREATOR = new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel source) {
            return new Hour(source);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };

    private void jsonToDataMembers(JSONObject data) throws JSONException {
        extractTemperature(data);  extractApparentTemperature(data);
        extractPrecipitationAccumulation(data);
    }

    private void extractPrecipitationAccumulation(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_precipitatioonAccumulation))) {
            setPrecipitationAccumulation(data.getDouble(getContext().getString(R.string.JSON_double_property_precipitatioonAccumulation))+"");
        }
        else {
            setPrecipitationAccumulation(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractApparentTemperature(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_apparentTemperature))) {
            setApparentTemperature(data.getDouble(getContext().getString(R.string.JSON_double_property_apparentTemperature))+"");
        }
        else  {
            setApparentTemperature(getContext().getString(R.string.empty_two_digit_number));
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
