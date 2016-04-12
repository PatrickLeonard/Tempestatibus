package com.codeoregonapp.patrickleonard.tempestatibus.weather;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base Class for the weather data received from the Dark Sky Forecast API
 * Created by Patrick Leonard on 1/1/2016.
 */
public class WeatherData implements Parcelable {

    public static final String TAG = WeatherData.class.getSimpleName();
    private String mIcon;
    private long mTime;
    private String mHumidity;
    private String mPrecipitationProbability;
    private String mSummary;
    private double mPrecipitationIntensity;
    private String mPrecipitationIntensityString;
    private String mPrecipitationType;
    private String mDewPoint;
    private String mWindSpeed;
    private String mWindBearing;
    private String mVisibility;
    private String mCloudCover;
    private String mPressure;
    private String mOzone;
    private String mUnits;
    private Context mContext;


    public String getPrecipitationIntensityString() {
        return mPrecipitationIntensityString;
    }

    public void setPrecipitationIntensityString(String precipitationIntensityString) {
        mPrecipitationIntensityString = precipitationIntensityString;
    }

    public int getIconId(String theme,Context context) {
        return TempestatibusApplicationSettings.getIconId(getIcon(),theme,context);
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        mHumidity = humidity;
    }

    public String getPrecipitationProbability() {
        return mPrecipitationProbability;
    }

    public void setPrecipitationProbability(String precipitationProbability) {
        mPrecipitationProbability = precipitationProbability;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }


    public double getPrecipitationIntensity() {
        return mPrecipitationIntensity;
    }

    public void setPrecipitationIntensity(double precipitationIntensity) {
        mPrecipitationIntensity = precipitationIntensity;
    }

    public String getPrecipitationType() {
        return mPrecipitationType;
    }

    public void setPrecipitationType(String precipitationType) {
        mPrecipitationType = precipitationType;
    }

    public String getDewPoint() {
        return mDewPoint;
    }

    public void setDewPoint(String dewPoint) {
        mDewPoint = dewPoint;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        mWindSpeed = windSpeed;
    }

    public String getWindBearing() {
        return mWindBearing;
    }

    public void setWindBearing(String windBearing) {
        mWindBearing = windBearing;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public void setVisibility(String visibility) {
        mVisibility = visibility;
    }

    public String getCloudCover() {
        return mCloudCover;
    }

    public void setCloudCover(String cloudCover) {
        mCloudCover = cloudCover;
    }

    public String getPressure() {
        return mPressure;
    }

    public void setPressure(String pressure) {
        mPressure = pressure;
    }

    public String getOzone() {
        return mOzone;
    }

    public void setOzone(String ozone) {
        mOzone = ozone;
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    protected WeatherData(Parcel in) {
        setTime(in.readLong());
        setIcon(in.readString());
        setHumidity(in.readString());
        setPrecipitationProbability(in.readString());
        setSummary(in.readString());
        setPrecipitationIntensity(in.readDouble());
        setPrecipitationType(in.readString());
        setDewPoint(in.readString());
        setWindSpeed(in.readString());
        setWindBearing(in.readString());
        setVisibility(in.readString());
        setCloudCover(in.readString());
        setPressure(in.readString());
        setOzone(in.readString());
        setUnits(in.readString());
        setPrecipitationIntensityString(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getTime());
        dest.writeString(getIcon());
        dest.writeString(getHumidity());
        dest.writeString(getPrecipitationProbability());
        dest.writeString(getSummary());
        dest.writeDouble(getPrecipitationIntensity());
        dest.writeString(getPrecipitationType());
        dest.writeString(getDewPoint());
        dest.writeString(getWindSpeed());
        dest.writeString(getWindBearing());
        dest.writeString(getVisibility());
        dest.writeString(getCloudCover());
        dest.writeString(getPressure());
        dest.writeString(getOzone());
        dest.writeString(getUnits());
        dest.writeString(getPrecipitationIntensityString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };

    public WeatherData() { super(); }

    public WeatherData(Context context, JSONObject data, String units) throws JSONException {
        setContext(context); setUnits(units);
        jsonToDataMembers(data);
    }

    private void jsonToDataMembers(JSONObject dataObject) throws JSONException {
        extractHumidity(dataObject); extractTime(dataObject);
        extractIcon(dataObject); extractPrecipitationProbability(dataObject);
        extractSummary(dataObject);  extractPrecipitationIntensity(dataObject);
        extractDewPoint(dataObject);  extractWindSpeed(dataObject);
        extractVisibility(dataObject); extractCloudCover(dataObject);
        extractPressure(dataObject);  extractOzone(dataObject);
        extractPrecipitationType(dataObject); extractWindBearing(dataObject);
    }

    private void extractWindBearing(JSONObject dataObject) throws JSONException {
        if (!getWindSpeed().equals(mContext.getString(R.string.empty_two_digit_number)) &&
                dataObject.has(mContext.getString(R.string.JSON_double_property_windBearing))) {
            double windBearing = dataObject.getDouble(mContext.getString(R.string.JSON_double_property_windBearing));
            String direction = translateBearingToDirection(windBearing);
            setWindBearing(direction);
        }
        else {
            setWindBearing(-1 + ""); //sentinel
       }    } 

    public String translateBearingToDirection(double windBearing) {
        if(windBearing >= 0 && windBearing  < 90) {
            return firstQuadrant(windBearing);
        }
        else if(windBearing >= 90 && windBearing  < 180) {
            return secondQuadrant(windBearing);
        }
        else if(windBearing >= 180 && windBearing  < 270) {
            return thirdQuadrant(windBearing);
        }
        else if(windBearing >= 270 && windBearing  < 360) {
            return fourthQuadrant(windBearing);
        }
        return mContext.getString(R.string.empty_two_digit_number);
    }

    private String firstQuadrant(double windBearing) {
        
        if(windBearing >= 0 && windBearing < 11.25) {
            return "N";
        }
        else if(windBearing >= 11.25 && windBearing < 33.75) {
            return "NNE";
        }
        else if(windBearing >= 33.75 && windBearing < 56.25) {
            return "NE";
        }
        else if(windBearing >= 56.25 && windBearing < 78.75) {
            return "ENE";
        }
        else {
            return "E";
        }
    }
    
    private String secondQuadrant(double windBearing) {
        if(windBearing >= 78.75 && windBearing < 101.25) {
            return "E";
        }
        else if(windBearing >= 101.25 && windBearing < 123.75) {
            return "ESE";
        }
        else if(windBearing >= 123.75 && windBearing < 146.25) {
            return "SE";
        }
        else if(windBearing >= 146.25 && windBearing < 168.75) {
            return "SSE";
        }
        else {
            return "S";
        }
    }
    
    private String thirdQuadrant(double windBearing) {
        if(windBearing >= 168.75 && windBearing < 191.25) {
            return "S";
        }
        else if(windBearing >= 191.25 && windBearing < 213.75) {
            return "SSW";
        }
        else if(windBearing >= 213.75 && windBearing < 236.25) {
            return "SW";
        }
        else if(windBearing >= 236.25 && windBearing < 258.75) {
            return "WSW";
        }
        else {
            return "W";
        }
    }
    
    private String fourthQuadrant(double windBearing) {
        if(windBearing >= 258.75 && windBearing < 281.25) {
            return "W";
        }
        else if(windBearing >= 281.25 && windBearing < 303.75) {
            return "WNW";
        }
        else if(windBearing >= 303.75 && windBearing < 326.25) {
            return "NW";
        }
        else if(windBearing >= 326.25 && windBearing < 348.75) {
            return "NNW";
        }
        else {
            return "N";
        }
    }
    
    private void extractPrecipitationType(JSONObject dataObject) throws JSONException {
        //Conditional data items
        if(getPrecipitationIntensity() != 0 &&
                dataObject.has(mContext.getString(R.string.JSON_double_property_precipitationType))) {
            setPrecipitationType(dataObject.getString(mContext.getString(R.string.JSON_double_property_precipitationType)));
        }
        else {
            setPrecipitationType(-1 + ""); //sentinel
        }
    }

    private void extractOzone(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_ozone))) {
            setOzone((int) Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_ozone))) + "");
        }
        else {
            setOzone(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractPressure(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_pressure))) {
            setPressure((int) Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_pressure))) + "");
        }
        else {
            setPressure(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractCloudCover(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_cloudCover))) {
            double cover = dataObject.getDouble(mContext.getString(R.string.JSON_double_property_cloudCover));
            setCloudCover(translateCloudCover(cover));
        }
        else {
            setCloudCover(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private String translateCloudCover(double cover) {
        if(cover >= 0 && cover < .4) {
            return "Clear";
        }
        else if(cover >= .4 && cover < .75 ) {
            return "Scattered";
        }
        else if(cover >= .75 && cover < 1) {
            return "Broken";
        }
        else {
            return "Overcast";
        }
    }

    private void extractVisibility(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_visibility))) {
            setVisibility((int)Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_visibility)))+"");
        }
        else {
            setVisibility(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractWindSpeed(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_windSpead))) {
            setWindSpeed((int) Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_windSpead))) + "");
        }
        else {
            setWindSpeed(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractDewPoint(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_dewPoint))) {
            setDewPoint((int) Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_dewPoint))) + "");
        }
        else {
            setDewPoint(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractPrecipitationIntensity(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_precipitationIntensity))) {
            double intensity = dataObject.getDouble(mContext.getString(R.string.JSON_double_property_precipitationIntensity));
            setPrecipitationIntensity(intensity);
            setPrecipitationIntensityString(translatePrecipitationIntensity(intensity));
        }
        else {
            setPrecipitationIntensity(-1);
        }
    }

    protected String translatePrecipitationIntensity(double intensity) {
        if(intensity >= 0 && intensity < .002) {
            return "None";
        }
        else if(intensity >= .002 && intensity < .017) {
            return "Very light";
        }
        else if(intensity >= .017 && intensity < .1) {
            return "Light";
        }
        if(intensity >= .1 && intensity < .4) {
            return "Moderate";
        }
        else {
            return "Heavy";
        }
    }

    private void extractSummary(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_string_property_summary))) {
            setSummary(dataObject.getString(mContext.getString(R.string.JSON_string_property_summary)));
        }
        else {
            setSummary(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractPrecipitationProbability(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_precip_probability))) {
            String probability = ""+(int)Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_precip_probability))*100);
            setPrecipitationProbability(probability);
        }
        else {
            setPrecipitationProbability(mContext.getString(R.string.empty_two_digit_number));
        }
    }

    private void extractIcon(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_string_property_icon))) {
            setIcon(dataObject.getString(mContext.getString(R.string.JSON_string_property_icon)));
        }
        else {
            //Need to get an error icon going...
            setIcon(dataObject.getString(mContext.getString(R.string.partly_cloudy_day)));
        }
    }

    private void extractTime(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_long_property_time))) {
            setTime(dataObject.getLong(mContext.getString(R.string.JSON_long_property_time)));
        }
        else{
            throw new JSONException(mContext.getString(R.string.exception_caught_text));
        }
    }

    private void extractHumidity(JSONObject dataObject) throws JSONException {
        if(dataObject.has(mContext.getString(R.string.JSON_double_property_humidity))) {
            setHumidity((int)Math.round(dataObject.getDouble(mContext.getString(R.string.JSON_double_property_humidity))*100)+"");
        }
        else {
            setHumidity(mContext.getString(R.string.empty_two_digit_number));
        }
    }
}
