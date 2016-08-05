package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils;

/**
 * Constants used in retrieving the address associated with the phones location
 * Created by Patrick Leonard on 11/8/2015.
 */
public final class AddressFetchConstants {
    public static final int FAILURE_RESULT = 0;
    public static final int SUCCESS_RESULT = 1;
    public static final int NOT_PRESENT = 2;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressUtils";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String ERROR_MESSAGE_DATA_KEY = PACKAGE_NAME +
            ".ERROR_MESSAGE_DATA_KEY";
    public static final String STANDARD_RESULT_DATA_KEY = PACKAGE_NAME +
            ".STANDARD_RESULT_DATA_KEY";
    public static final String SHORTENED_RESULT_DATA_KEY = PACKAGE_NAME +
            ".SHORTENED_RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String CALLING_CLASS_NAME = PACKAGE_NAME +
            ".CALLING_CLASS_NAME";
}
