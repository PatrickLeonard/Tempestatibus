package com.codeoregonapp.patrickleonard.tempestatibus.ui;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils.GoogleAPIConnectionConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;
import com.google.android.gms.common.ConnectionResult;

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

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Retrieve any error messages, will be 0 and NULL respectively if none exist
        int errorCode = resultData.getInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY);
        String errorMessage = resultData.getString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY);
        displayResult(resultData); //Display the resulting data, even if an error occurred.
        //Handle the error after displaying the data
        switch(resultCode) {
            case ForecastRetrievalServiceConstants.FAILURE_RESULT:  { //Display a generic error message
                mMainActivity.alertUserAboutError(mMainActivity.getString(R.string.error_title),mMainActivity.getString(R.string.error_message),resultCode); break;
            }
            case ForecastRetrievalServiceConstants.GOOGLE_CONNECTION_ERROR:  { //Display the Google error message
                ConnectionResult connectionResult = resultData.getParcelable(GoogleAPIConnectionConstants.CONNECTION_RESULT_KEY);
                mMainActivity.showGoogleErrorDialog(connectionResult); break;
            }
            case ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT: { //Display the incoming location error message
                if(!mMainActivity.getRefusedSettings()) {
                    errorMessage = "Open Settings?";
                    mMainActivity.alertUserAboutError(mMainActivity.getString(R.string.no_location_found_address),errorMessage,resultCode);
                } break;
            }
            case ForecastRetrievalServiceConstants.NETWORK_FAILURE_RESULT: { //Display the incoming network error message
                if(!mMainActivity.getRefusedSettings()) {
                    errorMessage = "Open Settings?";
                    mMainActivity.alertUserAboutError(mMainActivity.getString(R.string.network_unavailable_text),errorMessage,resultCode);
                } break;
            }
            case ForecastRetrievalServiceConstants.SUCCESS_RESULT: {
                break; // do nothing, no error
            }
            default: {
                logError(resultCode,errorCode,errorMessage);
            }
        }
    }

    private void displayResult(Bundle resultData) {
        Forecast forecast = resultData.getParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY);
        String standardAddress = resultData.getString(ForecastRetrievalServiceConstants.STANDARD_ADDRESS_DATA_KEY);
        String shortenedAddress = resultData.getString(ForecastRetrievalServiceConstants.SHORTENED_ADDRESS_DATA_KEY);
        //Set the data in the Main Activity and update the UI Views
        mMainActivity.setForecast(forecast);
        mMainActivity.setStandardAddress(standardAddress);
        mMainActivity.setShortenedAddress(shortenedAddress);
        mMainActivity.updateCurrentDisplay(mMainActivity.getForecast());
    }

    //Detailed error log of the retrieval attempt
    private void logError(int resultCode, int errorCode, String errorMessage) {
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG, "Result failure in onReceiveResult");
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Result code: " + resultCode);
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Error code: " + errorCode);
        Log.e(ForecastRetrievalServiceMainActivityResultReceiver.TAG,"Error Message:" + errorMessage);
    }
}
