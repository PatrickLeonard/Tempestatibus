package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;


/**
 * The IntentService makes the restful call to the Dark Sky Forecast API and then sends the result
 * to the associated receiver
 * Created by Patrick Leonard on 11/8/2015.
 */
public class ForecastFetchIntentService extends IntentService {

    public static final String TAG = ForecastFetchIntentService.class.getSimpleName();

    private static final long TWO_SECONDS = 2 * 1000;

    public String mName;
    private ResultReceiver mReceiver;
    private Location mLocation;
    private OkHttpClient mOkHttpClient;
    private ConnectivityManager mConnectivityManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        //Retrieve items them the intent
        mReceiver = intent.getParcelableExtra(ForecastFetchConstants.RECEIVER);
        mLocation = intent.getParcelableExtra(ForecastFetchConstants.LOCATION_DATA_EXTRA);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        makeForecastAPICall();
    }

    //Deliver results to the calling thread
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(ForecastFetchConstants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    public ForecastFetchIntentService(String name) {
        super(name);
        mName = name;
    }

    public ForecastFetchIntentService() {
        super("No arg");
        mName = "No arg";
    }

    public void makeForecastAPICall() {
        if (isNetworkAvailable()) {
            //If the network is available create the Get request to Dark Sky Forecast API
            String forecastURL = constructForecastURL();
            if (mOkHttpClient == null) {
                mOkHttpClient = new OkHttpClient();
                mOkHttpClient.setConnectTimeout(TWO_SECONDS, TimeUnit.MILLISECONDS); //This request should not take a long time.
                mOkHttpClient.setWriteTimeout(TWO_SECONDS, TimeUnit.MILLISECONDS);  //The user isn't going to sit and wait for the spinner
                mOkHttpClient.setReadTimeout(TWO_SECONDS, TimeUnit.MILLISECONDS);  //Error out if this request doesn't happen fast enough??
            }
            final Request request = getRequest(forecastURL);
            if (request == null) {
                //Failure creating request
                deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT,getString(R.string.forecast_fetch_error));
                return; //No request can be made if null
            }
            //Create and Enque the API Call for asynchronous action
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                //Failure of the asynchronous API call
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(ForecastFetchIntentService.TAG, getString(R.string.exception_caught_text) + getString(R.string.IO), e);
                    deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT, getString(R.string.forecast_fetch_error));
                }
                //Some response received, hopefully success
                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        //Using GZIP compression to reduce data usage
                        String extractCharset = extractCharset(response);
                        GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
                        InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream,extractCharset);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        final String jsonData = bufferedReader.readLine();
                        deliverResultToReceiver(ForecastFetchConstants.SUCCESS_RESULT, jsonData);
                    } else {
                        Log.e(ForecastFetchIntentService.TAG, getString(R.string.unsuccessful_response));
                        deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT, getString(R.string.forecast_fetch_error));
                    }
                    response.body().close();
                }
            });
        //No network...that's no good.
        } else {
            Log.e(ForecastFetchIntentService.TAG, getString(R.string.network_unavailable_text));
            deliverResultToReceiver(ForecastFetchConstants.FAILURE_RESULT,getString(R.string.network_unavailable_text));
        }
    }

    //Programmatically retrieve the charset from the Response to handle all types
    private String extractCharset(Response response) {
        String charset="";
        String defaultCharset = getString(R.string.utf_8_content_type);
        String contentTypeString = response.header(getString(R.string.forecast_api_call_header_content_type),
                defaultCharset);
        //Create a default, if not the default, strip out of the response
        if(!contentTypeString.equals(defaultCharset)) {
            String[] contentTypeArray = contentTypeString.split(";");
            for(String contentType: contentTypeArray) {
                if(contentType.contains(getString(R.string.forecast_api_call_header_value_key_charset))) {
                    String[] charsetTuple = contentType.split("=");
                    charset = charsetTuple[1];
                    break;
                }
            }
        }
        return charset;
    }

    //Create the request using GZIP compression
    private Request getRequest(String forecastURL) {
        Request request;
        request = new Request.Builder().url(forecastURL).addHeader(getString(R.string.forecast_api_call_header_accept_encoding), getString(R.string.forcast_api_call_header_value_gzip_encoding)).build();
        return request;
    }

    //Check if the network is available for the phone
    private boolean isNetworkAvailable() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //Construct the Forecast API call URL using the Latitude and Longitude of the phone
    public String constructForecastURL() {
        //Get the API Key
        String apiKey = getAPIKey();
        //Construct the bulk of the HTTP Get request
        String forecastURL = getString(R.string.api_url) +
                apiKey + "/" + mLocation.getLatitude() + "," + mLocation.getLongitude();
        //Get the Application settings for the additional request parameters
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(this);
        //This app will support all of the languages supported by the API
        forecastURL += "?lang="+ TempestatibusApplicationSettings.getForecastLanguage();
        //Get the Units Preference, true if SI, false if US
        if(tempestatibusApplicationSettings.getAppUnitsPreference()) {
            forecastURL += "&units=si";
        }
        //Only Extend the hourly information if calling from MainActivity, not from the widget
        if(tempestatibusApplicationSettings.getHourlyExtendPreference()) {
            forecastURL += "&extend=hourly";
        }
        return forecastURL;
    }

    //Get the API Key
    private String getAPIKey() {
        Context context = getApplicationContext();
        ApplicationInfo applicationInfo;
        Bundle bundle;
        String apiKey="";
        try {
            applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            bundle = applicationInfo.metaData;
            apiKey = bundle.getString("api-key");
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(ForecastFetchIntentService.TAG, "Error extracting key.", e);
        }
        return apiKey;
    }
}
