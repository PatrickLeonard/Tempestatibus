package com.codeoregonapp.patrickleonard.tempestatibus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;
import com.codeoregonapp.patrickleonard.tempestatibus.weather.Day;

import java.util.List;

/**
 * This class is the adapter for the Day Forecast ListView
 * Created by Patrick Leonard on 11/7/2015.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private List<Day> mDays;

    public DayAdapter(Context context, List<Day> days) {
        mContext = context; mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.size();
    }

    @Override
    public Object getItem(int position) {
        return mDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        //If not already tagged to the ViewHolder inflate the view and bind to the View variables
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_grid_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView)convertView.findViewById(R.id.dailyGridIconImageView);
            viewHolder.maxTemperatureSmallDegreeSymbol = (ImageView)convertView.findViewById(R.id.dailyGridMMaxTempSmallDegreeImageView);
            viewHolder.minTemperatureSmallDegreeSymbol = (ImageView)convertView.findViewById(R.id.dailyGridMMinTempSmallDegreeImageView);
            viewHolder.dayLabel = (TextView)convertView.findViewById(R.id.dayAbbreviationLabel);
            viewHolder.maxTemperatureLabel = (TextView)convertView.findViewById(R.id.dailyGridMaxTemperatureLabel);
            viewHolder.minTemperatureLabel = (TextView)convertView.findViewById(R.id.dailyGridMinTemperatureLabel);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag(); //Views already bound
        }

        //Get the data from the list for the corresponding item
        Day day =  mDays.get(position);
        String theme = getTheme(); // Get theme to set the correct images
        viewHolder.iconImageView.setImageResource(day.getIconId(theme,mContext));
        viewHolder.maxTemperatureSmallDegreeSymbol.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        viewHolder.minTemperatureSmallDegreeSymbol.setImageResource(TempestatibusApplicationSettings.getSmallDegreeId(theme));
        //Set the other data items
        viewHolder.maxTemperatureLabel.setText(String.format("%s", day.getTemperatureMax()));
        viewHolder.minTemperatureLabel.setText(String.format("%s",day.getTemperatureMin()));
        viewHolder.dayLabel.setText(day.getDayOfTheWeekAbbreviation());

        return convertView;
    }

    //Reused static class to bind the items in the Collection View
    private static class ViewHolder {
        ImageView iconImageView;
        ImageView maxTemperatureSmallDegreeSymbol;
        ImageView minTemperatureSmallDegreeSymbol;
        TextView maxTemperatureLabel;
        TextView minTemperatureLabel;
        TextView dayLabel;
    }

    //Utility Function for retrieving the application theme from the SharedPreferences
    private String getTheme() {
        TempestatibusApplicationSettings tempestatibusApplicationSettings = new TempestatibusApplicationSettings();
        tempestatibusApplicationSettings.createSharedPreferenceContext(mContext);
        return tempestatibusApplicationSettings.getAppThemePreference();
    }
}
