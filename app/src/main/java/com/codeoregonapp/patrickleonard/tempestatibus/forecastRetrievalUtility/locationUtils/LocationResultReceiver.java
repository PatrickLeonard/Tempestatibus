package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;


/**
 * This class receives the results from the LocationFetchService and reacts to them in the
 * MainActivity
 * Created by Patrick Leonard on 11/9/2015.
 */
public class LocationResultReceiver extends ResultReceiver {

    public static final String TAG = LocationResultReceiver.class.getSimpleName();

    public LocationResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public Location mLocationOutput;

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Show a toast message if an address was found.
        if (resultCode == LocationFetchConstants.SUCCESS_RESULT) {
            mLocationOutput = resultData.getParcelable(LocationFetchConstants.RESULT_DATA_KEY);
            mForecastRetrievalService.fetchLocationSuccess(mLocationOutput);
        }
        else {
            Log.e(LocationResultReceiver.TAG,"Result failure in onReceiveResult");
            Log.e(LocationResultReceiver.TAG,"Result code: " + resultCode);
            mForecastRetrievalService.fetchLocationFailure();
        }
    }

}
