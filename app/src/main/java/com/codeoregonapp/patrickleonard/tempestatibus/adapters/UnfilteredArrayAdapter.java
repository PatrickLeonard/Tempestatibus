package com.codeoregonapp.patrickleonard.tempestatibus.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;

public class UnfilteredArrayAdapter extends ArrayAdapter<String> {

    public UnfilteredArrayAdapter(Context context, int textViewResourceId, ArrayList<String> arrayList) {
        super(context, textViewResourceId); addAll(arrayList);
    }

    private static final NoFilter NO_FILTER = new NoFilter();

    /**
     * Override ArrayAdapter.getFilter() to return our own filtering.
     */
    @Override
    public Filter getFilter() {
        return NO_FILTER;
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
