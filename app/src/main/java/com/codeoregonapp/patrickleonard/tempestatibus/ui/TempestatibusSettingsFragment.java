package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;

/**
 * Settings Fragment for displaying the settings options to the users.
 * This class applies some styles to the SwitchPreferences in the fragment
 * Created by Patrick Leonard on 11/30/2015.
 */
public class TempestatibusSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedFragmentState) {
        super.onCreate(savedFragmentState);
        //Apply the preferences to the view so it shows up properly set to the user
        addPreferencesFromResource(R.xml.preferences);
        SwitchPreference appUnitsSwitchPreference = (SwitchPreference)findPreference(TempestatibusApplicationSettings.APP_UNITS_PREFERENCE);
        appUnitsSwitchPreference.setWidgetLayoutResource(TempestatibusApplicationSettings.getSettingsPreferenceSwitchWidgetLayout());
        appUnitsSwitchPreference.setChecked(SettingsActivity.getUnitsPreference((PreferenceActivity)getActivity()));
        SwitchPreference hourlyExtendSwitchPreference = (SwitchPreference)findPreference(TempestatibusApplicationSettings.HOURLY_EXTEND_APP_PREFERENCE);
        hourlyExtendSwitchPreference.setWidgetLayoutResource(TempestatibusApplicationSettings.getSettingsPreferenceSwitchWidgetLayout());
        hourlyExtendSwitchPreference.setChecked(SettingsActivity.getHourlyExtendPreference((PreferenceActivity)getActivity()));
    }
}
