package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
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
 * A {@link Preference} that displays a date picker as a dialog.
 * <p>
 * This preference will save the picked date as a string into the SharedPreferences.
 * This string uses the {@code MM/dd/yyyy} format, formatted using {@link #FORMAT}.
 *
 * @see #PATTERN
 * @see #FORMAT
 */

@SuppressWarnings("WeakerAccess,unused")
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
        PreferenceFragmentCompat.registerPreferenceFragment(DatePickerPreference.class, DatePickerPreferenceDialogFragmentCompat.class);
    }

    private String summaryPattern;
    private CharSequence summaryHasDate;
    private CharSequence summary;

    private Date date;
    private Date pickerDate, minDate, maxDate;

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePickerPreference, defStyleAttr, 0);

        String pickerDate = a.getString(R.styleable.DatePickerPreference_pref_pickerDate);
        String minDate = a.getString(R.styleable.DatePickerPreference_pref_minDate);
        String maxDate = a.getString(R.styleable.DatePickerPreference_pref_maxDate);

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

        summaryPattern = a.getString(R.styleable.DatePickerPreference_pref_summaryDatePattern);
        summaryHasDate = a.getText(R.styleable.DatePickerPreference_pref_summaryHasDate);
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
     * Returns the summary of this Preference. If no {@code pref_summaryHasDate} is set, this will be
     * displayed if no date is selected; otherwise the formatted date will be used.
     *
     * @return The summary.
     */
    @Override
    public CharSequence getSummary() {
        if (date == null) {
            return summary;
        } else {
            DateFormat simpleDateFormat;

            if (summaryPattern == null) {
                simpleDateFormat = android.text.format.DateFormat.getLongDateFormat(getContext());
            } else {
                simpleDateFormat = new SimpleDateFormat(summaryPattern, Locale.getDefault());
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String formattedDate = simpleDateFormat.format(cal.getTime());
            if (summaryHasDate != null && formattedDate != null) {
                return String.format(summaryHasDate.toString(), formattedDate);
            } else if (formattedDate != null) {
                return formattedDate;
            } else {
                return summary;
            }
        }
    }

    /**
     * Sets the summary for this Preference with a CharSequence. If no {@code pref_summaryHasDate} is
     * set, this will be displayed if no date is selected; otherwise the formatted date will be
     * used.
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
     * Returns the picked summary for this Preference. This will be displayed if the preference
     * has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted date
     * will be substituted in its place.
     *
     * @return The picked summary.
     */
    @Nullable
    public CharSequence getSummaryHasDate() {
        return summaryHasDate;
    }

    /**
     * Sets the picked summary for this Preference with a resource ID. This will be displayed if the
     * preference has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted date
     * will be substituted in its place.
     *
     * @param resId The summary as a resource.
     * @see #setSummaryHasDate(CharSequence)
     */
    public void setSummaryHasDate(@StringRes int resId) {
        setSummaryHasDate(getContext().getString(resId));
    }

    /**
     * Sets the picked summary for this Preference with a CharSequence. This will be displayed if
     * the preference has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current formatted date
     * will be substituted in its place.
     *
     * @param summaryHasDate The summary for the preference.
     */
    public void setSummaryHasDate(@Nullable CharSequence summaryHasDate) {
        if (summaryHasDate == null && this.summaryHasDate != null) {
            this.summaryHasDate = null;
        } else if (summaryHasDate != null && !summaryHasDate.equals(this.summaryHasDate)) {
            this.summaryHasDate = summaryHasDate.toString();
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

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.date = getDate();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setDate(myState.date);
    }

    private static class SavedState extends BaseSavedState {
        private Date date;

        public SavedState(Parcel source) {
            super(source);
            date = (Date) source.readSerializable();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable(date);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public static class DateWrapper {
        public final int year;
        public final int month;
        public final int day;

        public DateWrapper(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
}
