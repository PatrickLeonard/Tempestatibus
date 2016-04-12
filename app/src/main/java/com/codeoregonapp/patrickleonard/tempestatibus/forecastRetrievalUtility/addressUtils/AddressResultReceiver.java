package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
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
    public int mResultCode;

    public AddressResultReceiver(Handler handler, ForecastRetrievalService forecastRetrievalService) {
        super(handler);
        mForecastRetrievalService = forecastRetrievalService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //If successful, assign variables and alert the calling thread
        if (resultCode == AddressFetchConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            mStandardAddressOutput = resultData.getString(AddressFetchConstants.STANDARD_RESULT_DATA_KEY);
            mShortenedAddressOutput = resultData.getString(AddressFetchConstants.SHORTENED_RESULT_DATA_KEY);
            mForecastRetrievalService.setStandardAddress(mStandardAddressOutput);
            mForecastRetrievalService.setShortenedAddress(mShortenedAddressOutput);
            mForecastRetrievalService.startForecastFetchIntentService();
        }
        //If Geocoder isn't present deliver that no location was found
        else if(resultCode == AddressFetchConstants.NOT_PRESENT) {
            mResultCode = resultCode;
            mStandardAddressOutput = mForecastRetrievalService.getString(R.string.no_location_found_address);
            mForecastRetrievalService.setStandardAddress(mStandardAddressOutput);
            mForecastRetrievalService.setShortenedAddress(mStandardAddressOutput);
            mForecastRetrievalService.deliverError(mResultCode, mStandardAddressOutput);
        }
        else {
            //Deliver whatever generic error occurred
            String errorMessage = resultData.getString(AddressFetchConstants.STANDARD_RESULT_DATA_KEY);
            mForecastRetrievalService.deliverError(resultCode,errorMessage);
        }
    }
}
