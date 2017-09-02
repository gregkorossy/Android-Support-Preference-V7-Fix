package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class ColorPickerPreference extends DialogPreference {

    static {
        PreferenceFragmentCompat.addDialogPreference(ColorPickerPreference.class, ColorPickerPreferenceDialogFragmentCompat.class);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public ColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public ColorPickerPreference(Context context) {
        this(context, null);
    }

    public int getColor() {
        return 0;
    }

    public void setColor(int color) {

    }
}
