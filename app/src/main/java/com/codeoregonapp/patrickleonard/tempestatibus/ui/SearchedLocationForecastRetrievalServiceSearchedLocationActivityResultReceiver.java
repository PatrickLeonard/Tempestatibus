package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.SearchedLocationForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * This receiver gets the results from the SearchedLocationForecastRetrievalService and reacts to them in the
 * SearchedLocationActivity
 * Created by Patrick Leonard on 2/6/2016
 * */
public class SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver extends ResultReceiver {

    public static final String TAG = SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.class.getSimpleName();

    public SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver(Handler handler, SearchedLocationActivity searchedLocationActivity) {
        super(handler);
        mSearchedLocationActivity = searchedLocationActivity;
    }

    private final SearchedLocationActivity mSearchedLocationActivity;
    public static Creator CREATOR = ResultReceiver.CREATOR;

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Retrieve any error messages, will 0 and NULL respectively if none exist
        int errorCode = resultData.getInt(SearchedLocationForecastRetrievalServiceConstants.ERROR_CODE_KEY);
        String errorMessage = resultData.getString(SearchedLocationForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY);
        switch(resultCode) {
            case ForecastRetrievalServiceConstants.SUCCESS_RESULT : {
                displayResult(resultData);

                break;
            }
            case ForecastRetrievalServiceConstants.NETWORK_FAILURE_RESULT: { //Display the incoming network error message
                errorMessage = "Open Settings?";
                mSearchedLocationActivity.alertUserAboutError(mSearchedLocationActivity.getString(R.string.network_unavailable_text),errorMessage,resultCode);
                break;
            }
            default: {
                logError(resultCode, errorCode, errorMessage);
                mSearchedLocationActivity.alertUserAboutError(mSearchedLocationActivity.getString(R.string.error_title),mSearchedLocationActivity.getString(R.string.error_message),resultCode); break;
            }
        }

    }

    private void displayResult(Bundle resultData) {
        //get the data from the successful retrieval of the Forecast data
        Forecast forecast = resultData.getParcelable(SearchedLocationForecastRetrievalServiceConstants.RESULT_DATA_KEY);
        //Set the data in the Main Activity and updateLocation the UI Views
        mSearchedLocationActivity.setForecast(forecast);
        mSearchedLocationActivity.updateCurrentDisplay(mSearchedLocationActivity.getForecast());
    }

    //Detailed error log of the retrieval attempt
    private void logError(int resultCode, int errorCode, String errorMessage) {
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG, "Result failure in onReceiveResult");
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Result code: " + resultCode);
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Result code: " + errorCode);
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Error Message:" + errorMessage);
    }
}
