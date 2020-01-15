package com.takisoft.preferencex.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.takisoft.preferencex.PreferenceFragmentCompat;
import com.takisoft.preferencex.PreferenceShowHide;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A placeholder fragment containing a simple view.
 * Also illustrates optional deployment of {@link PreferenceShowHide} which allows for
 * expansion/collapse via 'tap' of PreferenceCategory title.
 */
public class MyPreferenceFragment extends PreferenceFragmentCompat {

    PreferenceShowHide showHide;
    RecyclerView RV;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        testDynamicPrefs();

        // Collapse the UI so the user sees only the PreferenceCategory titles.
        // Tapping the title toggles visibility of the actual Preferences.
        // Exception: If PreferenceCategory title 'isEmpty()' children are not hidden.
        showHide = new PreferenceShowHide(this, null, true);

        Preference prefEmptyCheck = findPreference("pref_empty_check");

        if (prefEmptyCheck != null) {
            prefEmptyCheck.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!(Boolean) newValue) {
                        findPreference("pref_empty_categ").setTitle(null);
                    } else {
                        findPreference("pref_empty_categ").setTitle("Now you see me");
                    }

                    return true;
                }
            });
        }
    }

    private void testDynamicPrefs() {
        final Context ctx = getPreferenceManager().getContext(); // this is the material styled context

        final PreferenceCategory dynamicCategory = (PreferenceCategory) findPreference("pref_categ");

        Preference prefAdd = findPreference("pref_add");
        if (prefAdd != null) {
            prefAdd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                private int n = 0;

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Preference newPreference = new Preference(ctx);

                    newPreference.setTitle("New preference " + n++);
                    newPreference.setSummary(Long.toString(System.currentTimeMillis()));

                    if (dynamicCategory != null) {
                        dynamicCategory.addPreference(newPreference);
                    }
                    return true;
                }
            });
        }
    }

    /** Supply the RecyclerView. NB: Indirectly called by onCreateView() <br>
    NB: This is required only for deployment of {@link PreferenceShowHide} */
    @Override public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RV = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        if (RV!=null) {                             // Probably default: R.id.recycler_view
            Log.d("onCreateRecyclerView", "Default RecyclerView located");
            showHide.setRecyclerView(RV);
            RV.setLayoutManager(onCreateLayoutManager());
            return RV;
        }
        Log.d("onCreateRecyclerView", "Could not supply default RecyclerView to 'ShowHide' helper!");
        // If you are using a custom layout you must still provide the RecyclerView ...
        // ... For example: parent.findViewById(R.id.myRecyclerView)
        Log.d("onCreateRecyclerView", "See also: themes.xml 'PreferenceThemeOverlay'");
        return RV;
    }

}
