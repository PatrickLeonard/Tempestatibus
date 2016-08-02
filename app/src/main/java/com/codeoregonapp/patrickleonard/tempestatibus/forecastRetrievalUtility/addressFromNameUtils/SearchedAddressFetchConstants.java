package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils;

/**
 * Constants used in retrieving the address associated with the phones location
 * Created by Patrick Leonard on 2/6/2016.
 */
public final class SearchedAddressFetchConstants {
    public static final int FAILURE_RESULT = 0;
    public static final int SUCCESS_RESULT = 1;
    public static final int NOT_PRESENT = 2;
    public static final String PACKAGE_NAME =
            "com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.searchedLocationAddressUtils";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String ENTERED_TEXT_KEY = PACKAGE_NAME +
            ".ENTERED_TEXT";
}
