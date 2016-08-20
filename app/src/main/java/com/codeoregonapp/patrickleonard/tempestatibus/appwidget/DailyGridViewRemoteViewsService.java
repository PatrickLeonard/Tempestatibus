package com.codeoregonapp.patrickleonard.tempestatibus.appwidget;

import android.content.Intent;

import android.widget.RemoteViewsService;

/**
 * Base Adapter class to provide views for the Daily GridView in the first Widget
 * Created by Patrick Leonard on 1/10/2016.
 */
public class DailyGridViewRemoteViewsService extends RemoteViewsService {
    public static final String TAG = DailyGridViewRemoteViewsService.class.getSimpleName();
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DailyGridRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
