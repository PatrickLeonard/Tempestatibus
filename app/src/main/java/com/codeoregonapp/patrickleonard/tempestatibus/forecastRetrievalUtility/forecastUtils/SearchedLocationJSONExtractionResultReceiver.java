package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.SearchedLocationForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * This receiver gets the results from the JSONExtractionIntentService and reacts to it in the
 * SearchedLocationForecastRetrievalService
 * Created by Patrick Leonard on 2/6/2016.
 */
public class SearchedLocationJSONExtractionResultReceiver extends ResultReceiver {

    public static final String TAG = SearchedLocationJSONExtractionResultReceiver.class.getSimpleName();

    public static Creator CREATOR = ResultReceiver.CREATOR;
    public int mResultCode;
    private SearchedLocationForecastRetrievalService mSearchedLocationForecastRetrievalService;
    public SearchedLocationJSONExtractionResultReceiver(Handler handler, SearchedLocationForecastRetrievalService searchedLocationForecastRetrievalService) {
        super(handler); mSearchedLocationForecastRetrievalService = searchedLocationForecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //If the JSON Extraction was successful, deliver the Forecast Data Model to the ForecastRetrievalService
        if (resultCode == JSONExtractionConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            Forecast mForecastOutput = resultData.getParcelable(JSONExtractionConstants.RESULT_DATA_KEY);
            mSearchedLocationForecastRetrievalService.setForecast(mForecastOutput);
            mSearchedLocationForecastRetrievalService.deliverForecast();
        }
        else {
            //Or deliver an error code and message
            String errorMessage = resultData.getParcelable(JSONExtractionConstants.RESULT_DATA_KEY);
            mSearchedLocationForecastRetrievalService.deliverError(JSONExtractionConstants.FAILURE_RESULT,errorMessage);
        }
    }
}