package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils.ForecastFetchConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Service to retrieve the location of the phone. Need the Latitude and Longitude
 * to find address and perform Dark Sky Forecast API restful call.
 * Created by Patrick Leonard on 11/8/2015.
 */
public class LocationFetchService extends Service {

    public static final String TAG = LocationFetchService.class.getSimpleName();
    private static final long TEN_SECONDS = 1000 * 10;
    private ResultReceiver mReceiver;
    private FallbackLocationTracker mFallbackLocationTracker;
    private Timer mTimeoutTimer;
    private boolean mImmediate;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int ids) {
        if(intent != null) {
            mReceiver = intent.getParcelableExtra(LocationFetchConstants.RECEIVER);
            if (checkLocationServicesEnabled()) {
                if(mFallbackLocationTracker == null) {
                    mFallbackLocationTracker = new FallbackLocationTracker(this);
                }
                if (mFallbackLocationTracker.hasLocation()) {
                    deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT, mFallbackLocationTracker.getLocation());
                } else if (mFallbackLocationTracker.hasPossiblyStaleLocation()) {
                    deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT, mFallbackLocationTracker.getPossiblyStaleLocation());
                } else {
                    setImmediateLocationListener();
                }
            }
            else {
                deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT,null);
            }
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
    }

    private boolean checkLocationServicesEnabled() {
        LocationManager lm = (LocationManager)getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled;
        boolean network_enabled;
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return (gps_enabled || network_enabled) && checkAndroidVersionForLocationPermission();
    }

    //Check if the API considers physical location as a "Dangerous Permission"
    private boolean checkAndroidVersionForLocationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int locationDangerCheck = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (!(locationDangerCheck == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
            return true;
        } catch (SecurityException e) {
            Log.d(LocationFetchService.TAG, getString(R.string.error_message), e);
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setImmediateLocationListener() {
        mImmediate = true;
        startTimeoutTimer();
        mFallbackLocationTracker.start(new LocationTracker.LocationUpdateListener() {
            @Override
            public void onUpdate(Location oldLoc, long oldTime, final Location newLoc, long newTime) {
                if (newLoc != null) {
                        stopTimeoutTimer();
                        deliverResultToReceiver(LocationFetchConstants.SUCCESS_RESULT,newLoc);
                    }
                }
        },0,0);
    }

    private void startTimeoutTimer() {
        Log.d(LocationFetchService.TAG,"Starting timeout timer");
        mTimeoutTimer = new Timer();
        mTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mFallbackLocationTracker != null) {
                    mFallbackLocationTracker.stop();
                    stopTimeoutTimer();
                    Log.d(LocationFetchService.TAG,"TIMED OUT!!");
                    deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT,null);
                }
            }
        }, TEN_SECONDS);

    }

    private void stopTimeoutTimer() {
        if(mTimeoutTimer != null) {
            mTimeoutTimer.cancel();
            mTimeoutTimer.purge();
            mTimeoutTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFallbackLocationTracker.stop();
    }

}
