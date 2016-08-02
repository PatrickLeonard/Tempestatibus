package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.SearchedLocationForecastRetrievalService;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.SearchedLocationForecastRetrievalServiceConstants;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Forecast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is Searched Location Activity. It leads leads to two others: DailyForecastActivity and
 * HourlyForecastActivity for data using the searched for location
 * Author: Patrick Leonard 11/1/2015
 */
public class SearchedLocationActivity extends AppCompatActivity {

    //Static variables for communications between services both internal and external
    public static final String TAG = SearchedLocationActivity.class.getSimpleName();
    public static final String DAILY_FORECAST_PARCEL = "DAILY FORECAST"; //Daily Forecast data
    public static final String HOURLY_FORECAST_PARCEL = "HOURLY FORECAST"; //Hourly Forecast data
    public static final String SEARCHED_LOCATION_EXTRA = "SEARCHED_LOCATION"; //Searched Location Data
    public static final String SEARCHED_STANDARD_ADDRESS_EXTRA = "SEARCHED_STANDARD_ADDRESS"; //Searched Address Data
    public static final String SEARCHED_SHORTENED_ADDRESS_EXTRA = "SEARCHED_SHORTENED_ADDRESS"; //Searched Address Data
    public static final String CURRENT_LOCATION_EXTRA = "CURRENT_LOCATION"; //Current Location Data
    public static final String CURRENT_STANDARD_ADDRESS_EXTRA = "CURRENT_STANDARD_ADDRESS"; //Current Address Data
    public static final String CURRENT_SHORTENED_ADDRESS_EXTRA = "CURRENT_SHORTENED_ADDRESS"; //Current Address Data
    public static final String ADDRESS_EXTRA = "ADDRESS"; //Address Data sent to DailyForecastActivity
    private String mSearchedStandardAddress;
    private String mSearchedShortenedAddress;
    private Location mSearchedLocation;
    private Location mCurrentLocation;
    private String mCurrentStandardAddress;
    private String mCurrentShortenedAddress;
    private Forecast mForecast;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;
    private CachedLocationDataSource mCachedLocationDataSource;
    private String mUserSavedName;

    public Forecast getForecast() { return mForecast; }

    public void setForecast(Forecast forecast) {
        mForecast = forecast;
    }

    public String getSearchedStandardAddress() {
        return mSearchedStandardAddress;
    }

    public void setSearchedStandardAddress(String searchedStandardAddress) {
        mSearchedStandardAddress = searchedStandardAddress;
    }
    
    public String getSearchedShortenedAddress() {
        return mSearchedShortenedAddress;
    }

    public void setSearchedShortenedAddress(String searchedShortenedAddress) {
        mSearchedShortenedAddress = searchedShortenedAddress;
    }
    
    public Location getSearchedLocation() {
        return mSearchedLocation;
    }

    public void setSearchedLocation(Location searchedLocation) {
        mSearchedLocation = searchedLocation;
    }

    public String getUserSavedName() {
        return mUserSavedName;
    }

    public void setUserSavedName(String userSavedName) {
        mUserSavedName = userSavedName;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public String getCurrentStandardAddress() {
        return mCurrentStandardAddress;
    }

    public void setCurrentStandardAddress(String currentStandardAddress) {
        mCurrentStandardAddress = currentStandardAddress;
    }

    public String getCurrentShortenedAddress() {
        return mCurrentShortenedAddress;
    }

    public void setCurrentShortenedAddress(String currentShortenedAddress) {
        mCurrentShortenedAddress = currentShortenedAddress;
    }
    
    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        }
        return mTempestatibusApplicationSettings;
    }

    public CachedLocationDataSource getLocationDataSource() {
        return mCachedLocationDataSource;
    }

    public void setLocationDataSource(CachedLocationDataSource cachedLocationDataSource) {
        mCachedLocationDataSource = cachedLocationDataSource;
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
    TextView mPowerByForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        //Set the theme, the content layout, and Bind views using Butterknife
        setContentView(R.layout.searched_location_activity);
        ButterKnife.bind(this);
        //Apply the current app theme, make the indeterminant progress bar visible
        applyCurrentTheme();
        //Get the Location and Address Data
        setSearchedLocation((Location) getIntent().getParcelableExtra(SearchedLocationActivity.SEARCHED_LOCATION_EXTRA));
        setSearchedStandardAddress(getIntent().getStringExtra(SearchedLocationActivity.SEARCHED_STANDARD_ADDRESS_EXTRA));
        setSearchedShortenedAddress(getIntent().getStringExtra(SearchedLocationActivity.SEARCHED_SHORTENED_ADDRESS_EXTRA));
        //Get the Current Location and Address data
        setCurrentLocation((Location) getIntent().getParcelableExtra(SearchedLocationActivity.CURRENT_LOCATION_EXTRA));
        setCurrentStandardAddress(getIntent().getStringExtra(SearchedLocationActivity.CURRENT_STANDARD_ADDRESS_EXTRA));
        setCurrentShortenedAddress(getIntent().getStringExtra(SearchedLocationActivity.CURRENT_SHORTENED_ADDRESS_EXTRA));
        //Set the onClick listener for the data refresh button
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle refresh and get data
                toggleRefresh();
                startSearchedLocationForecastRetrievalService();
            }
        });
        //Set the onClick listener for the data refresh button
        mSaveIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSaveLocationDialog();
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
        mSearchIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getForecast() != null) {
                    Location location = new Location(LocationManager.PASSIVE_PROVIDER);
                    location.setLatitude(getCurrentLocation().getLatitude());
                    location.setLongitude(getCurrentLocation().getLongitude());
                    Intent intent = new Intent(SearchedLocationActivity.this, SearchForLocationActivity.class);
                    intent.putExtra(SearchForLocationActivity.CURRENT_STANDARD_ADDRESS_EXTRA, getCurrentStandardAddress());
                    intent.putExtra(SearchForLocationActivity.CURRENT_SHORTENED_ADDRESS_EXTRA, getCurrentShortenedAddress());
                    intent.putExtra(SearchForLocationActivity.CURRENT_LOCATION_EXTRA, location);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mPowerByForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to open the default browser and travel to http://forecast.io
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forecast_io_url)));
                startActivity(browserIntent);
            }
        });
        mProgressBar.setVisibility(View.VISIBLE);
        //Make the refresh button invisible
        mRefreshImageView.setVisibility(View.INVISIBLE);
        //Get forecast data
        startSearchedLocationForecastRetrievalService();
    }

    private void saveLocation() {
        setLocationDataSource(new CachedLocationDataSource(this));
        //Need a dialog for user to enter the display name
        getLocationDataSource().createLocation(getLocation(),getUserSavedName(),getSearchedStandardAddress(),getSearchedShortenedAddress());
        Toast.makeText(this, "Saved the Location!", Toast.LENGTH_SHORT).show();
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
            mLocationLabel.setText(String.format("%s", getSearchedStandardAddress()));
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

    //Start the DailyForecastActivity with the List<Day> data
    public void startDailyActivity(int item) {
        //Don't start this activity if there is no forecast data
        if (getForecast() != null && getForecast().getDailyForecastList() != null) {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra(SearchedLocationActivity.ADDRESS_EXTRA, getSearchedStandardAddress());
            intent.putExtra(SearchedLocationActivity.DAILY_FORECAST_PARCEL, getForecast().getDailyForecastList().get(item));
            startActivity(intent);
        }
    }

    public void startHourlyActivity() {
        //Don't start this activity if there is no forecast data
        if (getForecast() != null && getForecast().getHourlyForecastList() != null) {
            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putParcelableArrayListExtra(SearchedLocationActivity.HOURLY_FORECAST_PARCEL, (ArrayList<? extends Parcelable>) getForecast().getHourlyForecastList());
            startActivity(intent);
        }
    }

    //Start the ForecastRetrievalServices
    protected void startSearchedLocationForecastRetrievalService() {
        Intent intent = new Intent(this, SearchedLocationForecastRetrievalService.class);
        SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver
                searchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver
                = new SearchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver(new Handler(), this);
        intent.putExtra(SearchedLocationForecastRetrievalServiceConstants.SEARCHED_LOCATION_ACTIVITY_RECEIVER, searchedLocationForecastRetrievalServiceSearchedLocationActivityResultReceiver);
        intent.putExtra(SearchedLocationForecastRetrievalServiceConstants.LOCATION_DATA_EXTRA, getSearchedLocation());
        startService(intent);
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
        mHourglassIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getHourglassId());
        mSaveIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getSaveIconId());
        mSearchIconImageView.setBackgroundResource(TempestatibusApplicationSettings.getSearchIconId());
    }

    @NonNull
    private Location getLocation() {
        Location location = new Location(LocationManager.PASSIVE_PROVIDER);
        location.setLatitude(getForecast().getLatitude());
        location.setLongitude(getForecast().getLongitude());
        return location;
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
                SearchedLocationActivity.this.setUserSavedName(input.getText().toString());
                SearchedLocationActivity.this.saveLocation();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SearchedLocationActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SearchedLocationActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SearchedLocationActivity.this,
                TempestatibusApplicationSettings.getTextColorId()));
    }
}