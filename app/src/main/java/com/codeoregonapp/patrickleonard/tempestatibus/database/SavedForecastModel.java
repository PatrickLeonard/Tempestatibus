package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO to act as the Model for saved locations
 * Created by Patrick Leonard on 6/14/2016.
 */
public class SavedForecastModel implements Parcelable {
    private int mId;
    private String mForecastData;

    protected SavedForecastModel(Parcel in) {
        mId = in.readInt();
        mForecastData = in.readString();
    }

    public static final Creator<SavedForecastModel> CREATOR = new Creator<SavedForecastModel>() {
        @Override
        public SavedForecastModel createFromParcel(Parcel in) {
            return new SavedForecastModel(in);
        }

        @Override
        public SavedForecastModel[] newArray(int size) {
            return new SavedForecastModel[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getForecastData() {
        return mForecastData;
    }

    public void setForecastData(String mForecastData) {
        this.mForecastData = mForecastData;
    }

    public SavedForecastModel(int id, String forecastData) {
        this.mId = id;
        this.mForecastData = forecastData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mForecastData);
    }
}
