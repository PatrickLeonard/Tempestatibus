package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

/**
 *  Created by Patrick Leonard on 11/8/2015.
 *  Adapted from: http://gabesechansoftware.com/location-tracking/
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;

public class ProviderLocationTracker implements LocationListener, LocationTracker {

    // The minimum time before a location is considered "stale" or old data
    // Cache the location for 4 minutes.
    private static final long STALE_LOCATION_TIME = 1000 * 60 * 4;

    private LocationManager lm;
    private ConnectivityManager mConnectivityManager;
    public enum ProviderType{
        NETWORK,
        GPS
    }

    private String provider;

    private Location lastLocation;
    private long lastTime;

    private boolean isRunning;

    private LocationUpdateListener listener;

    public ProviderLocationTracker(Context context, ProviderType type) {
        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(type == ProviderType.NETWORK){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else{
            provider = LocationManager.GPS_PROVIDER;
        }
    }

    public void start(long minUpdateTime, long minUpdateDistance) throws SecurityException {
        if(isRunning){
            //Already running, do nothing
            return;
        }
        if(provider.equals(LocationManager.NETWORK_PROVIDER) && checkNetworkConnection() && lm.isProviderEnabled(provider)) {
            internalStart(minUpdateTime, minUpdateDistance);
        }
        else if(provider.equals(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(provider)){
            internalStart(minUpdateTime, minUpdateDistance);
        }
    }

    private void internalStart(long minUpdateTime, long minUpdateDistance) throws SecurityException {
        //The provider is on, so start getting updates.  Update current location
        isRunning = true;
        lm.requestLocationUpdates(provider, minUpdateTime, minUpdateDistance, this);
        lastLocation = null;
        lastTime = 0;
    }

    private boolean checkNetworkConnection() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void start(LocationUpdateListener update, long minUpdateTime, long minUpdateDistance) {
        start(minUpdateTime,minUpdateDistance);
        listener = update;
    }


    public void stop() throws SecurityException {
        if(isRunning){
            lm.removeUpdates(this);
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        return ((lastLocation!=null) &&
                (!(System.currentTimeMillis() - lastTime > STALE_LOCATION_TIME)));
    }

    @Override
    public boolean hasPossiblyStaleLocation() throws SecurityException {
        return lm.getLastKnownLocation(provider)!= null;
    }

    public Location getLocation(){
        return lastLocation;
    }

    @Override
    public Location getPossiblyStaleLocation() throws SecurityException {
        return lm.getLastKnownLocation(provider);
    }

    public void onLocationChanged(Location newLoc) {
        long now = System.currentTimeMillis();
        if(isBetterLocation(newLoc,lastLocation)) {
            if (listener != null) {
                listener.onUpdate(lastLocation, lastTime, newLoc, now);
            }
            lastLocation = newLoc;
            lastTime = now;
        }
    }

    private static final int FOUR_MINUTES = 1000 * 60 * 4;

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

        // If it's been more than four minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than four minutes older, it must be worse
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

    public void onProviderDisabled(String arg0) {
    }
    public void onProviderEnabled(String arg0) {
    }
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }
}