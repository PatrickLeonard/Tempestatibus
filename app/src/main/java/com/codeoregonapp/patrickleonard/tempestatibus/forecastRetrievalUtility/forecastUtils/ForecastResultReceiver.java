package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;


/**
 * This receiver gets the results from the ForecastFetchIntentService and reacts to them in the
 * MainActivity
 * Created by Patrick Leonard on 12/31/2015.
 */
public class ForecastResultReceiver extends ResultReceiver {

    public static final String TAG = ForecastResultReceiver.class.getSimpleName();

    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public String mForecastOutput;

    public ForecastResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        mForecastOutput = resultData.getString(ForecastFetchConstants.RESULT_DATA_KEY);
        //Send the JSON data results to the JSON Data Extractor
        if (resultCode == ForecastFetchConstants.SUCCESS_RESULT) {
            mForecastRetrievalService.fetchForecastSuccess(mForecastOutput);
        } //Send an error if the network was not available
        else {
            mForecastRetrievalService.fetchForecastFailure(mForecastOutput);
        }
    }
}
