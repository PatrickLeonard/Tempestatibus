package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility;

/**
 * Constants used in fetching the Forecast data from the Dark Sky Forecast API
 * Created by Patrick Leonard on 11/8/2015.
 */
public final class ForecastRetrievalServiceConstants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int GOOGLE_CONNECTION_ERROR = 2;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility";
    public static final String MAIN_ACTIVITY_RECEIVER = PACKAGE_NAME + ".RECEIVER_MAIN_ACTIVITY";
    public static final String WIDGET_RECEIVER = PACKAGE_NAME + ".RECEIVER_WIDGET";
    public static final String ERROR_CODE_KEY = PACKAGE_NAME +
            ".ERROR_CODE_KEY";
    public static final String ERROR_MESSAGE_KEY = PACKAGE_NAME +
            ".ERROR_MESSAGE_KEY";
    public static final String STANDARD_ADDRESS_DATA_KEY = PACKAGE_NAME +
            ".STANDARD_ADDRESS_DATA_KEY";
    public static final String SHORTENED_ADDRESS_DATA_KEY = PACKAGE_NAME +
            ".SHORTENED_ADDRESS_DATA_KEY";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String CALLING_CLASS_NAME_DATA_EXTRA = PACKAGE_NAME +
            ".CALLING_CLASS_NAME_DATA_EXTRA";
}
