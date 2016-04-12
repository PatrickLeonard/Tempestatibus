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
        Location currentLocation = intent.getParcelableExtra(SearchedAddressFetchConstants.CURRENT_LOCATION_DATA);
        double range = 10.00;
        String enteredText = intent.getStringExtra(SearchedAddressFetchConstants.ENTERED_TEXT_KEY);
        List<Address> addresses = null;
        if(Geocoder.isPresent()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                double lowerLeftLat = getLowerLeftLat(currentLocation,range);
                double lowerLeftLong = getLowerLeftLong(currentLocation, range);
                double upperRightLat = getUpperRightLat(currentLocation, range);
                double upperRightLong = getUpperRightLong(currentLocation, range);
                addresses = geocoder.getFromLocationName(enteredText,5, lowerLeftLat, lowerLeftLong,
                                                                        upperRightLat,upperRightLong);
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

    private double getLowerLeftLat(Location currentLocation,double range) {
        double currentLat = currentLocation.getLatitude();
        double lowerLeftLat = currentLat-range;
        if(lowerLeftLat < -90.00) {
            return (-90.00-(90.00+lowerLeftLat));
        }
        else {
            return lowerLeftLat;
        }
    }

    private double getLowerLeftLong(Location currentLocation,double range) {
        double currentLong = currentLocation.getLongitude();
        double lowerLeftLong = currentLong-range;
        if(lowerLeftLong < -180.00) {
            return (-180.00-(180.00+lowerLeftLong));
        }
        else {
            return lowerLeftLong;
        }
    }

    private double getUpperRightLat(Location currentLocation,double range) {
        double currentLat = currentLocation.getLatitude();
        double upperRightLat = currentLat+range;
        if(upperRightLat > 90.00) {
            return (90.00-(-90.00+upperRightLat));
        }
        else {
            return upperRightLat;
        }
    }

    private double getUpperRightLong(Location currentLocation,double range) {
        double currentLong = currentLocation.getLongitude();
        double upperRightLong = currentLong+range;
        if(upperRightLong > 180.00) {
            return (180.00-(-180.00+upperRightLong));
        }
        else {
            return upperRightLong;
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
