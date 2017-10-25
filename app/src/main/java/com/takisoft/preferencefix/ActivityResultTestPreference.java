package com.takisoft.preferencefix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.PreferenceActivityResultListener;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class ActivityResultTestPreference extends Preference implements PreferenceActivityResultListener {
    public static final int RC_GET_CONTENT = 3462;

    public ActivityResultTestPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ActivityResultTestPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ActivityResultTestPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActivityResultTestPreference(Context context) {
        super(context);
    }

    @Override
    public void onPreferenceClick(@NonNull PreferenceFragmentCompat fragment, @NonNull Preference preference) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        fragment.startActivityForResult(intent, RC_GET_CONTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_GET_CONTENT && resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(getContext(), data.getDataString(), Toast.LENGTH_LONG).show();
        }
    }
}
