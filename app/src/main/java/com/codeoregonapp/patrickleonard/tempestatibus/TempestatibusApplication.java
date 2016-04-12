package com.codeoregonapp.patrickleonard.tempestatibus;

import android.preference.PreferenceManager;

/**
 * Application class to setup the default preferences upon app install
 * Created by Patrick Leonard on 11/30/2015.
 */
public class TempestatibusApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
