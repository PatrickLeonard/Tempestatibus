package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;


/**
 * This class receives the results from the AddressFetchIntentService and reacts to the results in the
 * MainActivity
 * Created by Patrick Leonard on 11/9/2015.
 */
public class AddressResultReceiver extends ResultReceiver {

    public static final String TAG = AddressResultReceiver.class.getSimpleName();
    private final ForecastRetrievalService mForecastRetrievalService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public String mStandardAddressOutput;
    public String mShortenedAddressOutput;
    public String mErrorMessage;

    public AddressResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //If successful, assign variables and alert the calling thread
        if (resultCode == AddressFetchConstants.SUCCESS_RESULT) {
            mStandardAddressOutput = resultData.getString(AddressFetchConstants.STANDARD_RESULT_DATA_KEY);
            mShortenedAddressOutput = resultData.getString(AddressFetchConstants.SHORTENED_RESULT_DATA_KEY);
            mForecastRetrievalService.fetchAddressSuccess(mStandardAddressOutput,mShortenedAddressOutput);
        }
        //If Geocoder isn't present deliver that no location was found
        else  {
            mErrorMessage = resultData.getString(AddressFetchConstants.ERROR_MESSAGE_DATA_KEY);
            mForecastRetrievalService.fetchAddressFailure(mErrorMessage);
        }
    }
}
