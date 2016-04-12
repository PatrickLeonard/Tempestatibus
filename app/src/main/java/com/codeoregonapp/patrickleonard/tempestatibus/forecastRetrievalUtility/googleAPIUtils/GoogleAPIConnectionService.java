package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Service to retrieve the location of the phone. Need the Latitude and Longitude
 * to find address and perform Dark Sky Forecast API restful call.
 * Created by Patrick Leonard on 11/8/2015.
 */
public class GoogleAPIConnectionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = GoogleAPIConnectionService.class.getSimpleName();

    private ResultReceiver mReceiver;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int ids) {
        //Set the receiver and connect to the Google API
        if(intent != null) {
            mReceiver = intent.getParcelableExtra(GoogleAPIConnectionConstants.RECEIVER);
            mResolvingError = intent.getBooleanExtra(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR,false);
            if(!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            else {
                apiConnected();
            }
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //Disconnect on destroy if connected
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        apiConnected();
    }


    //Connected to the API check to see if the Geocoder is Present
    private void apiConnected() {
        int result;
        if(Geocoder.isPresent()) {
            result = GoogleAPIConnectionConstants.GEOCODER_PRESENT;
        }
        else {
            result = GoogleAPIConnectionConstants.GEOCODER_NOT_PRESENT;
        }
        //Deliver results, connected and if Geocoder is present
        deliverResultToReceiver(GoogleAPIConnectionConstants.SUCCESS_RESULT, result);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Deliver error if connection suspended
        deliverResultToReceiver(GoogleAPIConnectionConstants.CONNECTION_SUSPENDED, i);
    }

    //Deliver and error if connection failed. Send message and code to attempt to resolve the error
    //Per Google Developers web page
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            deliverResultToReceiver(GoogleAPIConnectionConstants.REQUEST_RESOLVE_ERROR, connectionResult.getErrorCode(),connectionResult.getResolution());
        } else {
            deliverResultToReceiver(GoogleAPIConnectionConstants.FAILURE_RESULT, GoogleAPIConnectionConstants.NO_RESOLUTION);
        }
        Log.e(GoogleAPIConnectionService.TAG, "Connection Failed");
        Log.e(GoogleAPIConnectionService.TAG, String.format("Error code: %s", connectionResult.getErrorCode()));
    }

    //Straight from the Google Developers Web Page
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();
    }

    //Deliver results to receiver that does not have an error to resolve
    private void deliverResultToReceiver(int resultCode, int errorCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(GoogleAPIConnectionConstants.RESULT_DATA_KEY, errorCode);
        bundle.putBoolean(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR, mResolvingError);
        mReceiver.send(resultCode, bundle);
    }

    //Deliver results to receiver that does have an error to resolve
    private void deliverResultToReceiver(int resultCode, int errorCode, PendingIntent pendingIntent) {
        Bundle bundle = new Bundle();
        bundle.putInt(GoogleAPIConnectionConstants.RESULT_DATA_KEY, errorCode);
        bundle.putParcelable(GoogleAPIConnectionConstants.RESOLUTION_INTENT_KEY, pendingIntent);
        bundle.putBoolean(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR, mResolvingError);
        mReceiver.send(resultCode, bundle);
    }
}
