package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;
import com.google.android.gms.common.ConnectionResult;

/**
 * Result Receive for connecting to the Google Services API
 * Created by Patrick Leonard on 11/9/2015.
 */
public class GoogleAPIConnectionResultReceiver extends ResultReceiver {

    public static final String TAG = GoogleAPIConnectionResultReceiver.class.getSimpleName();

    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;


    public GoogleAPIConnectionResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //On receiving a result on if the Google API connected
        //If it did connect fetch the human address from the AddressFetchIntentService
        int errorCode = resultData.getInt(GoogleAPIConnectionConstants.RESULT_DATA_KEY);
        if (resultCode == GoogleAPIConnectionConstants.SUCCESS_RESULT) {
            mForecastRetrievalService.googleConnectionSuccess();
        }
        else {
            ConnectionResult connectionResult = resultData.getParcelable(GoogleAPIConnectionConstants.CONNECTION_RESULT_KEY);
            //Or there was some other error to be delivered
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Result failure in onReceiveResult");
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Result code: " + resultCode);
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Error Code: " + errorCode);
            mForecastRetrievalService.googleConnectionFailure(errorCode,connectionResult);
        }

    }
}
