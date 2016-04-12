package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

/**
 * Service to retrieve the location of the phone. Need the Latitude and Longitude
 * to find address and perform Dark Sky Forecast API restful call.
 * Created by Patrick Leonard on 11/8/2015.
 */
public class LocationFetchService extends Service {

    public static final String TAG = LocationFetchService.class.getSimpleName();
    private static long LOCATION_UPDATE_TIME = 1000 * 60 * 4;
    private static long LOCATION_UPDATE_DISTANCE = 10;
    private ResultReceiver mReceiver;
    private FallbackLocationTracker mFallbackLocationTracker;
    private boolean mImmediate;

    @Override
    public void onCreate() {
        super.onCreate();
        mFallbackLocationTracker = new FallbackLocationTracker(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int ids) {
        if(intent != null) {
            mReceiver = intent.getParcelableExtra(LocationFetchConstants.RECEIVER);
            if(mFallbackLocationTracker.hasLocation()){
               deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT,mFallbackLocationTracker.getLocation());
            }
            else if(mFallbackLocationTracker.hasPossiblyStaleLocation()) {
                deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT,mFallbackLocationTracker.getPossiblyStaleLocation());
            }
            else {
                setImmediateLocationListener();
            }
            mFallbackLocationTracker.start(LOCATION_UPDATE_TIME,LOCATION_UPDATE_DISTANCE);
        }
        return START_NOT_STICKY;
    }

    private void deliverResultToReceiver(int resultCode, Location location) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(LocationFetchConstants.RESULT_DATA_KEY, location);
        mReceiver.send(resultCode, bundle);
        if(mImmediate) {
            mFallbackLocationTracker.stop();
            mImmediate = false;
        }
        mFallbackLocationTracker.start(LOCATION_UPDATE_TIME,LOCATION_UPDATE_DISTANCE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setImmediateLocationListener() {
        mImmediate = true;
        mFallbackLocationTracker.start(new LocationTracker.LocationUpdateListener() {
            @Override
            public void onUpdate(Location oldLoc, long oldTime, final Location newLoc, long newTime) {
                if (newLoc != null) {
                        deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT,newLoc);
                    }
                }
        },0,0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFallbackLocationTracker.stop();
    }

}
