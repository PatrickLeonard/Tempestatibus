package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility;

/**
 * Constants used in fetching the Forecast data from the Dark Sky Forecast API
 * for a location searched for by the user.
 * Created by Patrick Leonard on 2/6/2016.
 */
public final class SearchedLocationForecastRetrievalServiceConstants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility";
    public static final String SEARCHED_LOCATION_ACTIVITY_RECEIVER = PACKAGE_NAME + ".RECEIVER_SEARCHED_LOCATION_ACTIVITY";
    public static final String ERROR_CODE_KEY = PACKAGE_NAME +
            ".ERROR_CODE_KEY";
    public static final String ERROR_MESSAGE_KEY = PACKAGE_NAME +
            ".ERROR_MESSAGE_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
}
