package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerPreference extends DialogPreference {
    private boolean is24HourView;
    private boolean useSystemFormat;
    private int hourOfDay;
    private int minute;

    private CharSequence mSummary;

    static {
        PreferenceFragmentCompat.addDialogPreference(TimePickerPreference.class, TimePickerPreferenceDialogFragmentCompat.class);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, com.takisoft.fix.support.v7.preference.extras.R.styleable.TimePickerPreference, defStyleAttr, 0);
        is24HourView = a.getBoolean(com.takisoft.fix.support.v7.preference.extras.R.styleable.TimePickerPreference_is24HourView, android.text.format.DateFormat.is24HourFormat(context));
        useSystemFormat = a.getBoolean(com.takisoft.fix.support.v7.preference.extras.R.styleable.TimePickerPreference_useSystemFormat, false);
        hourOfDay = a.getInt(com.takisoft.fix.support.v7.preference.extras.R.styleable.TimePickerPreference_hour, 0);
        minute = a.getInt(com.takisoft.fix.support.v7.preference.extras.R.styleable.TimePickerPreference_minute, 0);
        a.recycle();

        mSummary = super.getSummary();
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public TimePickerPreference(Context context) {
        this(context, null);
    }

    public boolean is24HourView() {
        return is24HourView;
    }

    public void set24HourView(boolean is24HourView) {
        this.is24HourView = is24HourView;
    }

    public boolean isUseSystemFormat() {
        return useSystemFormat;
    }

    public void setUseSystemFormat(boolean useSystemFormat) {
        this.useSystemFormat = useSystemFormat;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setTime(int hourOfDay, int minute) {
        setInternalTime(hourOfDay * 60 + minute, false);
    }

    private void setInternalTime(int totalMinutes, boolean force) {
        int oldUri = getPersistedInt(this.hourOfDay * 60 + this.minute);

        final boolean changed = oldUri != totalMinutes;

        if (changed || force) {
            hourOfDay = totalMinutes / 60;
            minute = totalMinutes - (hourOfDay * 60);

            persistInt(totalMinutes);

            notifyChanged();
        }
    }

    @Override
    public CharSequence getSummary() {
        if (mSummary == null) {
            return super.getSummary();
        } else {
            DateFormat simpleDateFormat;

            if (useSystemFormat) {
                simpleDateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
            } else {
                simpleDateFormat = new SimpleDateFormat(is24HourView ? "HH:mm" : "h:mm a", Locale.getDefault());
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            return String.format(mSummary.toString(), simpleDateFormat.format(cal.getTime()));
        }
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        if (summary == null && mSummary != null) {
            mSummary = null;
        } else if (summary != null && !summary.equals(mSummary)) {
            mSummary = summary.toString();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValueObj) {
        final Integer defaultValue = (Integer) defaultValueObj;
        setInternalTime(restoreValue ? getPersistedInt(hourOfDay * 60 + minute) : (defaultValue == null ? 0 : defaultValue), true);
        //setInternalRingtone(restoreValue ? onRestoreRingtone() : (!TextUtils.isEmpty(defaultValue) ? Uri.parse(defaultValue) : null), true);
    }
}
