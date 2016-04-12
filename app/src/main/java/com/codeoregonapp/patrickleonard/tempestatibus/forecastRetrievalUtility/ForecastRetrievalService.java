package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils.AddressFetchConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils.AddressFetchIntentService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils.AddressResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.JSONExtractionConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.JSONExtractionIntentService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.JSONExtractionResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils.GoogleAPIConnectionConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils.GoogleAPIConnectionResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils.GoogleAPIConnectionService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils.LocationFetchConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils.LocationFetchService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils.LocationResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;
import com.codeoregonapp.patrickleonard.tempestatibus.widget.WidgetForecastUpdateService;


/**
 * Service to update the widgets as needed
 * Created by Patrick Leonard on 1/3/2016.
 */
public class ForecastRetrievalService extends Service {

    public static final String TAG = ForecastRetrievalService.class.getSimpleName();
    private Location mLocation;
    private String mStandardAddress;
    private String mShortenedAddress;
    private String mJSONData;
    private Forecast mForecast;
    private ResultReceiver mMainReceiver;
    private ResultReceiver mWidgetReceiver;
    private String mCallingClassName;
    private Long mLastSuccessfulRequestSystemTime;
    private boolean mIsRunning;

    public void setLastSuccessfulRequestSystemTime(Long lastSuccessfulRequestSystemTime) {
        mLastSuccessfulRequestSystemTime = lastSuccessfulRequestSystemTime;
    }

    public ResultReceiver getMainReceiver() {
        return mMainReceiver;
    }

    public void setMainReceiver(ResultReceiver mainReceiver) {
        mMainReceiver = mainReceiver;
    }

    public ResultReceiver getWidgetReceiver() {
        return mWidgetReceiver;
    }

    public void setWidgetReceiver(ResultReceiver widgetReceiver) {
        mWidgetReceiver = widgetReceiver;
    }

    public String getCallingClassName() {
        return mCallingClassName;
    }

    public void setCallingClassName(String callingClassName) {
        mCallingClassName = callingClassName;
    }

    public Forecast getForecast() {
        return mForecast;
    }

    public void setForecast(Forecast forecast) {
        mForecast = forecast;
    }

    public String getJSONData() {
        return mJSONData;
    }

    public void setJSONData(String JSONData) {
        mJSONData = JSONData;
    }

    public String getStandardAddress() {
        return mStandardAddress;
    }

    public void setStandardAddress(String standardAddress) {
        mStandardAddress = standardAddress;
    }

    public String getShortenedAddress() {
        return mShortenedAddress;
    }

    public void setShortenedAddress(String shortenedAddress) {
        mShortenedAddress = shortenedAddress;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIsRunning = false;
        mLastSuccessfulRequestSystemTime = 0L;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int ids) {
        if(intent != null) {
            retrieveForecast(intent);
        }
        return START_NOT_STICKY;
    }

    private void retrieveForecast(Intent intent) {
        //Only get a new Forecast if haven't looked in 1sec.
        Long currentTime = System.currentTimeMillis();
        Long difference = currentTime - mLastSuccessfulRequestSystemTime;
        if(!mIsRunning) {
            clearReceivers();
        }
        setReceiver(intent);
        if (difference > 2000L && !mIsRunning) {
            startLocationFetchService();
        } else if(!mIsRunning) {
            deliverForecastAndAddress();
        }
    }

    //Set the receiver that called to send the Calling class name
    private void setReceiver(Intent intent) {
        setCallingClassName(intent.getStringExtra(ForecastRetrievalServiceConstants.CALLING_CLASS_NAME_DATA_EXTRA));
        if(getCallingClassName().equals(MainActivity.TAG)) {
            setMainReceiver((ResultReceiver) intent.getParcelableExtra(ForecastRetrievalServiceConstants.MAIN_ACTIVITY_RECEIVER));
        }
        else if(getCallingClassName().equals(WidgetForecastUpdateService.TAG)) {
            setWidgetReceiver((ResultReceiver) intent.getParcelableExtra(ForecastRetrievalServiceConstants.WIDGET_RECEIVER));
        }
    }

    //Function to start the LocationFetchService
    protected void startLocationFetchService() {
        mIsRunning = true;
        Intent intent = new Intent(this, LocationFetchService.class);
        LocationResultReceiver resultReceiver = new LocationResultReceiver(new Handler(), this);
        intent.putExtra(LocationFetchConstants.RECEIVER, resultReceiver);
        startService(intent);
    }

    //Function to start the GoogleAPIConnectionService
    public void startGoogleAPIConnectionService() {
        Intent intent = new Intent(this, GoogleAPIConnectionService.class);
        GoogleAPIConnectionResultReceiver resultReceiver = new GoogleAPIConnectionResultReceiver(new Handler(), this);
        intent.putExtra(GoogleAPIConnectionConstants.RECEIVER, resultReceiver);
        intent.putExtra(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR,isRestricted());
        startService(intent);
    }

    //Function to start the AddressFetchIntentService
    public void startAddressFetchIntentService() {
        Intent intent = new Intent(this, AddressFetchIntentService.class);
        AddressResultReceiver resultReceiver = new AddressResultReceiver(new Handler(), this);
        intent.putExtra(AddressFetchConstants.RECEIVER, resultReceiver);
        intent.putExtra(AddressFetchConstants.LOCATION_DATA_EXTRA, mLocation);
        intent.putExtra(AddressFetchConstants.CALLING_CLASS_NAME,mCallingClassName);
        startService(intent);
    }

    //Function to start the ForecastFetchIntentService
    public void startForecastFetchIntentService() {
        if(mStandardAddress.equals(getString(R.string.no_address_found))) {
            deliverError(ForecastRetrievalServiceConstants.FAILURE_RESULT,getString(R.string.no_address_found));
        }
        else {
            Intent intent = new Intent(this, com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchIntentService.class);
            ForecastResultReceiver resultReceiver = new ForecastResultReceiver(new Handler(), this);
            intent.putExtra(ForecastFetchConstants.RECEIVER, resultReceiver);
            intent.putExtra(ForecastFetchConstants.LOCATION_DATA_EXTRA,getLocation());
            startService(intent);
        }
    }

    //Function to start the JSONExtractionIntentService
    public void startJSONExtractionIntentService() {
        if(getJSONData() == null || getJSONData().equals("")) {
            deliverError(ForecastRetrievalServiceConstants.FAILURE_RESULT,getString(R.string.error_message_empty_JSON_data));
        }
        else {
            Intent intent = new Intent(this, JSONExtractionIntentService.class);
            JSONExtractionResultReceiver resultReceiver = new JSONExtractionResultReceiver(new Handler(), this);
            intent.putExtra(JSONExtractionConstants.RECEIVER, resultReceiver);
            intent.putExtra(JSONExtractionConstants.JSON_DATA_EXTRA, getJSONData());
            startService(intent);
        }
    }

    //Deliver the Forecast data to the calling receiver
    public void deliverForecastAndAddress() {
        Bundle bundle = new Bundle();
        setLastSuccessfulRequestSystemTime(System.currentTimeMillis());
        bundle.putString(ForecastRetrievalServiceConstants.STANDARD_ADDRESS_DATA_KEY, getStandardAddress());
        bundle.putString(ForecastRetrievalServiceConstants.SHORTENED_ADDRESS_DATA_KEY, getShortenedAddress());
        bundle.putParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY, getForecast());
        sendData(ForecastRetrievalServiceConstants.SUCCESS_RESULT,bundle);
    }

    //Deliver the an error to the calling receiver
    public void deliverError(int errorCode, String message) {
        Bundle bundle = new Bundle();
        mLastSuccessfulRequestSystemTime = 0L;
        bundle.putInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY, errorCode);
        bundle.putString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY, message);
        sendData(errorCode,bundle);
    }

    //Deliver and error specific to the Google API to the calling receiver
    public void deliverGoogleError(int errorCode, String message) {
        Bundle bundle = new Bundle();
        mLastSuccessfulRequestSystemTime = 0L;
        bundle.putInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY, errorCode);
        bundle.putString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY, message);
        sendData(errorCode,bundle);
    }

    public void sendData(int resultCode, Bundle bundle) {
        if(getMainReceiver() != null) {
            getMainReceiver().send(resultCode, bundle);
        }
        if (getWidgetReceiver() != null) {
            getWidgetReceiver().send(resultCode, bundle);
        }
        mIsRunning = false;
    }

    private void clearReceivers() {
        setMainReceiver(null);
        setWidgetReceiver(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
