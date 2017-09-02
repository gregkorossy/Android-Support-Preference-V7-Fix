package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.colorpicker.ColorPickerDialog;
import com.takisoft.fix.support.v7.preference.colorpicker.R;

public class ColorPickerPreference extends DialogPreference {

    static {
        PreferenceFragmentCompat.addDialogPreference(ColorPickerPreference.class, ColorPickerPreferenceDialogFragmentCompat.class);
    }

    private int[] colors;
    private CharSequence[] colorDescriptions;
    private int color;
    private int columns;
    private int size;

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference, defStyleAttr, 0);

        int colorsId = a.getResourceId(R.styleable.ColorPickerPreference_colors, 0); // TODO default 16-size array of colors

        if (colorsId != 0) {
            colors = context.getResources().getIntArray(colorsId);
        }

        colorDescriptions = a.getTextArray(R.styleable.ColorPickerPreference_colorDescriptions);
        color = a.getColor(R.styleable.ColorPickerPreference_currentColor, 0);
        columns = a.getInt(R.styleable.ColorPickerPreference_columns, 3);
        size = a.getInt(R.styleable.ColorPickerPreference_size, 2);

        a.recycle();
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
        return color;
    }

    public void setColor(int color) {
        setInternalColor(color, false);
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public CharSequence[] getColorDescriptions() {
        return colorDescriptions;
    }

    public void setColorDescriptions(CharSequence[] colorDescriptions) {
        this.colorDescriptions = colorDescriptions;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    @ColorPickerDialog.Size
    public int getSize() {
        return size;
    }

    public void setSize(@ColorPickerDialog.Size int size) {
        this.size = size;
    }

    private void setInternalColor(int color, boolean force) {
        int oldColor = getPersistedInt(0);

        boolean changed = oldColor != color;

        if (changed || force) {
            this.color = color;

            persistInt(color);

            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValueObj) {
        final String defaultValue = (String) defaultValueObj;
        setInternalColor(restoreValue ? getPersistedInt(0) : (!TextUtils.isEmpty(defaultValue) ? Color.parseColor(defaultValue) : 0), true);
    }
}
