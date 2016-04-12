package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.locationUtils;

/**
 * Created by Patrick Leonard on 11/8/2015.
 * Adapted from: http://gabesechansoftware.com/location-tracking/
 */
import android.location.Location;

public interface LocationTracker {
    interface LocationUpdateListener {
        void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }
    void start(long minUpdateTime, long minUpdateDistance);
    void start(LocationUpdateListener update, long minUpdateTime, long minUpdateDistance);
    void stop();
    boolean hasLocation();
    boolean hasPossiblyStaleLocation();
    Location getLocation();
    Location getPossiblyStaleLocation();
}