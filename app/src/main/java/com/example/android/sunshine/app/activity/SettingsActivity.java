package com.example.android.sunshine.app.activity;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.android.sunshine.app.R;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_metrics_key)));
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value){
        String stringValue = value.toString();

        if(pref instanceof ListPreference){
            ListPreference listPreference = (ListPreference)pref;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if(prefIndex >= 0){
                pref.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            pref.setSummary(stringValue);
        }
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference pref){
        pref.setOnPreferenceChangeListener(this);
        onPreferenceChange(pref,
                PreferenceManager
                        .getDefaultSharedPreferences(pref.getContext())
                        .getString(pref.getKey(),""));
    }

}
