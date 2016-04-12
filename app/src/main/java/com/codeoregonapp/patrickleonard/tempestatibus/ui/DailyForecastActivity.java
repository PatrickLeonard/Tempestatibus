package com.codeoregonapp.patrickleonard.tempestatibus.ui;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Day;


import butterknife.Bind;
import butterknife.ButterKnife;


public class DailyForecastActivity extends AppCompatActivity {

    public static final String TAG = DailyForecastActivity.class.getSimpleName();
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;
    private String mAddress;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        }
        return mTempestatibusApplicationSettings;
    }

    //ButterKnife used to Bind all of the many items being displayed to the user
    @Bind(R.id.locationLabel) TextView mLocationLabel;
    @Bind(R.id.timeLabel)
    TextView mTimeLabel;
    @Bind(R.id.maxTemperatureLabel)
    TextView mMaxTemperatureLabel;
    @Bind(R.id.minTemperatureLabel)
    TextView mMinTemperatureLabel;
    @Bind(R.id.maxTimeValue)
    TextView mMaxTimeValue;
    @Bind(R.id.minTimeValue)
    TextView mMinTimeValue;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precipValue)
    TextView mPrecipitationChanceValue;
    @Bind(R.id.summaryValue)
    TextView mSummaryValue;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.pressureValue)
    TextView mPressureValue;
    @Bind(R.id.windValue)
    TextView mWindValue;
    @Bind(R.id.moonValue)
    TextView mMoonValue;
    @Bind(R.id.cloudValue)
    TextView mCloudValue;
    @Bind(R.id.visibilityValue)
    TextView mVisibilityValue;
    @Bind(R.id.dewValue)
    TextView mDewValue;
    @Bind(R.id.ozoneValue)
    TextView mOzoneValue;
    @Bind(R.id.sunsetValue)
    TextView mSunsetValue;
    @Bind(R.id.sunriseValue)
    TextView mSunriseValue;
    @Bind(R.id.maxTemperatureDegreeImageView)
    ImageView mMaxTemperatureDegreeImageView;
    @Bind(R.id.minTemperatureDegreeImageView)
    ImageView mMinTemperatureDegreeImageView;
    @Bind(R.id.dewValueUnits)
    TextView mDewValueUnits;
    @Bind(R.id.dewValueSmallDegree)
    ImageView mDewValueSmallDegree;
    @Bind(R.id.temperatureValueUnits)
    TextView mTemperatureValueUnits;
    @Bind(R.id.maxApparentTemperatureValue)
    TextView mMaxApparentTemperatureValue;
    @Bind(R.id.maxApparentTemperatureValueSmallDegree)
    ImageView mMaxApparentTemperatureValueSmallDegree;
    @Bind(R.id.maxApparentTemperatureValueUnits)
    TextView mMaxApparentTemperatureUnits;
    @Bind(R.id.minApparentTemperatureValue)
    TextView mMinApparentTemperatureValue;
    @Bind(R.id.minApparentTemperatureValueSmallDegree)
    ImageView mMinApparentTemperatureValueSmallDegree;
    @Bind(R.id.minApparentTemperatureValueUnits)
    TextView mMinApparentTemperatureUnits;
    @Bind(R.id.poweredByForecast)
    TextView mPoweredByForecast;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);
        mPoweredByForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to open the default browser and travel to http://forecast.io
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forecast_io_url)));
                startActivity(browserIntent);
            }
        });
        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.ADDRESS_EXTRA);
        setAddress(address);
        Day day = intent.getParcelableExtra(MainActivity.DAILY_FORECAST_PARCEL);
        updateCurrentDisplay(day);
    }

    @Override
    protected void onResume() {
        SettingsActivity.setActivityTheme(this);
        super.onResume();
    }

    //Set the data that is displayed to the user....yes there is a lot.
    public void updateCurrentDisplay(Day day) {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = getTempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(this);
        String theme = mTempestatibusApplicationSettings.getAppThemePreference();
        mMaxTemperatureLabel.setText(String.format("%s", day.getTemperatureMax()));
        mMaxTemperatureDegreeImageView.setImageResource(TempestatibusApplicationSettings.getLargeDegreeId(theme));
        mMinTemperatureDegreeImageView.setImageResource(TempestatibusApplicationSettings.getLargeDegreeId(theme));
        mMinTemperatureLabel.setText(String.format("%s", day.getTemperatureMin()));
        mMaxTimeValue.setText(String.format("%s", day.getTimeOfDay(day.getTemperatureMaxTime())));
        mMinTimeValue.setText(String.format("%s", day.getTimeOfDay(day.getTemperatureMinTime())));
        mTemperatureValueUnits.setText(String.format("%s", getTemperatureUnits()));
        mMaxApparentTemperatureValue.setText(String.format("%s",day.getApparentTemperatureMax()));
        mMaxApparentTemperatureUnits.setText(String.format("%s", getTemperatureUnits()));
        mMaxApparentTemperatureValueSmallDegree.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        mMinApparentTemperatureValue.setText(String.format("%s",day.getApparentTemperatureMin()));
        mMinApparentTemperatureUnits.setText(String.format("%s", getTemperatureUnits()));
        mMinApparentTemperatureValueSmallDegree.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        mTimeLabel.setText(String.format("%s", day.getWellFormattedDate()));
        mHumidityValue.setText(String.format("%s%%", day.getHumidity()));
        mPrecipitationChanceValue.setText(String.format("%s%%", day.getPrecipitationProbability()));
        mWindValue.setText(String.format("%s %s %s", day.getWindSpeed(), getVelocityUnits(), determineWindBearing(day)));
        mMoonValue.setText(String.format("%s",day.getMoonPhase()));
        mDewValue.setText(String.format("%s",day.getDewPoint()));
        mDewValueUnits.setText(String.format("%s", getTemperatureUnits()));
        mDewValueSmallDegree.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        mVisibilityValue.setText(String.format("%s %s", day.getVisibility(), getDistanceUnits()));
        mPressureValue.setText(String.format("%s %s", day.getPressure(), getPressureUnits()));
        mOzoneValue.setText(String.format("%s %s",day.getOzone(),getString(R.string.units_dobson)));
        mSunsetValue.setText(String.format("%s",day.getTimeOfDay(day.getSunsetTime())));
        mSunriseValue.setText(String.format("%s",day.getTimeOfDay(day.getSunriseTime())));
        mCloudValue.setText(String.format("%s",day.getCloudCover()));
        mSummaryValue.setText(String.format("%s", day.getSummary()));
        Log.v(MainActivity.TAG, "Address: " + getAddress());
        mLocationLabel.setText(String.format("%s", getAddress()));
        Drawable drawable = ContextCompat.getDrawable(this, day.getIconId(theme,this));
        mIconImageView.setImageDrawable(drawable);
    }

    //Utilities for displaying the proper units and other associated Strings with the data
    //Self explanatory I would think...
    private String determineWindBearing(Day day) {
        String windBearing;
        if(!day.getWindSpeed().equals("0")) {
            windBearing = day.getWindBearing();
        }
        else {
            windBearing = "";
        }
        return windBearing;
    }

    private String getDistanceUnits() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(this);
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return getString(R.string.si_units_kilometers);
        }
        else {
            return getString(R.string.us_unit_miles);
        }
    }

    private String getVelocityUnits() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(this);
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return getString(R.string.si_units_kilometers_per_hour);
        }
        else {
            return getString(R.string.us_units_miles_per_hour);
        }
    }

    private String getTemperatureUnits() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(this);
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return getString(R.string.si_units_celsius);
        }
        else {
            return getString(R.string.us_units_fahrenheit);
        }
    }

    private String getPressureUnits() {
        getTempestatibusApplicationSettings().createSharedPreferenceContext(this);
        if(getTempestatibusApplicationSettings().getAppUnitsPreference()) {
            return getString(R.string.si_units_hectoPascals);
        }
        else {
            return getString(R.string.us_units_millibars);
        }
    }

}
