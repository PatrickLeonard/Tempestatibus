package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * This receiver gets the results from the ForecastRetrievalService and reacts to them in the
 * MainActivity
 * Created by Patrick Leonard on 12/31/2015.
 */
public class ForecastRetrievalServiceMainActivityResultReceiver extends ResultReceiver {

    public static final String TAG = ForecastRetrievalServiceMainActivityResultReceiver.class.getSimpleName();

    public ForecastRetrievalServiceMainActivityResultReceiver(Handler handler, MainActivity mainActivity) {
        super(handler);
        mMainActivity = mainActivity;
    }

    private final MainActivity mMainActivity;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public int mResultCode;

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Retrieve any error messages, will 0 and NULL respectively if none exist
        int errorCode = resultData.getInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY);
        String errorMessage = resultData.getString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY);
        if (resultCode == ForecastRetrievalServiceConstants.SUCCESS_RESULT) {
            //get the data from the successful retrieval of the Forecast data
            mResultCode = resultCode;
            Forecast forecast = resultData.getParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY);
            String address = resultData.getString(ForecastRetrievalServiceConstants.STANDARD_ADDRESS_DATA_KEY);
            //Set the data in the Main Activity and update the UI Views
            mMainActivity.setForecast(forecast);
            mMainActivity.setAddress(address);
            Log.v(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Calling updateCurrentDisplay from: " + Thread.currentThread().getName());
            mMainActivity.updateCurrentDisplay(mMainActivity.getForecast());
        }
        //Deliver and error to the user otherwise
        else if(resultCode == ForecastRetrievalServiceConstants.GOOGLE_CONNECTION_ERROR) {
            logError(resultCode, errorCode, errorMessage);
            mMainActivity.showErrorDialog(errorCode);
        }
        else {
            logError(resultCode, errorCode, errorMessage);
            mMainActivity.alertUserAboutError();
        }
    }

    //Detailed error log of the retrieval attempt
    private void logError(int resultCode, int errorCode, String errorMessage) {
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG, "Result failure in onReceiveResult");
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Result code: " + resultCode);
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Result code: " + errorCode);
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Error Message:" + errorMessage);
    }
}
