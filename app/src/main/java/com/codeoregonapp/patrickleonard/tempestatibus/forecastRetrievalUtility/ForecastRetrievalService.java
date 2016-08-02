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
import com.codeoregonapp.patrickleonard.tempestatibus.database.CachedForecastDataSource;
import com.codeoregonapp.patrickleonard.tempestatibus.database.CachedLocationDataSource;
import com.codeoregonapp.patrickleonard.tempestatibus.database.SavedForecastModel;
import com.codeoregonapp.patrickleonard.tempestatibus.database.SavedLocationModel;
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
import com.codeoregonapp.patrickleonard.tempestatibus.notifications.SevereWeatherNotificationHelper;
import com.codeoregonapp.patrickleonard.tempestatibus.ui.MainActivity;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;
import com.codeoregonapp.patrickleonard.tempestatibus.widget.WidgetForecastUpdateService;

import java.util.List;


/**
 * Service to updateLocation the widgets as needed
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
    private SevereWeatherNotificationHelper mSevereWeatherNotificationHelper;
    private String mCallingClassName;
    private boolean mIsRunning;
    private int mResultCode;
    private String mResultMessage;

    public SevereWeatherNotificationHelper getSevereWeatherNotificationHelper() {
        return mSevereWeatherNotificationHelper;
    }

    public void setSevereWeatherNotificationHelper(SevereWeatherNotificationHelper mSevereWeatherNotificationHelper) {
        this.mSevereWeatherNotificationHelper = mSevereWeatherNotificationHelper;
    }

    public String getResultMessage() {
        return mResultMessage;
    }

    public void setResultMessage(String mResultMessage) {
        this.mResultMessage = mResultMessage;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
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
        setResultCode(ForecastRetrievalServiceConstants.NULL_RESULT);
        setSevereWeatherNotificationHelper(new SevereWeatherNotificationHelper(this));
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int ids) {
        if(intent != null) {
                retrieveForecast(intent);
        }
        return Service.START_STICKY_COMPATIBILITY;
    }

    private void retrieveForecast(Intent intent) {
        if(!mIsRunning) {
            clearReceivers();
        }
        setReceiver(intent);
        if (!mIsRunning) {
            startLocationFetchService();
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
    private void startLocationFetchService() {
        mIsRunning = true;
        Intent intent = new Intent(this, LocationFetchService.class);
        LocationResultReceiver resultReceiver = new LocationResultReceiver(new Handler(), this);
        intent.putExtra(LocationFetchConstants.RECEIVER, resultReceiver);
        startService(intent);
    }

    private void assignLastKnownLocation() {
        CachedLocationDataSource cachedLocationDataSource = new CachedLocationDataSource(getApplicationContext());
        List<SavedLocationModel> lastKnown = cachedLocationDataSource.readLastKnownLocationWrapper();
        setShortenedAddress(lastKnown.get(0).getShortenedAddress());
        setStandardAddress(lastKnown.get(0).getStandardAddress());
        setLocation(lastKnown.get(0).getLocation());
    }

    private void assignLastRetrievedData() {
        CachedForecastDataSource cachedForecastDataSource = new CachedForecastDataSource(getApplicationContext());
        List<SavedForecastModel> lastKnown = cachedForecastDataSource.readLastKnownForecast();
        setJSONData(lastKnown.get(0).getForecastData());
    }

    //Function to start the GoogleAPIConnectionService
    private void startGoogleAPIConnectionService() {
        Intent intent = new Intent(this, GoogleAPIConnectionService.class);
        GoogleAPIConnectionResultReceiver resultReceiver = new GoogleAPIConnectionResultReceiver(new Handler(), this);
        intent.putExtra(GoogleAPIConnectionConstants.RECEIVER, resultReceiver);
        intent.putExtra(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR,isRestricted());
        startService(intent);
    }

    //Function to start the AddressFetchIntentService
    private void startAddressFetchIntentService() {
        Intent intent = new Intent(this, AddressFetchIntentService.class);
        AddressResultReceiver resultReceiver = new AddressResultReceiver(new Handler(), this);
        intent.putExtra(AddressFetchConstants.RECEIVER, resultReceiver);
        intent.putExtra(AddressFetchConstants.LOCATION_DATA_EXTRA, mLocation);
        intent.putExtra(AddressFetchConstants.CALLING_CLASS_NAME,mCallingClassName);
        startService(intent);
    }

    //Function to start the ForecastFetchIntentService
    private void startForecastFetchIntentService() {
        Intent intent = new Intent(this, com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchIntentService.class);
        ForecastResultReceiver resultReceiver = new ForecastResultReceiver(new Handler(), this);
        intent.putExtra(ForecastFetchConstants.RECEIVER, resultReceiver);
        intent.putExtra(ForecastFetchConstants.LOCATION_DATA_EXTRA,getLocation());
        startService(intent);
    }

    private void updateLastKnownLocationModel() {
        CachedLocationDataSource cachedLocationDataSource = new CachedLocationDataSource(getApplicationContext());
        cachedLocationDataSource.updateLastKnownLocation(getStandardAddress(),getShortenedAddress(),getLocation().getLatitude(),getLocation().getLongitude());
    }

    private void updateLastKnownForecastModel() {
        CachedForecastDataSource cachedForecastDataSource = new CachedForecastDataSource(getApplicationContext());
        cachedForecastDataSource.updateLastKnownForecast(getJSONData());
    }
    
    //Function to start the JSONExtractionIntentService
    private void startJSONExtractionIntentService() {
        if(getJSONData() == null || getJSONData().equals("")) {
            setResultCode(ForecastRetrievalServiceConstants.FAILURE_RESULT);
            sendData(getResultCode(),getResultCode(),getString(R.string.error_message_empty_JSON_data));
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
    private void deliverForecastAndAddress() {
        if(getResultCode() != ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT &&
                getResultCode() != ForecastRetrievalServiceConstants.NETWORK_FAILURE_RESULT) {
            updateLastKnownLocationModel(); //Data retrieved for location successfully, location is acceptable to update into database
            updateLastKnownForecastModel(); //JSON Extraction completed, then data is acceptable to update into database
            processAlerts(); // Send weather alert notifications if applicable
            setResultCode(ForecastRetrievalServiceConstants.SUCCESS_RESULT); //Complete Success~=
        }
        sendData(getResultCode(),ForecastRetrievalServiceConstants.NULL_RESULT,getResultMessage());
    }

    private void processAlerts() {
        getSevereWeatherNotificationHelper().setIncomingAlerts(getForecast().getWeatherAlerts());
        getSevereWeatherNotificationHelper().processAlerts();
    }

    //Deliver the error to the calling receiver
    private Bundle prepareBundle(int errorCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY, errorCode);
        bundle.putString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY, message);
        bundle.putString(ForecastRetrievalServiceConstants.STANDARD_ADDRESS_DATA_KEY, getStandardAddress());
        bundle.putString(ForecastRetrievalServiceConstants.SHORTENED_ADDRESS_DATA_KEY, getShortenedAddress());
        bundle.putParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY, getForecast());
        return bundle;
    }

    private void sendData(int resultCode, int errorCode, String message) {
        if(getMainReceiver() != null) {
            getMainReceiver().send(resultCode, prepareBundle(errorCode,message));
        }
        if (getWidgetReceiver() != null) {
            getWidgetReceiver().send(resultCode, prepareBundle(errorCode,message));
        }
        mIsRunning = false;
        setResultCode(ForecastRetrievalServiceConstants.NULL_RESULT);
    }

    private void clearReceivers() {
        setMainReceiver(null);
        setWidgetReceiver(null);
    }

    public void fetchAddressSuccess(String standardAddress, String shortenedAddress) {
        setStandardAddress(standardAddress);
        setShortenedAddress(shortenedAddress);
        startForecastFetchIntentService();
    }

    public void fetchAddressFailure(String standardAddress, String shortenedAddress, int resultCode) {
        setStandardAddress(standardAddress);
        setShortenedAddress(shortenedAddress);
        sendData(ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT, resultCode,standardAddress);
    }

    public void fetchForecastSuccess(String forecastOutput) {
        setJSONData(forecastOutput);
        startJSONExtractionIntentService();
    }

    public void fetchForecastFailure(String forecastOutput) {
        assignLastRetrievedData();
        setResultCode(ForecastRetrievalServiceConstants.NETWORK_FAILURE_RESULT);
        setResultMessage(forecastOutput);
        startJSONExtractionIntentService();
    }

    public void jsonExtractionSuccess(Forecast forecast) {
        setForecast(forecast);
        deliverForecastAndAddress();
    }

    public void jsonExtractionFailure(String errorMessage, int resultCode) {
        sendData(ForecastRetrievalServiceConstants.FAILURE_RESULT,resultCode,errorMessage);
    }

    public void googleConnectionSuccess() {
        startAddressFetchIntentService();
    }

    public void googleConnectionFailure(int errorCode) {
       sendData(ForecastRetrievalServiceConstants.GOOGLE_CONNECTION_ERROR,errorCode,"Google API Connection Problem");
    }

    public void fetchLocationSuccess(Location location) {
        setLocation(location);
        startGoogleAPIConnectionService();
    }

    public void fetchLocationFailure() {
        assignLastKnownLocation();
        setResultCode(ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT);
        setResultMessage(getString(R.string.no_location_found_address));
        startForecastFetchIntentService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
