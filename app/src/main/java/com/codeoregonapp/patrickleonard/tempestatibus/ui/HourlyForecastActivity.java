package com.codeoregonapp.patrickleonard.tempestatibus.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.adapters.HourAdapter;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Hour;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {
    public static final String TAG = HourlyForecastActivity.class.getSimpleName();
    ArrayList<Hour> mHours;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.poweredByForecast)TextView mPoweredByForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        //Set the theme, content view, and use ButterKnife to Bind the recycler view
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mHours = intent.getParcelableArrayListExtra(MainActivity.HOURLY_FORECAST_PARCEL);
        HourAdapter adapter = new HourAdapter(mHours,this);
        //Get the Hours data and set the new HourAdapter to the RecyclerView
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPoweredByForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to open the default browser and travel to http://forecast.io
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forecast_io_url)));
                startActivity(browserIntent);
            }
        });
    }

    //Keep up with theme changes on resume
    @Override
    protected void onResume() {
        SettingsActivity.setActivityTheme(this);
        super.onResume();
    }
}