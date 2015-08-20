package com.takisoft.preferencefix;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }
}
