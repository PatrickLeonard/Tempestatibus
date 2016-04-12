package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.codeoregonapp.patrickleonard.tempestatibus.R;

/**
 * Customer SwitchPreference that listens for it's own changes
 * Created by Patrick Leonard on 12/26/2015.
 */
public class CustomSwitchPreference extends SwitchPreference {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwitchPreference(Context context) {
        super(context);
    }

    protected void onBindView(View view) {
        // Clean listener before invoke SwitchPreference.onBindView
        ViewGroup viewGroup= (ViewGroup)view;
        clearListenerInViewGroup(viewGroup);
        super.onBindView(view);
        //make sure the custom switch is set to the preferences correctly when the user sees the View
        final Switch mySwitch = (Switch) view.findViewById(R.id.preferenceSwitch);
        Boolean initVal = this.getPersistedBoolean(false);
        if (initVal) {
            mySwitch.setChecked(true);
        }
        this.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mySwitch.setChecked((Boolean)newValue);
                return true;
            }
        });
    }

    //Only let the specific switch be affected by preference changes
    private void clearListenerInViewGroup(ViewGroup viewGroup) {
        if (null == viewGroup) {
            return;
        }
        int count = viewGroup.getChildCount();
        for(int n = 0; n < count; ++n) {
            View childView = viewGroup.getChildAt(n);
            if(childView instanceof Switch) {
                final Switch switchView = (Switch) childView;
                switchView.setOnCheckedChangeListener(null);
                return;
            } else if (childView instanceof ViewGroup){
                ViewGroup childGroup = (ViewGroup)childView;
                clearListenerInViewGroup(childGroup);
            }
        }
    }
}
