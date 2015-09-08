package com.takisoft.preferencefix;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        testDynamicPrefs();
    }

    private void testDynamicPrefs() {
        final Context ctx = getPreferenceManager().getContext(); // this is the material styled context

        final PreferenceCategory dynamicCategory = (PreferenceCategory) findPreference("pref_categ");

        Preference prefAdd = findPreference("pref_add");
        prefAdd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            private int n = 0;

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Preference newPreference = new Preference(ctx);

                newPreference.setTitle("New preference " + n++);
                newPreference.setSummary(Long.toString(System.currentTimeMillis()));

                dynamicCategory.addPreference(newPreference);
                return true;
            }
        });
    }
}
