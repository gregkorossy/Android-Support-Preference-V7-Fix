package com.takisoft.preferencex.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.takisoft.preferencex.PreferenceFragmentCompatMasterSwitch;

public class MyMasterSwitchFragment extends PreferenceFragmentCompatMasterSwitch {
    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_master, rootKey);

        getMasterSwitch().setOnPreferenceChangeListener(new OnMasterSwitchChangeListener() {
            @Override
            public boolean onMasterSwitchChange(boolean newValue) {
                updatePreferences(newValue);
                return true;
            }
        });

        updatePreferences(getMasterSwitch().isChecked());
    }

    private void updatePreferences(boolean enabled) {
        findPreference("cat_enabled").setVisible(enabled);
        findPreference("cat_disabled").setVisible(!enabled);
    }
}
