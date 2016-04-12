package com.codeoregonapp.patrickleonard.tempestatibus.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity to allow the user to configure the app widget. Simple and derpy
 * Created by Patrick Leonard on 1/30/2016.
 */
public class TempestatibusWidgetConfigure extends AppCompatActivity {

    private static final String TAG = TempestatibusWidgetConfigure.class.getSimpleName();
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;

    //User ButterKnife to bind the Views to variables
    @Bind(R.id.matchAppThemeButton)
    AppCompatRadioButton mMatchAppRadio;
    @Bind(R.id.clearAppThemeButton)
    AppCompatRadioButton mClearAppRadio;
    @Bind(R.id.whiteTextButton)
    AppCompatRadioButton mWhiteTextRadio;
    @Bind(R.id.blackTextButton)
    AppCompatRadioButton mBlackTextRadio;
    @Bind(R.id.proceedButton)
    AppCompatButton mProceedButton;
    @Bind(R.id.text_color_title)
    TextView mTextColorTitleTextView;
    private int mAppWidgetId;
    private Intent mResultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        mTempestatibusApplicationSettings.createSharedPreferenceContext(getApplicationContext());
        setTheme(mTempestatibusApplicationSettings.getThemeId(mTempestatibusApplicationSettings.getAppThemePreference()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_configuration_activity);
        ButterKnife.bind(this);
        //Hide the switches/title for the text color until the user selects the clear background
        mTextColorTitleTextView.setVisibility(View.INVISIBLE);
        mWhiteTextRadio.setVisibility(View.INVISIBLE);
        mBlackTextRadio.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        mMatchAppRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMatchAppRadio.setChecked(true);  //Check this selection
                mClearAppRadio.setChecked(false); //un-check the other selection

                //Hide the text color selections if a user chooses to match the app theme
                mTextColorTitleTextView.setVisibility(View.INVISIBLE);
                mWhiteTextRadio.setVisibility(View.INVISIBLE);
                mBlackTextRadio.setVisibility(View.INVISIBLE);
            }
        });

        mClearAppRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClearAppRadio.setChecked(true); //Check this selection
                mMatchAppRadio.setChecked(false); //un-check the other selection

                //Make the text color selections visible is a user chooses clear background
                mTextColorTitleTextView.setVisibility(View.VISIBLE);
                mWhiteTextRadio.setVisibility(View.VISIBLE);
                mBlackTextRadio.setVisibility(View.VISIBLE);
            }
        });

        mBlackTextRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlackTextRadio.setChecked(true); //Check this selection
                mWhiteTextRadio.setChecked(false); //un-check the other
            }
        });

        mWhiteTextRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhiteTextRadio.setChecked(true); //Check this selection
                mBlackTextRadio.setChecked(false); //un-check the other
            }
        });

        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedWidgetTheme(mAppWidgetId); //Execute these functions when the user clicks
                        UpdateWidgetAndFinish();  //Make sure it happens on the main ui thread
                    }
                });
            }
        });
        mResultValue = new Intent();
        mResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_CANCELED, mResultValue); //Set canceled so if backed out of will exit clean
    }

    private void UpdateWidgetAndFinish() {
        Intent intent = new Intent(getApplicationContext(),
                WidgetForecastUpdateService.class);
        int[] widgetIds = {mAppWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        startService(intent);
        setResult(RESULT_OK, mResultValue);
        finish(); //Finish this activity with OK result
    }

    private void setSelectedWidgetTheme(int appWidgetId) {
        if(mMatchAppRadio.isChecked()) {
            mTempestatibusApplicationSettings.setWidgetThemePreference(mTempestatibusApplicationSettings.getAppThemePreference(),appWidgetId);
        }
        else if(mClearAppRadio.isChecked() && mWhiteTextRadio.isChecked()) {
            mTempestatibusApplicationSettings.setWidgetThemePreference(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,appWidgetId);
        }
        else if(mClearAppRadio.isChecked() && mBlackTextRadio.isChecked()) {
            mTempestatibusApplicationSettings.setWidgetThemePreference(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,appWidgetId);
        }
        else {
            Log.e(TempestatibusWidgetConfigure.TAG,"Bad selection for app widget");
        }
    }

}