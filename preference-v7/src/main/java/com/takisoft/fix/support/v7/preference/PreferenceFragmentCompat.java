package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompatFix;
import android.support.v7.preference.EditTextPreferenceFix;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceManagerFix;

import java.lang.reflect.Field;

public abstract class PreferenceFragmentCompat extends android.support.v7.preference.PreferenceFragmentCompat {
    private static final String FRAGMENT_DIALOG_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    private static Field preferenceManagerField;

    static {
        Field[] fields = android.support.v7.preference.PreferenceFragmentCompat.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == PreferenceManager.class) {
                preferenceManagerField = field;
                preferenceManagerField.setAccessible(true);
                break;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Context styledContext = getPreferenceManager().getContext();

            PreferenceManager fixedManager = new PreferenceManagerFix(styledContext);
            fixedManager.setOnNavigateToScreenListener(this);

            preferenceManagerField.set(PreferenceFragmentCompat.this, fixedManager);

            Bundle args = this.getArguments();
            String rootKey;
            if (args != null) {
                rootKey = this.getArguments().getString("android.support.v7.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
            } else {
                rootKey = null;
            }

            this.onCreatePreferencesFix(savedInstanceState, rootKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bundle
     * @param s
     * @deprecated Use {@link #onCreatePreferencesFix(Bundle, String)} instead.
     */
    @Override
    @Deprecated
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public abstract void onCreatePreferencesFix(Bundle bundle, String s);

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            Object f = null;

            if (preference instanceof EditTextPreferenceFix) {
                f = EditTextPreferenceDialogFragmentCompatFix.newInstance(preference.getKey());
            } else if (preference instanceof EditTextPreference) {
                f = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else {
                super.onDisplayPreferenceDialog(preference);
            }

            if (f != null) {
                ((DialogFragment) f).setTargetFragment(this, 0);
                ((DialogFragment) f).show(this.getFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        }
    }
}
