package com.codeoregonapp.patrickleonard.tempestatibus.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Patrick Leonard on 7/11/2016.
 */
public class WeatherAlert implements Parcelable {

    private static final String TAG = WeatherAlert.class.getSimpleName();
    private String mTitle;
    private long mTime;
    private long mExpires;
    private String mDescription;
    private String mUriString;
    private String mUUID;
    private int mID;

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        this.mUUID = UUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public long getExpires() {
        return mExpires;
    }

    public void setExpires(long mExpires) {
        this.mExpires = mExpires;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getTime()*1000);
        return formatter.format(date);
    }

    public String getFormattedExpiredTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        Date date = new Date(getExpires()*1000);
        return formatter.format(date);
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getUriString() {
        return mUriString;
    }

    public void setUriString(String mUriString) {
        this.mUriString = mUriString;
    }

    protected WeatherAlert(Parcel in) {
        mTitle = in.readString();
        mTime = in.readLong();
        mExpires = in.readLong();
        mDescription = in.readString();
        mUriString = in.readString();
        mUUID = in.readString();
        mID = in.readInt();
    }

    public WeatherAlert(String title, long time, long expires, String description, String uriString) {
        this.mTitle = title;
        this.mTime = time;
        this.mExpires = expires;
        this.mDescription = description;
        this.mUriString = uriString;
    }

    public static final Creator<WeatherAlert> CREATOR = new Creator<WeatherAlert>() {
        @Override
        public WeatherAlert createFromParcel(Parcel in) {
            return new WeatherAlert(in);
        }

        @Override
        public WeatherAlert[] newArray(int size) {
            return new WeatherAlert[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeLong(mTime);
        dest.writeLong(mExpires);
        dest.writeString(mDescription);
        dest.writeString(mUriString);
        dest.writeString(mUUID);
        dest.writeInt(mID);
    }

    @Override
    public boolean equals(Object another) {
        if(another instanceof WeatherAlert) {
            WeatherAlert alert = (WeatherAlert)another;
            boolean titleMatch = alert.getTitle().equals(this.getTitle());
            boolean timeMatch = alert.getTime() == this.getTime();
            boolean expireMatch = alert.getExpires() == this.getExpires();
            boolean descMatch = alert.getDescription().equals(this.getDescription());
            boolean uriMatch = alert.getUriString().equals(this.getUriString());
            return titleMatch && timeMatch && expireMatch && descMatch && uriMatch;
        }
        else {
            return false;
        }
    }
}
