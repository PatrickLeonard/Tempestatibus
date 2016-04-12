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
 * Model class that holds the forecast data for a given Day
 * Created by Patrick Leonard on 11/5/2015.
 */
public class Day extends WeatherData implements Parcelable {
    
    private long mSunriseTime;
    private long mSunsetTime;
    private long mPrecipitationIntensityMaxTime;
    private long mTemperatureMinTime;
    private long mTemperatureMaxTime;
    private long mApparentTemperatureMinTime;
    private long mApparentTemperatureMaxTime;
    private String mMoonPhase;
    private double mPrecipitationIntensityMax;
    private String mPrecipitationIntensityMaxString;
    private String mTemperatureMin;
    private String mTemperatureMax;
    private String mApparentTemperatureMin;
    private String mApparentTemperatureMax;
    
    public long getSunriseTime() {
        return mSunriseTime;
    }

    public void setSunriseTime(long sunriseTime) {
        mSunriseTime = sunriseTime;
    }

    public long getSunsetTime() {
        return mSunsetTime;
    }

    public void setSunsetTime(long sunsetTime) {
        mSunsetTime = sunsetTime;
    }

    public String getMoonPhase() {
        return mMoonPhase;
    }

    public void setMoonPhase(String moonPhase) {
        mMoonPhase = moonPhase;
    }

    public double getPrecipitationIntensityMax() {
        return mPrecipitationIntensityMax;
    }

    public void setPrecipitationIntensityMax(double precipitationIntensityMax) {
        mPrecipitationIntensityMax = precipitationIntensityMax;
    }

    public String getPrecipitationIntensityMaxString() {
        return mPrecipitationIntensityMaxString;
    }

    public void setPrecipitationIntensityMaxString(String precipitationIntensityMaxString) {
        mPrecipitationIntensityMaxString = precipitationIntensityMaxString;
    }

    public String getTemperatureMin() {
        return mTemperatureMin;
    }

    public void setTemperatureMin(String temperatureMin) {
        mTemperatureMin = temperatureMin;
    }

    public long getTemperatureMinTime() {
        return mTemperatureMinTime;
    }

    public void setTemperatureMinTime(long temperatureMinTime) {
        mTemperatureMinTime = temperatureMinTime;
    }

    public String getTemperatureMax() {
        return mTemperatureMax;
    }

    public void setTemperatureMax(String temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public long getTemperatureMaxTime() {
        return mTemperatureMaxTime;
    }

    public void setTemperatureMaxTime(long temperatureMaxTime) {
        mTemperatureMaxTime = temperatureMaxTime;
    }

    public String getApparentTemperatureMin() {
        return mApparentTemperatureMin;
    }

    public void setApparentTemperatureMin(String apparentTemperatureMin) {
        mApparentTemperatureMin = apparentTemperatureMin;
    }

    public long getApparentTemperatureMinTime() {
        return mApparentTemperatureMinTime;
    }

    public void setApparentTemperatureMinTime(long apparentTemperatureMinTime) {
        mApparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    public String getApparentTemperatureMax() {
        return mApparentTemperatureMax;
    }

    public void setApparentTemperatureMax(String apparentTemperatureMax) {
        mApparentTemperatureMax = apparentTemperatureMax;
    }

    public long getApparentTemperatureMaxTime() {
        return mApparentTemperatureMaxTime;
    }

    public void setApparentTemperatureMaxTime(long apparentTemperatureMaxTime) {
        mApparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }

    public long getPrecipitationIntensityMaxTime() {
        return mPrecipitationIntensityMaxTime;
    }

    public void setPrecipitationIntensityMaxTime(long precipitationIntensityMaxTime) {
        mPrecipitationIntensityMaxTime = precipitationIntensityMaxTime;
    }

    public String getTimeOfDay(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(time*1000);
        return dateFormat.format(date);
    }

    public String getDayOfTheWeekAbbreviation() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return dateFormat.format(date);
    }

    public String getWellFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return dateFormat.format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(getSunriseTime());
        dest.writeLong(getSunsetTime());
        dest.writeLong(getApparentTemperatureMaxTime());
        dest.writeLong(getApparentTemperatureMinTime());
        dest.writeLong(getTemperatureMaxTime());
        dest.writeLong(getTemperatureMinTime());
        dest.writeLong(getPrecipitationIntensityMaxTime());
        dest.writeString(getTemperatureMax());
        dest.writeString(getTemperatureMin());
        dest.writeString(getApparentTemperatureMax());
        dest.writeString(getApparentTemperatureMin());
        dest.writeString(getMoonPhase());
        dest.writeDouble(getPrecipitationIntensityMax());
        dest.writeString(getPrecipitationIntensityMaxString());
    }

    private Day (Parcel parcel) {
        super(parcel);
        setSunriseTime(parcel.readLong());
        setSunsetTime(parcel.readLong());
        setApparentTemperatureMaxTime(parcel.readLong());
        setApparentTemperatureMinTime(parcel.readLong());
        setTemperatureMaxTime(parcel.readLong());
        setTemperatureMinTime(parcel.readLong());
        setPrecipitationIntensityMaxTime(parcel.readLong());
        setTemperatureMax(parcel.readString());
        setTemperatureMin(parcel.readString());
        setApparentTemperatureMax(parcel.readString());
        setApparentTemperatureMin(parcel.readString());
        setMoonPhase(parcel.readString());
        setPrecipitationIntensityMax(parcel.readDouble());
        setPrecipitationIntensityMaxString(parcel.readString());
    }

    public Day(Context context, JSONObject data, String units) throws JSONException {
        super(context, data, units);
        setContext(context); setUnits(units);
        jsonToDataMembers(data);
    }

    public Day() {
        super();
    }

    public  static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    private void jsonToDataMembers(JSONObject dailyDataObject) throws JSONException {
        extractApparentTemperatureMax(dailyDataObject); extractApparentTemperatureMin(dailyDataObject);
        extractTemperatureMax(dailyDataObject); extractTemperatureMin(dailyDataObject);
        extractPrecipitationIntensityMax(dailyDataObject); extractMoonPhase(dailyDataObject);
        extractTemperatureMaxTime(dailyDataObject); extractTemperatureMinTime(dailyDataObject);
        extractApparentTemperatureMaxTime(dailyDataObject); extractApparentTemperatureMinTme(dailyDataObject);
        extractSunriseTime(dailyDataObject);  extractSunsetTime(dailyDataObject);
        extractPrecipitationIntensityMaxTime(dailyDataObject);
    }

    private void extractPrecipitationIntensityMaxTime(JSONObject dailyDataObject) throws JSONException {
        //Conditional data item
        if(getPrecipitationIntensityMax() != 0 &&
                dailyDataObject.has(getContext().getString(R.string.JSON_long_property_precipIntensityMaxTime))) {
            setPrecipitationIntensityMaxTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_precipIntensityMaxTime)));
        }

    }

    private void extractSunsetTime(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_sunsetTime))) {
            setSunsetTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_sunsetTime)));
        }
        else {
            throw new JSONException("SunsetTime is unavailable");
        }
    }

    private void extractSunriseTime(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_sunriseTime))) {
            setSunriseTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_sunriseTime)));
        }
        else {
            throw new JSONException("SunriseTime is unavailable");
        }
    }

    private void extractApparentTemperatureMinTme(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_apparentTemperatureMinTime))) {
            setApparentTemperatureMinTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_apparentTemperatureMinTime)));
        }
        else {
            throw new JSONException("ApparentTemperatureMinTime is unavailable");
        }
    }

    private void extractApparentTemperatureMaxTime(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_apparentTemperatureMaxTime))) {
            setApparentTemperatureMaxTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_apparentTemperatureMaxTime)));
        }
        else {
            throw new JSONException("ApparentTemperatureMaxTime is unavailable");
        }
    }

    private void extractTemperatureMinTime(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_temperatureMinTime))) {
            setTemperatureMinTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_temperatureMinTime)));
        }
        else {
            throw new JSONException("Temperature Min Time unavailable");
        }
    }

    private void extractTemperatureMaxTime(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_long_property_temperatureMaxTime))) {
            setTemperatureMaxTime(dailyDataObject.getLong(getContext().getString(R.string.JSON_long_property_temperatureMaxTime)));
        }
        else  {
            throw new JSONException("Temperature Max time not available");
        }
    }

    private void extractMoonPhase(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_double_propperty_moonPhase))) {
            double phase = dailyDataObject.getDouble(getContext().getString(R.string.JSON_double_propperty_moonPhase));
            setMoonPhase(translateMoonPhase(phase));
        }
        else {
            setMoonPhase(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private String translateMoonPhase(double phase) {
        if(phase == 0) {
            return "New";
        }
        else if(phase > 0 && phase < .25) {
            return "Waxing Crescent";
        }
        else if(phase == .25) {
            return "First Quarter";
        }
        else if(phase > .25 && phase < .50) {
            return "Waxing Gibbous";
        }
        else if(phase == .50) {
            return "Full";
        }
        else if(phase > .50 && phase < .75) {
            return "Waning Gibbous";
        }
        else if(phase == .75) {
            return "Last Quarter";
        }
        else if(phase > .75) {
            return "Waxing Crescent";
        }
        else {
            return getContext().getString(R.string.empty_two_digit_number);
        }
    }

    private void extractPrecipitationIntensityMax(JSONObject dailyDataObject) throws JSONException {
        if(dailyDataObject.has(getContext().getString(R.string.JSON_double_propert_precipIntensityMax))) {
            double intensity = dailyDataObject.getDouble(getContext().getString(R.string.JSON_double_propert_precipIntensityMax));
            setPrecipitationIntensityMax(intensity);
            setPrecipitationIntensityMaxString(translatePrecipitationIntensity(intensity));
        }
        else {
            setPrecipitationIntensityMax(-1);
        }
    }

    private void extractApparentTemperatureMax(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_apparentTemperatureMax))) {
            setApparentTemperatureMax((int) Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_apparentTemperatureMax)))+"");
        } else {
            setApparentTemperatureMax(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractApparentTemperatureMin(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_apparentTemperatureMin))) {
            setApparentTemperatureMin((int) Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_apparentTemperatureMin))) + "");
        } else {
            setApparentTemperatureMin(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractTemperatureMax(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_object_double_property_max_temp))) {
            setTemperatureMax((int) Math.round(data.getDouble(getContext().getString(R.string.JSON_object_double_property_max_temp))) + "");
        } else {
            setTemperatureMax(getContext().getString(R.string.empty_two_digit_number));
        }
    }

    private void extractTemperatureMin(JSONObject data) throws JSONException {
        if(data.has(getContext().getString(R.string.JSON_double_property_temperatureMin))) {
            setTemperatureMin((int) Math.round(data.getDouble(getContext().getString(R.string.JSON_double_property_temperatureMin))) + "");
        } else {
            setTemperatureMin(getContext().getString(R.string.empty_two_digit_number));
        }
    }
}
