package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
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
    public int mResultCode;

    public ForecastResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //Send the JSON data results to the JSON Data Extractor
        if (resultCode == ForecastFetchConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            mForecastOutput = resultData.getString(ForecastFetchConstants.RESULT_DATA_KEY);
            mForecastRetrievalService.setJSONData(mForecastOutput);
            mForecastRetrievalService.startJSONExtractionIntentService();
        }
        //Send an error if the network was not available
        else if(resultCode == ForecastFetchConstants.NOT_PRESENT) {
            mResultCode = resultCode;
            String forecastOutput = mForecastRetrievalService.getString(R.string.network_unavailable_text);
            mForecastRetrievalService.setStandardAddress(forecastOutput);
            mForecastRetrievalService.deliverError(mResultCode,mForecastOutput);
        }
        //Send a generic error
        else {
            String errorMessage = resultData.getString(ForecastFetchConstants.RESULT_DATA_KEY);
            mForecastRetrievalService.deliverError(resultCode,errorMessage);
        }
    }
}
