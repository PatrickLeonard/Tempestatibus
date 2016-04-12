package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;

/**
 * Result Receive for connecting to the Google Services API
 * Created by Patrick Leonard on 11/9/2015.
 */
public class GoogleAPIConnectionResultReceiver extends ResultReceiver {

    public static final String TAG = GoogleAPIConnectionResultReceiver.class.getSimpleName();

    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public int mResultCode;


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
            mResultCode = resultCode;
            mForecastRetrievalService.startAddressFetchIntentService();
        }
        //Or there is an error that is being attempted to resolve
        else if ((resultCode == GoogleAPIConnectionConstants.REQUEST_RESOLVE_ERROR) ||
                (resultCode == GoogleAPIConnectionConstants.CONNECTION_SUSPENDED)) {
            mForecastRetrievalService.deliverGoogleError(errorCode, "Attempting to resolve request");
        }
        else {
            //Or there was some other error to be delivered
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Result failure in onReceiveResult");
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Result code: " + resultCode);
            Log.e(GoogleAPIConnectionResultReceiver.TAG, "Error Code: " + errorCode);
            mForecastRetrievalService.deliverGoogleError(errorCode,"Google API Connection Problem");
        }

    }
}
