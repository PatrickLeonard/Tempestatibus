package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO to act as the Model for saved locations
 * Created by Patrick Leonard on 2/9/2016.
 */
public class SavedLocationModel implements Parcelable {

    private Location mLocation;
    private int mId;
    private String mName;
    private String mStandardAddress;
    private String mShortenedAddress;

    protected SavedLocationModel(Parcel in) {
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mId = in.readInt();
        mName = in.readString();
        mStandardAddress = in.readString();
        mShortenedAddress = in.readString();
    }

    public static final Creator<SavedLocationModel> CREATOR = new Creator<SavedLocationModel>() {
        @Override
        public SavedLocationModel createFromParcel(Parcel in) {
            return new SavedLocationModel(in);
        }

        @Override
        public SavedLocationModel[] newArray(int size) {
            return new SavedLocationModel[size];
        }
    };

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getStandardAddress() {
        return mStandardAddress;
    }

    public void setStandardAddress(String standardAddress) {
        mStandardAddress = standardAddress;
    }

    public String getShortenedAddress() {
        return mShortenedAddress;
    }

    public void setShortenedAddress(String shortenedAddress) {
        mShortenedAddress = shortenedAddress;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public SavedLocationModel(Location location, int id, String name,String standardAddress,String shortenedAddress) {
        setId(id); setLocation(location); setName(name); setStandardAddress(standardAddress); setShortenedAddress(shortenedAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mLocation, flags);
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mStandardAddress);
        dest.writeString(mShortenedAddress);
    }
}
