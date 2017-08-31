package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.datetimepicker.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A {@link Preference} that displays a time picker as a dialog.
 * <p>
 * This preference will save the picked time as a string into the SharedPreferences.
 * This string uses the 24-hour clock formatted using {@link #FORMAT}.
 *
 * @see #PATTERN
 * @see #FORMAT
 */
@SuppressWarnings("WeakerAccess")
public class TimePickerPreference extends DialogPreference {
    /**
     * The pattern that is used for parsing the default value.
     */
    public static final String PATTERN = "HH:mm";

    /**
     * The date format that can be used to convert the saved value to {@link Date} objects.
     */
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN, Locale.US);

    static {
        PreferenceFragmentCompat.addDialogPreference(TimePickerPreference.class, TimePickerPreferenceDialogFragmentCompat.class);
    }

    public static final int FORMAT_AUTO = 0;
    public static final int FORMAT_12H = 1;
    public static final int FORMAT_24H = 2;

    @IntDef({FORMAT_AUTO, FORMAT_12H, FORMAT_24H})
    @interface HourFormat {
    }

    private Date time;
    private Date pickerTime;

    private int hourFormat;
    private String summaryPattern;
    private CharSequence summaryNotPicked;
    private CharSequence summary;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreference, defStyleAttr, 0);
        hourFormat = a.getInt(R.styleable.TimePickerPreference_hourFormat, FORMAT_AUTO);
        summaryPattern = a.getString(R.styleable.TimePickerPreference_summaryTimePattern);
        summaryNotPicked = a.getText(R.styleable.TimePickerPreference_summaryNoTime);

        String pickerTime = a.getString(R.styleable.TimePickerPreference_pickerTime);

        if (!TextUtils.isEmpty(pickerTime)) {
            try {
                this.pickerTime = FORMAT.parse(pickerTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        a.recycle();

        summary = super.getSummary();
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
     * Returns the hour format of the picker. The possible values are {@link #FORMAT_AUTO},
     * {@link #FORMAT_12H}, and {@link #FORMAT_24H}.
     * <p>
     * The default is to use the system locale.
     *
     * @return The hour format of the picker.
     * @see #FORMAT_AUTO
     * @see #FORMAT_12H
     * @see #FORMAT_24H
     */
    @HourFormat
    public int getHourFormat() {
        return hourFormat;
    }

    boolean is24HourView() {
        return (hourFormat == FORMAT_AUTO) ? android.text.format.DateFormat.is24HourFormat(getContext()) : hourFormat == FORMAT_24H;
    }

    /**
     * Sets the hour format of the picker.
     * <p>
     * The default is to use the system locale.
     *
     * @param hourFormat The hour format to be used in the picker.
     */
    public void setHourFormat(@HourFormat int hourFormat) {
        this.hourFormat = hourFormat;
    }

    /**
     * Returns the hour of the day (a.k.a. 24-hour clock version). The range is 0-23, or -1 if the
     * time is not set.
     *
     * @return The hour of the day (a.k.a. 24-hour clock version). The range is 0-23, or -1 if the
     * time is not set.
     */
    @IntRange(from = -1, to = 23)
    public int getHourOfDay() {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            return cal.get(Calendar.HOUR_OF_DAY);
        }

        return -1;
    }

    /**
     * Returns the minute of the hour. The range is 0-59, or -1 if the
     * time is not set.
     *
     * @return The minute of the hour. The range is 0-59, or -1 if the
     * time is not set.
     */
    @IntRange(from = -1, to = 59)
    public int getMinute() {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            return cal.get(Calendar.MINUTE);
        }

        return -1;
    }

    /**
     * Returns the selected time.
     *
     * @return The selected time.
     */
    @Nullable
    public Date getTime() {
        return time;
    }

    /**
     * Sets and persists the selected time.
     *
     * @param time The selected time.
     */
    public void setTime(@Nullable Date time) {
        this.time = time;
    }

    /**
     * Sets and persists the picked time of the preference.
     *
     * @param hourOfDay The hour of the day (a.k.a. 24-hour clock version). The valid range is 0-23.
     * @param minute    The minute of the hour. The valid range is 0-59.
     */
    public void setTime(@IntRange(from = 0, to = 23) int hourOfDay, @IntRange(from = 0, to = 59) int minute) {
        setInternalTime(String.format(Locale.US, "%02d:%02d", hourOfDay, minute), false);
    }

    /**
     * Returns the default picker time that should be used if no persisted value exists and no
     * default time is set.
     *
     * @return The default picker time that should be used if no persisted value exists and no
     * default time is set.
     */
    @Nullable
    public Date getPickerTime() {
        return pickerTime;
    }

    /**
     * Sets the default picker time that should be used if no persisted value exists and no default
     * time is set.
     *
     * @param pickerTime The default picker time that should be used if no persisted value exists
     *                   and no default time is set.
     */
    public void setPickerTime(@Nullable Date pickerTime) {
        this.pickerTime = pickerTime;
    }

    private void setInternalTime(String time, boolean force) {
        String oldTime = getPersistedString(null);

        final boolean changed = (oldTime != null && !oldTime.equals(time)) || (time != null && !time.equals(oldTime));

        if (changed || force) {
            if (!TextUtils.isEmpty(time)) {
                try {
                    this.time = FORMAT.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                    this.time = null;
                }
            }

            persistString(time == null ? "" : time);

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
        if (summary == null) {
            return super.getSummary();
        } else {
            if (time == null) {
                return summaryNotPicked;
            } else {
                DateFormat simpleDateFormat;

                if (summaryPattern == null) {
                    simpleDateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
                } else {
                    simpleDateFormat = new SimpleDateFormat(summaryPattern, Locale.getDefault());
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(time);

                return String.format(summary.toString(), simpleDateFormat.format(cal.getTime()));
            }
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
        if (summary == null && this.summary != null) {
            this.summary = null;
        } else if (summary != null && !summary.equals(this.summary)) {
            this.summary = summary.toString();
        }
    }

    /**
     * Returns the date pattern that will be used in the summary to format the selected date. If not
     * set, the default format will be used based on the current locale. It can contain the usual
     * formatting characters. See {@link SimpleDateFormat} for more details.
     *
     * @return The date pattern that will be used in the summary to format the selected date.
     */
    public String getSummaryPattern() {
        return summaryPattern;
    }

    /**
     * Sets the date pattern that will be used in the summary to format the selected date. If not
     * set, the default format will be used based on the current locale. It can contain the usual
     * formatting characters. See {@link SimpleDateFormat} for more details.
     *
     * @param summaryPattern The date pattern that will be used in the summary to format the
     *                       selected date.
     */
    public void setSummaryPattern(String summaryPattern) {
        this.summaryPattern = summaryPattern;
    }

    /**
     * Returns the not-picked summary for this Preference. This will be displayed if the preference
     * has no persisted value yet and the default value is not set.
     *
     * @return The not-picked summary.
     */
    @Nullable
    public CharSequence getSummaryNotPicked() {
        return summaryNotPicked;
    }

    /**
     * Sets the not-picked summary for this Preference with a resource ID. This will be displayed if
     * the preference has no persisted value yet and the default value is not set.
     *
     * @param resId The summary as a resource.
     * @see #setSummaryNotPicked(CharSequence)
     */
    public void setSummaryNotPicked(@StringRes int resId) {
        setSummaryNotPicked(getContext().getString(resId));
    }

    /**
     * Sets the not-picked summary for this Preference with a CharSequence. This will be displayed
     * if the preference has no persisted value yet and the default value is not set.
     * If the summary has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted
     * date will be substituted in its place when it's retrieved.
     *
     * @param summaryNotPicked The summary for the preference.
     */
    public void setSummaryNotPicked(CharSequence summaryNotPicked) {
        if (summaryNotPicked == null && this.summaryNotPicked != null) {
            this.summaryNotPicked = null;
        } else if (summaryNotPicked != null && !summaryNotPicked.equals(this.summaryNotPicked)) {
            this.summaryNotPicked = summaryNotPicked.toString();
        }

        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValueObj) {
        final String defaultValue = (String) defaultValueObj;
        setInternalTime(restoreValue ? getPersistedString(null) : (!TextUtils.isEmpty(defaultValue) ? defaultValue : null), true);
    }
}
