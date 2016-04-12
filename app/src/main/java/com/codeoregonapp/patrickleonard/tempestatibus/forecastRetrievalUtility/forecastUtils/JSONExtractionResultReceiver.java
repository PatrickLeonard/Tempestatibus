package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * This receiver gets the results from the JSONExtractionIntentService and reacts to them in the
 * MainActivity
 * Created by Patrick Leonard on 12/31/2015.
 */
public class JSONExtractionResultReceiver extends ResultReceiver {

    public static final String TAG = JSONExtractionResultReceiver.class.getSimpleName();

    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public int mResultCode;

    public JSONExtractionResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //If the JSON Extraction was successful, deliver the Forecast Data Model to the ForecastRetrievalService
        if (resultCode == JSONExtractionConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            Forecast mForecastOutput = resultData.getParcelable(JSONExtractionConstants.RESULT_DATA_KEY);
            mForecastRetrievalService.setForecast(mForecastOutput);
            mForecastRetrievalService.setLastSuccessfulRequestSystemTime(System.currentTimeMillis());
            mForecastRetrievalService.deliverForecastAndAddress();
        }
        else {
            //Or deliver an error code and message
            String errorMessage = resultData.getParcelable(JSONExtractionConstants.RESULT_DATA_KEY);
            mForecastRetrievalService.deliverError(resultCode,errorMessage);
        }
    }
}
