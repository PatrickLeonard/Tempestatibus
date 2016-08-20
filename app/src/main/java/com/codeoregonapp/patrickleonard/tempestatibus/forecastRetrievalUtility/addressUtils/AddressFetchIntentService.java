package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils;

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
import java.util.List;
import java.util.Locale;

/**
 * This IntentService uses GoogleAPI Geocoder to retrieve the address associated with the phones
 * physical location.
 * Created by Patrick Leonard on 11/8/2015.
 */
public class AddressFetchIntentService extends IntentService {

    public static final String TAG = AddressFetchIntentService.class.getSimpleName();
    private ResultReceiver mReceiver;
    private String mStandardAddress;
    private String mShortenedAddress;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AddressFetchIntentService(String name) {
        super(name);
    }

    public AddressFetchIntentService() { super("No args"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(AddressFetchConstants.RECEIVER);
        Location location = intent.getParcelableExtra(AddressFetchConstants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        if(Geocoder.isPresent()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // The first address should give us the City, State, Country info
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.geolocation_service_not_available);
                Log.e(AddressFetchIntentService.TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_and_or_long);
                Log.e(AddressFetchIntentService.TAG, errorMessage + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(AddressFetchIntentService.TAG, errorMessage);
                }
                deliverResultToReceiver(AddressFetchConstants.FAILURE_RESULT, null,null,errorMessage);
            } else {
                Address address = addresses.get(0);
                createAddressStrings(address);
                deliverResultToReceiver(AddressFetchConstants.SUCCESS_RESULT,
                        mStandardAddress, mShortenedAddress,null);
            }
        }
        else {
            deliverResultToReceiver(AddressFetchConstants.NOT_PRESENT,
                    null,null,getString(R.string.no_geocode_present));
        }
    }

    private void createAddressStrings(Address address) {
        mStandardAddress = "";
        mShortenedAddress = "";
        int i = 0;
        int j = 1;
        for (;i<=address.getMaxAddressLineIndex();++i) {
            mStandardAddress += address.getAddressLine(i);
            mStandardAddress += " ";
        }

        if(address.getMaxAddressLineIndex() == 1) {
            mShortenedAddress = mStandardAddress;
        }
        else {
            for (; j <= address.getMaxAddressLineIndex(); ++j) {
                mShortenedAddress += address.getAddressLine(j);
                mShortenedAddress += " ";
            }
        }
    }

    //Deliver results bas to the calling thread
    private void deliverResultToReceiver(int resultCode, String standardMessage,String shortenedMessage,String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(AddressFetchConstants.ERROR_MESSAGE_DATA_KEY,errorMessage);
        bundle.putString(AddressFetchConstants.STANDARD_RESULT_DATA_KEY, standardMessage);
        bundle.putString(AddressFetchConstants.SHORTENED_RESULT_DATA_KEY, shortenedMessage);
        mReceiver.send(resultCode, bundle);
    }
}
