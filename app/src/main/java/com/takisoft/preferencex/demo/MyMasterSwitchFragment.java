package com.takisoft.preferencex.demo;

import android.os.Bundle;

import com.takisoft.preferencex.PreferenceFragmentCompatMasterSwitch;

import androidx.annotation.Nullable;

public class MyMasterSwitchFragment extends PreferenceFragmentCompatMasterSwitch {
    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_master, rootKey);
    }
}
