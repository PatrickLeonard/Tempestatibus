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
    private String mAddress;

    protected SavedLocationModel(Parcel in) {
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mId = in.readInt();
        mName = in.readString();
        mAddress = in.readString();
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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
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

    public SavedLocationModel(Location location, int id, String name,String address) {
        setId(id); setLocation(location); setName(name); setAddress(address);
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
        dest.writeString(mAddress);
    }
}
