package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.datetimepicker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A {@link Preference} that displays a time picker as a dialog.
 * <p>
 * This preference will store an integer into the SharedPreferences. This integer will be calculated
 * from the picked time using the following formula: {@code hourOfDay * 60 + minute}.
 */
public class TimePickerPreference extends DialogPreference {
    static {
        PreferenceFragmentCompat.addDialogPreference(TimePickerPreference.class, TimePickerPreferenceDialogFragmentCompat.class);
    }

    private boolean is24HourView;
    private boolean useSystemFormat;
    private int hourOfDay;
    private int minute;
    private CharSequence mSummary;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreference, defStyleAttr, 0);
        is24HourView = a.getBoolean(R.styleable.TimePickerPreference_is24HourView, android.text.format.DateFormat.is24HourFormat(context));
        useSystemFormat = a.getBoolean(R.styleable.TimePickerPreference_useSystemFormat, false);
        hourOfDay = a.getInt(R.styleable.TimePickerPreference_hour, 0);
        minute = a.getInt(R.styleable.TimePickerPreference_minute, 0);
        a.recycle();

        mSummary = super.getSummary();
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public TimePickerPreference(Context context) {
        this(context, null);
    }

    /**
     * Returns whether to use the 24-hour clock in the picker. The default value is set by the
     * system locale.
     *
     * @return Whether to use the 24-hour clock in the picker.
     */
    public boolean is24HourView() {
        return is24HourView;
    }

    /**
     * Sets whether to use the 24-hour clock in the picker. The default value is set by the
     * system locale.
     *
     * @param is24HourView Whether to use the 24-hour clock in the picker.
     */
    public void set24HourView(boolean is24HourView) {
        this.is24HourView = is24HourView;
    }

    /**
     * Returns whether to use the system format in the summary. The default behavior is to use the
     * value provided by {@link #is24HourView()}.
     *
     * @return Whether to use the system format in the summary.
     */
    public boolean isUseSystemFormat() {
        return useSystemFormat;
    }

    /**
     * Sets whether to use the system format in the summary. The default behavior is to use the
     * value provided by {@link #is24HourView()}.
     *
     * @param useSystemFormat Whether to use the system format in the summary or not.
     */
    public void setUseSystemFormat(boolean useSystemFormat) {
        this.useSystemFormat = useSystemFormat;
    }

    /**
     * Returns the hour of the day (a.k.a. 24-hour clock version). The range is 0-23.
     *
     * @return The hour of the day (a.k.a. 24-hour clock version). The range is 0-23.
     */
    @IntRange(from = 0, to = 23)
    public int getHourOfDay() {
        return hourOfDay;
    }

    /**
     * Returns the minute of the hour. The range is 0-59.
     *
     * @return The minute of the hour. The range is 0-59.
     */
    @IntRange(from = 0, to = 59)
    public int getMinute() {
        return minute;
    }

    /**
     * Sets the picked time of the preference.
     *
     * @param hourOfDay The hour of the day (a.k.a. 24-hour clock version). The valid range is 0-23.
     * @param minute    The minute of the hour. The valid range is 0-59.
     */
    public void setTime(@IntRange(from = 0, to = 23) int hourOfDay, @IntRange(from = 0, to = 59) int minute) {
        setInternalTime(hourOfDay * 60 + minute, false);
    }

    private void setInternalTime(int totalMinutes, boolean force) {
        int oldTime = getPersistedInt(this.hourOfDay * 60 + this.minute);

        final boolean changed = oldTime != totalMinutes;

        if (changed || force) {
            hourOfDay = totalMinutes / 60;
            minute = totalMinutes - (hourOfDay * 60);

            persistInt(totalMinutes);

            notifyChanged();
        }
    }

    /**
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted
     * time will be substituted in its place.
     *
     * @return The summary with appropriate string substitution.
     */
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

    /**
     * Sets the summary for this Preference with a CharSequence.
     * If the summary has a
     * {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted
     * time will be substituted in its place when it's retrieved.
     *
     * @param summary The summary for the preference.
     */
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
    }
}
