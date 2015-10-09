package com.omar.dardear.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.omar.dardear.popularmovies.data.MoviesContract;


public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        // TODO: Add preferences from XML

        addPreferencesFromResource(R.xml.pref_main);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
    }





    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));


        if(preference.getKey().equals("@string/pref_sort_key"))
        {
            getApplicationContext().getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI, null, null);
            ContentValues MovieValue = new ContentValues();
            MovieValue.put(MoviesContract.MoviesEntry.COLUMN_SORT_INDEX, 0);
            getApplicationContext().getContentResolver().update(MoviesContract.MoviesEntry.CONTENT_URI, MovieValue, null, null);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {


        String stringValue = value.toString();
        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {

            preference.setSummary(stringValue);
        }
        return true;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
