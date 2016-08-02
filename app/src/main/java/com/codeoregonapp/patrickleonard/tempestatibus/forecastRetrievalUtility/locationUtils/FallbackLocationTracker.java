package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

/**
*  Created by Patrick Leonard on 11/8/2015.
*  Adapted from: http://gabesechansoftware.com/location-tracking/
*/
import android.content.Context;
import android.location.Location;
import android.util.Log;

public class FallbackLocationTracker  implements LocationTracker, LocationTracker.LocationUpdateListener {

    private static final String TAG = FallbackLocationTracker.class.getSimpleName();
    private boolean isRunning;
    // The minimum time before a location is considered "stale" or old data
    // Cache the location for 4 minutes
    private static final int FOUR_MINUTES = 1000 * 60 * 4;
    // Cache the stale location for 8 minutes
    private static final int EIGHT_MINUTES = 1000 * 60 * 8;
    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;

    private LocationUpdateListener mListener;

    Location mLastLoc;
    long mLastTime;

    public FallbackLocationTracker(Context context) {
        gps = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.GPS);
        net = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.NETWORK);
        mLastTime = System.currentTimeMillis();
    }

    public void start(long minUpdateTime, long minUpdateDistance){
        if(isRunning){
            //Already running, do nothing
            return;
        }

        //Start both
        gps.start(this,minUpdateTime,minUpdateDistance);
        net.start(this,minUpdateTime,minUpdateDistance);
        isRunning = true;
    }

    public void start(LocationUpdateListener update, long minUpdateTime, long minUpdateDistance) {
        start(minUpdateTime,minUpdateDistance);
        mListener = update;
    }


    public void stop(){
        if(isRunning){
            gps.stop();
            net.stop();
            isRunning = false;
        }
    }

    public boolean hasLocation(){
        //If either has a location, use it
        return (gps.hasLocation() || net.hasLocation()) &&
                !(System.currentTimeMillis() - mLastTime > FOUR_MINUTES);
    }

    public boolean hasPossiblyStaleLocation(){
        //If either has a location, use it
        Log.d(FallbackLocationTracker.TAG,"Difference between last time and system time: " + (System.currentTimeMillis() - mLastTime));
        return (gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation()) &&
                !(System.currentTimeMillis() - mLastTime > EIGHT_MINUTES);
    }

    public Location getLocation(){
        Location ret = gps.getLocation();
        if(ret == null){
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        boolean update = false;
        if(isBetterLocation(newLoc, mLastLoc)) {
            update = true;
        }
        if(update){
            if(mListener != null) {
                mListener.onUpdate(mLastLoc, mLastTime, newLoc, newTime);
            }
            mLastLoc = newLoc;
            mLastTime = newTime;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > FOUR_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FOUR_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than three minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than three minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}