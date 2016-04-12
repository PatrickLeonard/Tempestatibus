package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

/**
 * Constants used in fetching the Forecast data from the Dark Sky Forecast API
 * Created by Patrick Leonard on 1/2/2016.
 */
public final class JSONExtractionConstants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils.forecastUtils";
    public static final String RECEIVER = PACKAGE_NAME + ".JSON_EXTRACTION_RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String JSON_DATA_EXTRA = PACKAGE_NAME +
            ".JSON_DATA_EXTRA";
}
