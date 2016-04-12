package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils;

/**
 * Constants for use with the GoogleAPIConnectionService for sharing the results of connecting
 * to the GoogleAPI using the Service
 * Created by Patrick Leonard on 11/8/2015.
 */
public final class GoogleAPIConnectionConstants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int CONNECTION_SUSPENDED = 2;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String STATE_RESOLVING_ERROR = "resolving_error";
    public static final String RESOLUTION_INTENT_KEY = PACKAGE_NAME +
            "PENDING_INTENT";
    // Request code to use when launching the resolution activity
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Code to use when there is no resolution to the connection problem found
    public static final int NO_RESOLUTION = -1;
    public static final int GEOCODER_PRESENT = -2;
    public static final int GEOCODER_NOT_PRESENT = -3;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";
}
