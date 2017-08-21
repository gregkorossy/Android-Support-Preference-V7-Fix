package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerPreference extends DialogPreference {
    private static final String PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN, Locale.getDefault());

    private String pickedDate;

    private int year;
    private int month;
    private int day;
    private String summaryPattern;

    private CharSequence summaryNotPicked;
    private CharSequence summary;

    static {
        PreferenceFragmentCompat.addDialogPreference(DatePickerPreference.class, DatePickerPreferenceDialogFragmentCompat.class);
    }

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Calendar calendar = Calendar.getInstance();

        TypedArray a = context.obtainStyledAttributes(attrs, com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference, defStyleAttr, 0);
        year = a.getInt(com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference_year, calendar.get(Calendar.YEAR)); // FIXME not good like this because we don't display the values now
        month = a.getInt(com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference_month, calendar.get(Calendar.MONTH)); // FIXME
        day = a.getInt(com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference_day, calendar.get(Calendar.DATE)); // FIXME
        summaryPattern = a.getString(com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference_summaryPattern);
        summaryNotPicked = a.getText(com.takisoft.fix.support.v7.preference.extras.R.styleable.DatePickerPreference_summaryNotPicked);
        a.recycle();

        summary = super.getSummary();
    }

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DatePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public DatePickerPreference(Context context) {
        this(context, null);
    }

    public int getYear() {
        return year;
    }

    @IntRange(from = 0)
    public int getMonth() {
        return month;
    }

    @IntRange(from = 1, to = 31)
    public int getDay() {
        return day;
    }

    public void setDate(int year, @IntRange(from = 0) int month, @IntRange(from = 1, to = 31) int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, day);

        setInternalDate(FORMAT.format(cal.getTime()), false);
    }

    private void setInternalDate(String date, boolean force) {
        String oldDate = getPersistedString(null);

        final boolean changed = (oldDate != null && !oldDate.equals(date)) || (date != null && !date.equals(oldDate));

        if (changed || force) {
            if (!TextUtils.isEmpty(date)) {
                try {
                    Date parsed = FORMAT.parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsed);

                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            pickedDate = date;
            persistString(date == null ? "" : date);

            notifyChanged();
        }
    }

    /**
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted
     * date will be substituted in its place.
     *
     * @return The summary with appropriate string substitution.
     */
    @Override
    public CharSequence getSummary() {
        if (summary == null) {
            return super.getSummary();
        } else {
            if (TextUtils.isEmpty(pickedDate)) {
                return summaryNotPicked;
            } else {
                DateFormat simpleDateFormat;

                if (summaryPattern == null) {
                    simpleDateFormat = android.text.format.DateFormat.getLongDateFormat(getContext());
                } else {
                    simpleDateFormat = new SimpleDateFormat(summaryPattern, Locale.getDefault());
                }

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);

                return String.format(summary.toString(), simpleDateFormat.format(cal.getTime()));
            }
        }
    }

    /**
     * Sets the summary for this Preference with a CharSequence.
     * If the summary has a
     * {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted
     * date will be substituted in its place when it's retrieved.
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
        setInternalDate(restoreValue ? getPersistedString(null) : (!TextUtils.isEmpty(defaultValue) ? defaultValue : null), true);
    }
}
