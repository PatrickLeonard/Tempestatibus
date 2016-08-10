package com.codeoregonapp.patrickleonard.tempestatibus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.codeoregonapp.patrickleonard.tempestatibus.R;

import java.util.ArrayList;

public class UnfilteredArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mAddressList;

    public UnfilteredArrayAdapter(Context context, int textViewResourceId, ArrayList<String> arrayList) {
        super(context, textViewResourceId); addAll(arrayList); mAddressList = arrayList; mContext = context;
    }

    private static final NoFilter NO_FILTER = new NoFilter();

    /**
     * Override ArrayAdapter.getFilter() to return our own filtering.
     */
    @Override
    public Filter getFilter() {
        return NO_FILTER;
    }

    @Override
    public int getCount() { return mAddressList.size(); }

    @Override
    public String getItem(int position) { return mAddressList.get(position); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //If not already tagged to the TextViewHolder inflate the view and bind to the View variables
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.powered_by_google_item,parent,false);
            viewHolder.addressLabel = (TextView)convertView.findViewById(R.id.addressText);
            viewHolder.subText = (TextView)convertView.findViewById(R.id.subText);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag(); //Views already bound
        }


        //Get the data from the list for the corresponding item
        String address = mAddressList.get(position);
        viewHolder.addressLabel.setText(address);
        viewHolder.subText.setText("Tap to see weather forecast.");
        viewHolder.addressLabel.setVisibility(View.VISIBLE);
        viewHolder.subText.setVisibility(View.VISIBLE);

        return convertView;
    }

    //Reused static class to bind the items in the Collection View
    private static class ViewHolder {
        TextView addressLabel;
        TextView subText;
    }

    /**
     * Class which does not perform any filtering. Filtering is already done by
     * the web service when asking for the list, so there is no need to do any
     * more as well. This way, ArrayAdapter.mOriginalValues is not used when
     * calling e.g. ArrayAdapter.add(), but instead ArrayAdapter.mObjects is
     * updated directly and methods like getCount() return the expected result.
     */
    private static class NoFilter extends Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            return new FilterResults();
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Do nothing
        }
    }
}
