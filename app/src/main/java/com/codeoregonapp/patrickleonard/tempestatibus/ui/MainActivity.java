package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.adapters.DayAdapter;
import com.codeoregonapp.patrickleonard.tempestatibus.database.CachedLocationDataSource;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.googleAPIUtils.GoogleAPIConnectionConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the main activity of the Tempestatibus App. It's the starting activity that leads to three others:
 * DailyForecastActivity, HourlyForecastActivity, and SettingsActivity. Yes, that is an Oxford comma.
 * Author: Patrick Leonard 11/1/2015
 */
public class MainActivity extends AppCompatActivity {

    //Static variables for communications between services both internal and external
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST_PARCEL = "DAILY FORECAST"; //Daily Forecast data
    public static final String HOURLY_FORECAST_PARCEL = "HOURLY FORECAST"; //Hourly Forecast data
    public static final String ADDRESS_EXTRA = "ADDRESS"; //Address Data
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2; //Access to phone location
    private String mStandardAddress;
    private String mShortenedAddress;
    private String mUserSavedName;
    private Forecast mForecast;
    private boolean mAppResolvingError;
    private boolean mRefusedSettings;
    private CachedLocationDataSource mCachedLocationDataSource;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;

    public CachedLocationDataSource getLocationDataSource() {
        return mCachedLocationDataSource;
    }

    public void setLocationDataSource(CachedLocationDataSource cachedLocationDataSource) {
        mCachedLocationDataSource = cachedLocationDataSource;
    }

    public Forecast getForecast() { return mForecast; }

    public void setForecast(Forecast forecast) {
        mForecast = forecast;
    }

    public void setStandardAddress(String standardAddress) {
        mStandardAddress = standardAddress;
    }

    public String getStandardAddress() {
        return mStandardAddress;
    }

    public void setShortenedAddress(String ShortenedAddress) {
        mShortenedAddress = ShortenedAddress;
    }

    public String getShortenedAddress() {
        return mShortenedAddress;
    }
    
    public String getUserSavedName() {
        return mUserSavedName;
    }

    public void setUserSavedName(String userSavedName) {
        mUserSavedName = userSavedName;
    }

    public boolean getRefusedSettings() {
        return mRefusedSettings;
    }

    public void setRefusedSettings(boolean mEnteredSettings) {
        this.mRefusedSettings = mEnteredSettings;
    }

    public boolean isAppResolvingError() {
        return mAppResolvingError;
    }

    public void setAppResolvingError(boolean appResolvingError) {
        mAppResolvingError = appResolvingError;
    }

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        }
        return mTempestatibusApplicationSettings;
    }

    //Use ButterKnife to Bind Views to Variables
    @Bind(R.id.timeLabel)
    TextView mTimeLabel;
    @Bind(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precipValue)
    TextView mPrecipitationChanceValue;
    @Bind(R.id.summaryValue)
    TextView mSummaryValue;
    @Bind(R.id.timeUntilPrecipValue)
    TextView mTimeUntilPrecipValue;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.locationLabel)
    TextView mLocationLabel;
    @Bind(R.id.pressureValue)
    TextView mPressureValue;
    @Bind(R.id.windValue)
    TextView mWindValue;
    @Bind(R.id.stormValue)
    TextView mStormValue;
    @Bind(R.id.visibilityValue)
    TextView mVisibilityValue;
    @Bind(R.id.dewValue)
    TextView mDewValue;
    @Bind(R.id.ozoneValue)
    TextView mOzoneValue;
    @Bind(R.id.settingsImageView)
    ImageView mSettingsImageView;
    @Bind(R.id.degreeImageView)
    ImageView mDegreeImageView;
    @Bind(R.id.dewValueSmallDegree)
    ImageView mDewValueSmallDegree;
    @Bind(R.id.daily_grid_view)
    GridView mDailyGridView;
    @Bind(R.id.dewValueUnits)
    TextView mDewValueUnits;
    @Bind(R.id.temperatureValueUnits)
    TextView mTemperatureValueUnits;
    @Bind(R.id.apparentTemperatureValue)
    TextView mApparentTemperatureValue;
    @Bind(R.id.apparentTemperatureValueSmallDegree)
    ImageView mApparentTemperatureValueSmallDegree;
    @Bind(R.id.apparentTemperatureValueUnits)
    TextView mApparentTemperatureUnits;
    @Bind(R.id.hourglassIconImageView)
    ImageView mHourglassIconImageView;
    @Bind(R.id.saveIconImageView)
    ImageView mSaveIconImageView;
    @Bind(R.id.searchIconImageView)
    ImageView mSearchIconImageView;
    @Bind(R.id.poweredByForecast)
    TextView mPoweredByForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        //Set the theme, the content layout, and Bind views using Butterknife
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Apply the current app theme
        applyCurrentTheme();
        //Note if the application is still attempting to resolve an error from the Google API
        setAppResolvingError(savedInstanceState != null
                && savedInstanceState.getBoolean(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR, false));
        //Default the refresh and progress bar
        //start with the ability to click the update image, so spinner invisible
        mProgressBar.setVisibility(View.INVISIBLE);
        //Make the refresh button visible
        mRefreshImageView.setVisibility(View.VISIBLE);
        //Set the onClick listener for the data refresh button
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle refresh and get data
                start();
            }
        });
        //Set onClick Listener for the Settings icon (Cog)
        mSettingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the settings activity onClick
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mSearchIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getForecast() != null) {
                    Intent intent = new Intent(MainActivity.this, SearchForLocationActivity.class);
                    intent.putExtra(SearchForLocationActivity.CURRENT_STANDARD_ADDRESS_EXTRA, getStandardAddress());
                    intent.putExtra(SearchForLocationActivity.CURRENT_SHORTENED_ADDRESS_EXTRA, getShortenedAddress());
                    intent.putExtra(SearchForLocationActivity.CURRENT_LOCATION_EXTRA, getLocation());
                    startActivity(intent);
                }
            }
        });
        //Set the onClick listener for the data refresh button
        mSaveIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createSaveLocationDialog();
                    }
                });
            }
        });
        //Set the onClick listener for the data refresh button
        mHourglassIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the HourlyForecastActivity when a user taps the Hourglass
                startHourlyActivity();
            }
        });
        mPoweredByForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to open the default browser and travel to http://forecast.io
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forecast_io_url)));
                startActivity(browserIntent);
            }
        });
        //Check if need to request permission to get phone location
        //Check for GoogleApiAvailability
        if (checkAndroidVersionForLocationPermission()) {
            Log.d(MainActivity.TAG,"No need to check for location permission.");
            if(checkGoogleApiAvailability()) {
                start();
            }
        }

        //Set refused settings to false
        setRefusedSettings(false);
        Log.d(MainActivity.TAG,"Finishing MainActivity OnCreate.");
    }

    private void start() {
        toggleRefresh();
        //Get forecast data
        startForecastRetrievalService();
    }

    @NonNull
    private Location getLocation() {
        Location location = new Location(LocationManager.PASSIVE_PROVIDER);
        location.setLatitude(getForecast().getLatitude());
        location.setLongitude(getForecast().getLongitude());
        return location;
    }

    private void toggleRefresh() {
        //Makes the progress bar visible when the refresh button is not
        //Also makes the refresh button visible then progress bar is not
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }


    //Display the data from the weather forecast to the user (Nicely formatted)
    public void updateCurrentDisplay(Forecast forecast) {
        if(forecast != null) {
            mTemperatureLabel.setText(String.format("%s", forecast.getCurrent().getTemperature()));
            mTemperatureValueUnits.setText(String.format("%s", getString(forecast.getTemperatureUnitsId())));
            mApparentTemperatureValue.setText(String.format("%s", forecast.getCurrent().getApparentTemperature()));
            mApparentTemperatureUnits.setText(String.format("%s", getString(forecast.getTemperatureUnitsId())));
            mTimeLabel.setText(String.format(getString(R.string.time_label_format_string), forecast.getCurrent().getFormattedTime()));
            mHumidityValue.setText(String.format("%s%%", forecast.getCurrent().getHumidity()));
            mPrecipitationChanceValue.setText(String.format("%s%%", forecast.getCurrent().getPrecipitationProbability()));
            mWindValue.setText(String.format("%s %s %s", forecast.getCurrent().getWindSpeed(), getString(forecast.getVelocityUnitsId()), forecast.determineWindBearing(forecast)));
            mStormValue.setText(String.format("%s %s %s", forecast.getCurrent().getNearestStormDistance(), getString(forecast.getDistanceUnitsId()), forecast.determineStormBearing(forecast)));
            mDewValue.setText(String.format("%s", forecast.getCurrent().getDewPoint()));
            mDewValueUnits.setText(String.format("%s", getString(forecast.getTemperatureUnitsId())));
            mVisibilityValue.setText(String.format("%s %s", forecast.getCurrent().getVisibility(), getString(forecast.getDistanceUnitsId())));
            mPressureValue.setText(String.format("%s %s", forecast.getCurrent().getPressure(), getString(forecast.getPressureUnitsId())));
            mOzoneValue.setText(String.format("%s %s", forecast.getCurrent().getOzone(), getString(R.string.units_dobson)));
            mSummaryValue.setText(String.format("%s", forecast.getCurrent().getSummary()));
            mTimeUntilPrecipValue.setText(String.format("%s", forecast.getTimeUntilPrecipitation()));
            mLocationLabel.setText(String.format("%s", getStandardAddress()));
            Drawable drawable = ContextCompat.getDrawable(this, forecast.getCurrent().getIconId(mTempestatibusApplicationSettings.getAppThemePreference(), this));
            mIconImageView.setImageDrawable(drawable);
            mDailyGridView.setAdapter(new DayAdapter(this, forecast.getDailyForecastList()));
            mDailyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startDailyActivity(position);
                }
            });
        }
        toggleRefresh();
    }

    //This function displays a generic error the user
    public void alertUserAboutError(String title,String message,int errorType) {
        AlertDialogFragment dialog = new
                AlertDialogFragment();
        Bundle dialogBundle = new Bundle();
        dialogBundle.putString(AlertDialogFragment.ALERT_MESSAGE,message);
        dialogBundle.putString(AlertDialogFragment.ALERT_TITLE,title);
        dialogBundle.putInt(AlertDialogFragment.ERROR_TYPE,errorType);
        dialog.setArguments(dialogBundle);
        dialog.show(getFragmentManager(), getString(R.string.error_dialog));
    }

    //This function handles requesting the phone location from the user
    //Straight from the Google Developers web page
    private void requestLocationPermission() {
        Log.d(MainActivity.TAG,"Requesting location permission.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, R.string.location_permission_request_rationale, Toast.LENGTH_SHORT).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Handles the results of requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(MainActivity.TAG,"Location Permission Granted.");
                    if(checkGoogleApiAvailability()) {
                        start();
                    }
                } else {
                    alertUserAboutError(getString(R.string.no_location_found_address),"Open Settings?",ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT);
                }
            }
        }
    }

    //Check if the API considers physical location as a "Dangerous Permission"
    private boolean checkAndroidVersionForLocationPermission() {
        Log.v(MainActivity.TAG,"Checking location permissions.");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(MainActivity.TAG,"Calling checkSelfPermission: Access Fine Location.");
                int locationDangerCheck = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (!(locationDangerCheck == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(MainActivity.TAG,"locationCheck: " + locationDangerCheck);
                    requestLocationPermission();
                    return false;
                }
            }
            return true;
        } catch (SecurityException e) {
            Log.d(MainActivity.TAG, getString(R.string.error_message), e);
            return false;
        }
    }

    public void startLocationSettingsActivity() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void startWirelessSettingsActivity() {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    //Start the DailyForecastActivity with the List<Day> data
    public void startDailyActivity(int item) {
        //Don't start this activity if there is no forecast data
        if (getForecast() != null && getForecast().getDailyForecastList() != null) {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra(MainActivity.ADDRESS_EXTRA, getStandardAddress());
            intent.putExtra(MainActivity.DAILY_FORECAST_PARCEL,getForecast().getDailyForecastList().get(item));
            startActivity(intent);
        }
    }

    public void startHourlyActivity() {
        //Don't start this activity if there is no forecast data
        if (getForecast() != null && getForecast().getHourlyForecastList() != null) {
            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putParcelableArrayListExtra(MainActivity.HOURLY_FORECAST_PARCEL, (ArrayList<? extends Parcelable>) getForecast().getHourlyForecastList());
            startActivity(intent);
        }
    }

    //Start the ForecastRetrievalServices
    protected void startForecastRetrievalService() {
        Intent intent = new Intent(this, ForecastRetrievalService.class);
        ForecastRetrievalServiceMainActivityResultReceiver resultReceiver = new ForecastRetrievalServiceMainActivityResultReceiver(new Handler(), this);
        intent.putExtra(ForecastRetrievalServiceConstants.MAIN_ACTIVITY_RECEIVER, resultReceiver);
        intent.putExtra(ForecastRetrievalServiceConstants.CALLING_CLASS_NAME_DATA_EXTRA,
                MainActivity.TAG);
        startService(intent);
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    public void showGoogleErrorDialog(ConnectionResult connectionResult) {
        try {
            // Create a fragment for the error dialog
            connectionResult.startResolutionForResult(this, GoogleAPIConnectionConstants.REQUEST_RESOLVE_ERROR);
        }
        catch (IntentSender.SendIntentException e) {
            Log.e(MainActivity.TAG,"SendIntentException occurred.");
            checkGoogleApiAvailability();
        }
    }

    public boolean checkGoogleApiAvailability() {
        Log.d(MainActivity.TAG,"Checking Google API Availability");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            Log.d(MainActivity.TAG,"Google API is not available");
            Log.d(MainActivity.TAG,"Result Code: " + resultCode);
            if(googleApiAvailability.isUserResolvableError(resultCode)) {
                Log.d(MainActivity.TAG,"Is a human resolvable error.");
                setAppResolvingError(true);
                googleApiAvailability.getErrorDialog(this, resultCode, GoogleAPIConnectionConstants.REQUEST_RESOLVE_ERROR).show();
            }
            else {
                Log.d(MainActivity.TAG,"Error is not human resolvable");
            }
            return false;
        }
        else {
            Log.d(MainActivity.TAG,"Google API is available");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleAPIConnectionConstants.REQUEST_RESOLVE_ERROR) {
            setAppResolvingError(false);
            if (resultCode == RESULT_OK) {
                //Google Play Services OK
                start();
            }
            else {
                //Google Play Services still do not work. Show non-fixable error dialog
                alertUserAboutError(null,null,ForecastRetrievalServiceConstants.GOOGLE_CONNECTION_ERROR);
            }
        }
    }

    //If the app crashes/stops make sure to save if trying to resolve GoogleAPIClient connection error
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(GoogleAPIConnectionConstants.STATE_RESOLVING_ERROR, isAppResolvingError());
    }

    //Apply theme specific images to the View
    private void applyCurrentTheme() {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = getTempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(this);
        String theme = tempestatibusApplicationSettings.getAppThemePreference();
        mRefreshImageView.setBackgroundResource(TempestatibusApplicationSettings.getRefreshId());
        mDegreeImageView.setBackgroundResource(TempestatibusApplicationSettings.getLargeDegreeId(theme));
        mDewValueSmallDegree.setBackgroundResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        mApparentTemperatureValueSmallDegree.setBackgroundResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        mSettingsImageView.setBackgroundResource(TempestatibusApplicationSettings.getSettingsIconId());
        mHourglassIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getHourglassId());
        mSaveIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getSaveIconId());
        mSearchIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getSearchIconId());
    }

    private void createSaveLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,TempestatibusApplicationSettings.getAlertDialogThemeId());
        //Set up the title
        builder.setTitle("Save Location");
        //Set up the input EditText
        final EditText input = (EditText)getLayoutInflater().inflate(R.layout.save_location_dialog_edit_text,null);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.setUserSavedName(input.getText().toString());
                MainActivity.this.saveLocation();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
    }

    private void saveLocation() {
        setLocationDataSource(new CachedLocationDataSource(this));
        //Need a dialog for user to enter the display name
        getLocationDataSource().createLocation(getLocation(), getUserSavedName(), getStandardAddress(),getShortenedAddress());
        Toast.makeText(this, "Saved the Location!", Toast.LENGTH_SHORT).show();
    }
}