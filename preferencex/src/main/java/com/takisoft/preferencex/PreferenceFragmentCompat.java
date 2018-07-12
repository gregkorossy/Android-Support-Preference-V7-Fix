package com.takisoft.preferencex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceManagerFix;

public abstract class PreferenceFragmentCompat extends androidx.preference.PreferenceFragmentCompat {
    private static final String FRAGMENT_DIALOG_TAG = "androidx.preference.PreferenceFragment.DIALOG";

    private static Field preferenceManagerField;

    static {
        Field[] fields = androidx.preference.PreferenceFragmentCompat.class.getDeclaredFields();
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
        final TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
        int theme = tv.resourceId;
        if (theme == 0) {
            // Fallback to default theme.
            theme = R.style.PreferenceThemeOverlay;
        }
        Context styledContext = new ContextThemeWrapper(getActivity(), theme);

        PreferenceManager fixedManager = new PreferenceManagerFix(styledContext);
        fixedManager.setOnNavigateToScreenListener(this);

        try {
            preferenceManagerField.set(PreferenceFragmentCompat.this, fixedManager);
        } catch (IllegalAccessException e) {
            // we don't care since if the dev uses new preferences, it will crash the app anyways
            e.printStackTrace();
        }

        Bundle args = this.getArguments();
        String rootKey;
        if (args != null) {
            rootKey = this.getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
        } else {
            rootKey = null;
        }

        this.onCreatePreferencesFix(savedInstanceState, rootKey);

    }

    /**
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           PreferenceScreen with this key.
     * @deprecated Use {@link #onCreatePreferencesFix(Bundle, String)} instead.
     */
    @Override
    @Deprecated
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    /**
     * Called during onCreate(Bundle) to supply the preferences for this fragment. Subclasses are
     * expected to call setPreferenceScreen(PreferenceScreen) either directly or via helper methods
     * such as addPreferencesFromResource(int).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           PreferenceScreen with this key.
     */
    public abstract void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey);

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            if (preference instanceof EditTextPreference) {
                displayPreferenceDialog(new EditTextPreferenceDialogFragmentCompat(), preference.getKey());
            } else if (dialogPreferences.containsKey(preference.getClass())) {
                try {
                    displayPreferenceDialog(dialogPreferences.get(preference.getClass()).newInstance(),
                            preference.getKey());
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }

    protected void displayPreferenceDialog(@NonNull Fragment fragment, @NonNull String key) {
        displayPreferenceDialog(fragment, key, null);
    }

    protected void displayPreferenceDialog(@NonNull Fragment fragment, @NonNull String key, @Nullable Bundle bundle) {
        FragmentManager fragmentManager = this.getFragmentManager();

        if (fragmentManager == null) {
            return;
        }

        Bundle b = bundle == null ? new Bundle(1) : bundle;
        b.putString("key", key);
        fragment.setArguments(b);
        fragment.setTargetFragment(this, 0);
        if (fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).show(fragmentManager, FRAGMENT_DIALOG_TAG);
        } else {
            fragmentManager
                    .beginTransaction()
                    .add(fragment, FRAGMENT_DIALOG_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean handled = super.onPreferenceTreeClick(preference);

        if (!handled && preference instanceof PreferenceActivityResultListener) {
            ((PreferenceActivityResultListener) preference).onPreferenceClick(this, preference);
        }

        return handled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(getPreferenceScreen(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Traverses a {@code PreferenceGroup} to notify all eligible preferences about the results
     * of a returning activity.
     *
     * @param group       The {@code PreferenceGroup} to traverse.
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    protected void onActivityResult(PreferenceGroup group, int requestCode, int resultCode, Intent data) {
        final int n = group.getPreferenceCount();

        for (int i = 0; i < n; i++) {
            Preference pref = group.getPreference(i);
            if (pref instanceof PreferenceActivityResultListener) {
                ((PreferenceActivityResultListener) pref).onActivityResult(requestCode, resultCode, data);
            }

            if (pref instanceof PreferenceGroup) {
                onActivityResult((PreferenceGroup) pref, requestCode, resultCode, data);
            }
        }
    }

    protected static HashMap<Class<? extends Preference>, Class<? extends Fragment>> dialogPreferences = new HashMap<>();

    /**
     * Sets a {@link Preference} to use the supplied {@link Fragment} as a dialog.
     * <p>
     * <strong>NOTE</strong>
     * If <var>prefClass</var> is not a subclass of {@link DialogPreference}, you must call
     * {@link PreferenceManager#showDialog(Preference)} to execute the dialog showing logic when the
     * user clicks on the preference.
     * <p>
     * <strong>WARNING</strong>
     * If <var>fragmentClass</var> is not a subclass of {@link DialogFragment}, the fragment will be
     * added to the fragment manager with the tag {@link #FRAGMENT_DIALOG_TAG} and using
     * {@link FragmentTransaction#commit()}. You <em>must</em> ensure that the fragment is removed from the
     * manager when it's done with its work.
     * If you want to handle how the fragment is being added to the manager, implement
     * {@link PreferenceActivityResultListener} instead and use the fragment from
     * {@link PreferenceActivityResultListener#onPreferenceClick(PreferenceFragmentCompat, Preference)}
     * to add it manually.
     *
     * @param prefClass     the {@link Preference} class to be used
     * @param fragmentClass the {@link Preference} class to be instantiated and displayed / added
     */
    public static void registerPreferenceFragment(Class<? extends Preference> prefClass, Class<? extends Fragment> fragmentClass) {
        dialogPreferences.put(prefClass, fragmentClass);
    }
}
