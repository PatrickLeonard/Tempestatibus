package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.SearchedLocationForecastRetrievalService;

/**
 * This receiver gets the results from the ForecastFetchIntentService and reacts to them in the
 * SearchedLocationForecastRetrievalService
 * Created by Patrick Leonard on 2/6/2016.
 */
public class SearchedLocationForecastResultReceiver extends ResultReceiver {

    public static final String TAG = SearchedLocationForecastResultReceiver.class.getSimpleName();

    public static Creator CREATOR = ResultReceiver.CREATOR;
    public String mForecastOutput;
    private SearchedLocationForecastRetrievalService mSearchedLocationForecastRetrievalService;
    public int mResultCode;

    public SearchedLocationForecastResultReceiver(Handler handler, SearchedLocationForecastRetrievalService searchedLocationForecastRetrievalService) {
        super(handler); mSearchedLocationForecastRetrievalService = searchedLocationForecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Send the JSON data results to the JSON Data Extractor
        if (resultCode == ForecastFetchConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            mForecastOutput = resultData.getString(ForecastFetchConstants.RESULT_DATA_KEY);
            mSearchedLocationForecastRetrievalService.setJSONData(mForecastOutput);
            mSearchedLocationForecastRetrievalService.startJSONExtractionIntentService();
        }
        //Send an error if the network was not available
        else if(resultCode == ForecastFetchConstants.NOT_PRESENT) {
            mResultCode = resultCode;
        }
        //Send a generic error
        else {
            String errorMessage = resultData.getString(ForecastFetchConstants.RESULT_DATA_KEY);
            mSearchedLocationForecastRetrievalService.deliverError(ForecastFetchConstants.FAILURE_RESULT,errorMessage);
        }
    }
}
