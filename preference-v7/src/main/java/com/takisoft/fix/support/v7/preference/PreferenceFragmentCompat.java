package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceManagerFix;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;

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

    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen);
    }

    /**
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the PreferenceScreen with this key.
     * @deprecated Use {@link #onCreatePreferencesFix(Bundle, String)} instead.
     */
    @Override
    @Deprecated
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {

    }

    /**
     * Called during onCreate(Bundle) to supply the preferences for this fragment. Subclasses are expected to call setPreferenceScreen(PreferenceScreen) either directly or via helper methods such as addPreferencesFromResource(int).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the PreferenceScreen with this key.
     */
    public abstract void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey);

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            Object f = null;

            if (preference instanceof EditTextPreference) {
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
