package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This IntentService uses GoogleAPI Geocoder to retrieve the address associated with the phones
 * physical location.
 * Created by Patrick Leonard on 11/8/2015.
 */
public class SearchedAddressFetchIntentService extends IntentService {

    public static final String TAG = SearchedAddressFetchIntentService.class.getSimpleName();
    private ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SearchedAddressFetchIntentService(String name) {
        super(name);
    }

    public SearchedAddressFetchIntentService() { super("No args"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(SearchedAddressFetchConstants.RECEIVER);
        String enteredText = intent.getStringExtra(SearchedAddressFetchConstants.ENTERED_TEXT_KEY);
        List<Address> addresses = null;
        if(Geocoder.isPresent()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocationName(enteredText,5);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.geolocation_service_not_available);
                Log.e(SearchedAddressFetchIntentService.TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e(SearchedAddressFetchIntentService.TAG, getString(R.string.error_message), illegalArgumentException);
            }
            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(SearchedAddressFetchIntentService.TAG, errorMessage);
                }
                deliverErrorResultToReceiver(SearchedAddressFetchConstants.FAILURE_RESULT, errorMessage);
            } else {
                deliverResultToReceiver(SearchedAddressFetchConstants.SUCCESS_RESULT,(ArrayList<Address>)addresses);
            }
        }
        else {
            deliverErrorResultToReceiver(SearchedAddressFetchConstants.NOT_PRESENT,
                    getString(R.string.no_geocode_present));
        }
    }

    //Deliver results bas to the calling thread
    private void deliverResultToReceiver(int resultCode, ArrayList<Address> addresses) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SearchedAddressFetchConstants.RESULT_DATA_KEY, addresses);
        mReceiver.send(resultCode, bundle);
    }

    //Deliver results bas to the calling thread
    private void deliverErrorResultToReceiver(int resultCode, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(SearchedAddressFetchConstants.RESULT_DATA_KEY, errorMessage);
        mReceiver.send(resultCode, bundle);
    }
}
