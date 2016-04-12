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
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.JSONExtractionConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.JSONExtractionIntentService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.SearchedLocationForecastResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.SearchedLocationJSONExtractionResultReceiver;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * Service to supply user with Forecast data for searched locations
 * Created by Patrick Leonard on 2/6/2016.
 */
public class SearchedLocationForecastRetrievalService extends Service {

    public static final String TAG = SearchedLocationForecastRetrievalService.class.getSimpleName();
    private Location mLocation;
    private String mJSONData;
    private Forecast mForecast;
    private ResultReceiver mSearchedLocationReceiver;

    public ResultReceiver getSearchedLocationReceiver() {
        return mSearchedLocationReceiver;
    }

    public void setSearchedLocationReceiver(ResultReceiver searchedLocationReceiver) {
        mSearchedLocationReceiver = searchedLocationReceiver;
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

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int ids) {
        if(intent != null) {
            retrieveForecast(intent);
        }
        return START_NOT_STICKY;
    }

    private void retrieveForecast(Intent intent) {
        setReceiver(intent);
        mLocation = intent.getParcelableExtra(SearchedLocationForecastRetrievalServiceConstants.LOCATION_DATA_EXTRA);
        if(mLocation != null) {
            startForecastFetchIntentService();
        }
    }

    //Set the receiver that called to send the Calling class name
    private void setReceiver(Intent intent) {
            setSearchedLocationReceiver((ResultReceiver) intent.getParcelableExtra(SearchedLocationForecastRetrievalServiceConstants.SEARCHED_LOCATION_ACTIVITY_RECEIVER));
    }

    //Function to start the ForecastFetchIntentService
    public void startForecastFetchIntentService() {
        Intent intent = new Intent(this, com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchIntentService.class);
        SearchedLocationForecastResultReceiver searchedLocationForecastResultReceiver = new SearchedLocationForecastResultReceiver(new Handler(), this);
        intent.putExtra(ForecastFetchConstants.RECEIVER, searchedLocationForecastResultReceiver);
        intent.putExtra(ForecastFetchConstants.LOCATION_DATA_EXTRA,getLocation());
        startService(intent);
    }

    //Function to start the JSONExtractionIntentService
    public void startJSONExtractionIntentService() {
        if(getJSONData() == null || getJSONData().equals("")) {
            deliverError(ForecastRetrievalServiceConstants.FAILURE_RESULT,getString(R.string.error_message_empty_JSON_data));
        }
        else {
            Intent intent = new Intent(this, JSONExtractionIntentService.class);
            SearchedLocationJSONExtractionResultReceiver searchedLocationJSONExtractionResultReceiver = new SearchedLocationJSONExtractionResultReceiver(new Handler(), this);
            intent.putExtra(JSONExtractionConstants.RECEIVER, searchedLocationJSONExtractionResultReceiver);
            intent.putExtra(JSONExtractionConstants.JSON_DATA_EXTRA, getJSONData());
            startService(intent);
        }
    }

    //Deliver the Forecast data to the calling receiver
    public void deliverForecast() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY, getForecast());
        sendData(ForecastRetrievalServiceConstants.SUCCESS_RESULT,bundle);
    }

    //Deliver the an error to the calling receiver
    public void deliverError(int errorCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY, errorCode);
        bundle.putString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY, message);
        sendData(errorCode,bundle);
    }

    public void sendData(int resultCode, Bundle bundle) {
        getSearchedLocationReceiver().send(resultCode, bundle);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
