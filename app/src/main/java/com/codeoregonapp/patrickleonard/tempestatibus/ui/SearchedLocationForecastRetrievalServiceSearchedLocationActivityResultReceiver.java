package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

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
    public int mResultCode;

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Retrieve any error messages, will 0 and NULL respectively if none exist
        int errorCode = resultData.getInt(SearchedLocationForecastRetrievalServiceConstants.ERROR_CODE_KEY);
        String errorMessage = resultData.getString(SearchedLocationForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY);
        if (resultCode == ForecastRetrievalServiceConstants.SUCCESS_RESULT) {
            //get the data from the successful retrieval of the Forecast data
            mResultCode = resultCode;
            Forecast forecast = resultData.getParcelable(SearchedLocationForecastRetrievalServiceConstants.RESULT_DATA_KEY);
            //Set the data in the Main Activity and update the UI Views
            mSearchedLocationActivity.setForecast(forecast);
            Log.v(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Calling updateCurrentDisplay from: " + Thread.currentThread().getName());
            mSearchedLocationActivity.updateCurrentDisplay(mSearchedLocationActivity.getForecast());
        }
        else {
            logError(resultCode, errorCode, errorMessage);
            mSearchedLocationActivity.alertUserAboutError();
        }
    }

    //Detailed error log of the retrieval attempt
    private void logError(int resultCode, int errorCode, String errorMessage) {
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG, "Result failure in onReceiveResult");
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Result code: " + resultCode);
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Result code: " + errorCode);
        Log.e(SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver.TAG,"Error Message:" + errorMessage);
    }
}
