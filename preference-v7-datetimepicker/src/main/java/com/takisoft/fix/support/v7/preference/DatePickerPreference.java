package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.datetimepicker.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class DatePickerPreference extends DialogPreference {
    /**
     * The pattern that is used for parsing the default value.
     */
    public static final String PATTERN = "MM/dd/yyyy";

    /**
     * The date format that can be used to convert the saved value to {@link Date} objects.
     */
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN, Locale.US);

    static {
        PreferenceFragmentCompat.addDialogPreference(DatePickerPreference.class, DatePickerPreferenceDialogFragmentCompat.class);
    }

    private String summaryPattern;
    private CharSequence summaryNotPicked;
    private CharSequence summary;

    private Date date;
    private Date pickerDate, minDate, maxDate;

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePickerPreference, defStyleAttr, 0);

        String pickerDate = a.getString(R.styleable.DatePickerPreference_pickerDate);
        String minDate = a.getString(R.styleable.DatePickerPreference_minDate);
        String maxDate = a.getString(R.styleable.DatePickerPreference_maxDate);

        if (!TextUtils.isEmpty(pickerDate)) {
            try {
                this.pickerDate = FORMAT.parse(pickerDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(minDate)) {
            try {
                this.minDate = FORMAT.parse(minDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(maxDate)) {
            try {
                this.maxDate = FORMAT.parse(maxDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        summaryPattern = a.getString(R.styleable.DatePickerPreference_summaryDatePattern);
        summaryNotPicked = a.getText(R.styleable.DatePickerPreference_summaryNoDate);
        a.recycle();

        summary = super.getSummary();
    }

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public DatePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public DatePickerPreference(Context context) {
        this(context, null);
    }

    /**
     * Returns the selected date.
     *
     * @return The selected date.
     */
    @Nullable
    public Date getDate() {
        return date;
    }

    /**
     * Sets and persists the selected date.
     *
     * @param date The selected date.
     */
    public void setDate(@Nullable Date date) {
        if (date == null) {
            setInternalDate(null, false);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            setInternalDate(FORMAT.format(cal.getTime()), false);
        }
    }

    /**
     * Sets and persists the selected date.
     *
     * @param year  the year
     * @param month the month (starts from 0; see {@link Calendar#MONTH} for details)
     * @param day   the day
     */
    public void setDate(int year, @IntRange(from = 0) int month, @IntRange(from = 1, to = 31) int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, day);

        setInternalDate(FORMAT.format(cal.getTime()), false);
    }

    /**
     * Returns the default picker date that should be used if no persisted value exists and no
     * default date is set.
     *
     * @return The default picker date that should be used if no persisted value exists and no
     * default date is set.
     */
    @Nullable
    public Date getPickerDate() {
        return pickerDate;
    }

    /**
     * Sets the default picker date that should be used if no persisted value exists and no default
     * date is set.
     *
     * @param pickerDate The default picker date that should be used if no persisted value exists
     *                   and no default date is set.
     */
    public void setPickerDate(@Nullable Date pickerDate) {
        this.pickerDate = pickerDate;
    }

    /**
     * Returns the minimal date shown by this picker.
     *
     * @return The minimal date shown by this picker.
     */
    @Nullable
    public Date getMinDate() {
        return minDate;
    }

    /**
     * Sets the minimal date shown by this picker.
     *
     * @param minDate The minimal date shown by this picker.
     */
    public void setMinDate(@Nullable Date minDate) {
        this.minDate = minDate;
    }

    /**
     * Returns the maximal date shown by this picker.
     *
     * @return The maximal date shown by this picker.
     */
    @Nullable
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * Sets the maximal date shown by this picker.
     *
     * @param maxDate The maximal date shown by this picker.
     */
    public void setMaxDate(@Nullable Date maxDate) {
        this.maxDate = maxDate;
    }

    private void setInternalDate(@Nullable String date, boolean force) {
        String oldDate = getPersistedString(null);

        final boolean changed = (oldDate != null && !oldDate.equals(date)) || (date != null && !date.equals(oldDate));

        if (changed || force) {
            if (!TextUtils.isEmpty(date)) {
                try {
                    this.date = FORMAT.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    this.date = null;
                    date = null;
                }
            } else {
                this.date = null;
            }

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
            if (date == null) {
                return summaryNotPicked;
            } else {
                DateFormat simpleDateFormat;

                if (summaryPattern == null) {
                    simpleDateFormat = android.text.format.DateFormat.getLongDateFormat(getContext());
                } else {
                    simpleDateFormat = new SimpleDateFormat(summaryPattern, Locale.getDefault());
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

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
    public void setSummaryNotPicked(@Nullable CharSequence summaryNotPicked) {
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
