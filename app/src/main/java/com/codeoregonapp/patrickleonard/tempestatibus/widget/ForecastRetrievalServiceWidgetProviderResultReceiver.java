package com.codeoregonapp.patrickleonard.tempestatibus.widget;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

/**
 * This receiver gets the results from the JSONExtractionIntentService and reacts to them in the
 * MainActivity
 * Created by Patrick Leonard on 12/31/2015.
 */
public class ForecastRetrievalServiceWidgetProviderResultReceiver extends ResultReceiver {

    public static final String TAG = ForecastRetrievalServiceWidgetProviderResultReceiver.class.getSimpleName();

    private final WidgetForecastUpdateService mWidgetForecastUpdateService;
    public static Creator CREATOR = ResultReceiver.CREATOR;
    public int mResultCode;

    public ForecastRetrievalServiceWidgetProviderResultReceiver(Handler handler,WidgetForecastUpdateService widgetForecastUpdateService) {
        super(handler);
        mWidgetForecastUpdateService = widgetForecastUpdateService;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        int errorCode = resultData.getInt(ForecastRetrievalServiceConstants.ERROR_CODE_KEY);
        String errorMessage = resultData.getString(ForecastRetrievalServiceConstants.ERROR_MESSAGE_KEY);
        if (resultCode == ForecastRetrievalServiceConstants.SUCCESS_RESULT) {
            mResultCode = resultCode;
            Forecast forecast = resultData.getParcelable(ForecastRetrievalServiceConstants.RESULT_DATA_KEY);
            String address = resultData.getString(ForecastRetrievalServiceConstants.SHORTENED_ADDRESS_DATA_KEY);
            mWidgetForecastUpdateService.setForecast(forecast);
            mWidgetForecastUpdateService.setAddress(address);
            WidgetForecastUpdateService.staticForecast = forecast;
            WidgetForecastUpdateService.staticAddress = address;
            mWidgetForecastUpdateService.setHasRetrievalError(false);
        }
        else {
            logError(resultCode, errorCode, errorMessage);
        }
        mWidgetForecastUpdateService.updateAppWidgets(mWidgetForecastUpdateService.getAllAppWidgetIdsFromAllProviders());
    }

    private void logError(int resultCode, int errorCode, String errorMessage) {
        Log.e(ForecastRetrievalServiceWidgetProviderResultReceiver.TAG, "Result failure in onReceiveResult");
        Log.e(ForecastRetrievalServiceWidgetProviderResultReceiver.TAG,"Result code: " + resultCode);
        Log.e(ForecastRetrievalServiceWidgetProviderResultReceiver.TAG,"Result code: " + errorCode);
        Log.e(ForecastRetrievalServiceWidgetProviderResultReceiver.TAG,"Error Message:" + errorMessage);
        mWidgetForecastUpdateService.setHasRetrievalError(true);
    }
}
