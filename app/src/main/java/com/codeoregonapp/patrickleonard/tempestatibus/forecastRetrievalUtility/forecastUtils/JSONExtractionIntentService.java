package com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.forecastUtils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

import org.json.JSONException;

/**
 * The IntentService makes the restful call to the Dark Sky Forecast API and then sends the result
 * to the associated receiver
 * Created by Patrick Leonard on 11/8/2015.
 */
public class JSONExtractionIntentService extends IntentService {

    public static final String TAG = JSONExtractionIntentService.class.getSimpleName();

    public String mName;
    private String mJSONData;
    private ResultReceiver mReceiver;
    private Forecast mForecast;

    public Forecast getForecast() {
        return mForecast;
    }

    public void setForecast(Forecast forecast) {
        mForecast = forecast;
    }

    public String getJSONData() {
        return mJSONData;
    }

    public void setJSONData(String JSONData) {
        mJSONData = JSONData;
    }

    public ResultReceiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(ResultReceiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Set the receiver and extract the JSON data to create the Forecast data model
        setReceiver((ResultReceiver) intent.getParcelableExtra(JSONExtractionConstants.RECEIVER));
        setJSONData(intent.getStringExtra(JSONExtractionConstants.JSON_DATA_EXTRA));
        try {
            //Deliver the data if successful
            setForecast(new Forecast(getJSONData(), getApplicationContext()));
            deliverResultToReceiver(JSONExtractionConstants.SUCCESS_RESULT,getForecast());
        }
        catch(JSONException e) {
            //If there is a JSON exception send error results
            Log.e(JSONExtractionIntentService.TAG,getApplicationContext().getString(R.string.exception_caught_text),e);
            deliverResultToReceiver(JSONExtractionConstants.FAILURE_RESULT,null);
        }
    }

    //Deliver the Forecast (extends Parcelable) to the calling thread
    private void deliverResultToReceiver(int resultCode, Parcelable parcelable) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(JSONExtractionConstants.RESULT_DATA_KEY, parcelable);
        mReceiver.send(resultCode, bundle);
    }

    public JSONExtractionIntentService(String name) {
        super(name);
        mName = name;
    }

    public JSONExtractionIntentService() {
        super("No arg");
        mName = "No arg";
    }
}
