package com.codeoregonapp.patrickleonard.tempestatibus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Hour;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is the adapter for the Hourly Forecast RecyclerView
 * Created by Patrick Leonard on 11/7/2015.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private ArrayList<Hour> mHours;
    private static Context mContext;
    private String mUnits;

    public HourAdapter(ArrayList<Hour> hours, Context context) {
        mHours = hours;
        mContext = context;
        mUnits = getTemperatureUnits();
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.hourly_list_item, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mHours.size();
    }

    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Use ButterKnife to Bind the Views to the variables
        @Bind(R.id.hourlyTemperatureLabel) TextView mTemperatureLabel;
        @Bind(R.id.timeLabel) TextView mTimeLabel;
        @Bind(R.id.summaryLabel) TextView mSummaryLabel;
        @Bind(R.id.hourlyIconImageView) ImageView mIconImageView;
        @Bind(R.id.dateLabel) TextView mDateLabel;
        @Bind(R.id.dateRelativeLayout)RelativeLayout mDateRelativeLayout;
        @Bind(R.id.smallDegreeImageView)ImageView mSmallDegreeImageView;
        @Bind(R.id.hourlyTempUnits) TextView mHourTempUnits;

        public HourViewHolder(View itemView) {
            //Call super, user ButterKnife to bind views, and set OnClick Listener
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindHour(Hour hour) {
            //Set the data and correct images using the Application theme
            setMonthAndDay(hour);
            String theme = getTheme();
            mTemperatureLabel.setText(String.format("%s", hour.getTemperature()));
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mIconImageView.setImageResource(hour.getIconId(theme, mContext));
            mSmallDegreeImageView.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
            mHourTempUnits.setText(mUnits);
        }

        private void setMonthAndDay(Hour hour) {
            //If it is midnight, display the new Date and add a "line of dashes" to separate the day in the
            //Recycler view....maybe add a Divider?
            if(hour.isMidnight()) {
                mDateLabel.setVisibility(View.VISIBLE);
                mDateRelativeLayout.setVisibility(View.VISIBLE);
                mDateLabel.setText(String.format("---------------  %s  ---------------",hour.getMonthAndDay()));
            }
            else {
                //Else make those Views "gone"
                mDateLabel.setVisibility(View.GONE);
                mDateRelativeLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            //Small onClick to display what is already shown in a Toast. Can expand here with
            //data from the Dark Sky Forecast API
            String time = mTimeLabel.getText().toString();
            String temp = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String.format(mContext.getString(R.string.hour_list_toast_message),
                    time, temp, summary);
            Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
        }
    }

    //Utility to get the theme.
    private String getTheme() {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(mContext);
        return tempestatibusApplicationSettings.getAppThemePreference();
    }

    private String getTemperatureUnits() {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(mContext);
        if(tempestatibusApplicationSettings.getAppUnitsPreference()) {
            return mContext.getString(R.string.si_units_celsius);
        }
        else {
            return mContext.getString(R.string.us_units_fahrenheit);
        }
    }
}
