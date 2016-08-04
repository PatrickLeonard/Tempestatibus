package com.codeoregonapp.patrickleonard.tempestatibus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.database.SavedLocationModel;

import java.util.ArrayList;

/**
 * This class is the adapter for the Day Forecast ListView
 * Created by Patrick Leonard on 11/7/2015.
 */
public class LocationAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SavedLocationModel> mSaveLocationsModels;

    public LocationAdapter(Context context, ArrayList<SavedLocationModel> saveLocationsModels) {
        mContext = context; mSaveLocationsModels = saveLocationsModels;
    }

    @Override
    public int getCount() {
        return mSaveLocationsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return mSaveLocationsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSaveLocationsModels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //If not already tagged to the ViewHolder inflate the view and bind to the View variables
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selectable_saved_location_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.locationNameLabel = (TextView)convertView.findViewById(R.id.savedLocationNameLabel);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag(); //Views already bound
        }
        //Get the data from the list for the corresponding item
        SavedLocationModel savedLocationModel =  mSaveLocationsModels.get(position);
        viewHolder.locationNameLabel.setText(savedLocationModel.getName());
        return convertView;
    }


    public void remove(int itemPosition) {
        mSaveLocationsModels.remove(itemPosition);
    }

    //Reused static class to bind the items in the Collection View
    private static class ViewHolder {
        TextView locationNameLabel;
    }
}
