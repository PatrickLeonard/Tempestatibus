package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.addressFromNameUtils;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.ui.SearchForLocationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class receives the results from the SearchedAddressFetchIntentService and reacts to get suggestions
 * and results for the SearchForLocationActivity
 * Created by Patrick Leonard on 2/6/2016.
 */
public class SearchedAddressResultReceiver extends ResultReceiver {

    public static final String TAG = SearchedAddressResultReceiver.class.getSimpleName();
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public List<Address> mSearchedAddresses;
    public int mResultCode;
    private SearchForLocationActivity mSearchForLocationActivity;

    public SearchedAddressResultReceiver(Handler handler, SearchForLocationActivity searchForLocationActivity) {
        super(handler); mSearchForLocationActivity = searchForLocationActivity;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        mResultCode = resultCode;
        mSearchedAddresses = resultData.getParcelableArrayList(SearchedAddressFetchConstants.RESULT_DATA_KEY);
        mSearchForLocationActivity.setAddresses((ArrayList<Address>) mSearchedAddresses);
        mSearchForLocationActivity.addResultsToAdapter();
    }
}
