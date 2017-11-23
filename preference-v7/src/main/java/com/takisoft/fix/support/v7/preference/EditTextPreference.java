package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextPreference extends android.support.v7.preference.EditTextPreference {
    private EditText editText;

    private CharSequence summaryHasText;
    private CharSequence summary;

    private CharSequence passwordSubstitute;

    public EditTextPreference(Context context) {
        this(context, null);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        editText = new AppCompatEditText(context, attrs);
        editText.setId(android.R.id.edit);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextPreference, defStyleAttr, 0);
        summaryHasText = a.getText(R.styleable.EditTextPreference_pref_summaryHasText);

        passwordSubstitute = a.getText(R.styleable.EditTextPreference_pref_summaryPasswordSubstitute);
        if (passwordSubstitute == null) {
            passwordSubstitute = "*****";
        }

        a.recycle();

        summary = super.getSummary();
    }

    public EditText getEditText() {
        return editText;
    }

    @Override
    public void setText(String text) {
        String oldText = getText();
        super.setText(text);
        if (!TextUtils.equals(text, oldText)) {
            notifyChanged();
        }
    }

    /**
     * Returns the summary of this Preference. If no {@code pref_summaryHasText} is set, this will
     * be displayed if no value is set; otherwise the value will be used.
     *
     * @return The summary.
     */
    @Override
    public CharSequence getSummary() {
        CharSequence text = getText();
        final boolean hasText = !TextUtils.isEmpty(text);

        if (!hasText) {
            return summary;
        } else {
            int inputType = getEditText().getInputType();

            if ((inputType & InputType.TYPE_NUMBER_VARIATION_PASSWORD) == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                    (inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    (inputType & InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
                text = passwordSubstitute;
            }

            if (summaryHasText != null) {
                return String.format(summaryHasText.toString(), text);
            } else {
                return text;
            }
        }
    }

    /**
     * Sets the summary for this Preference with a CharSequence. If no {@code pref_summaryHasText}
     * is set, this will be displayed if no value is set; otherwise the value will be used.
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
     * Returns the summary for this Preference. This will be displayed if the preference
     * has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current value will be substituted in its place.
     *
     * @return The picked summary.
     */
    @Nullable
    public CharSequence getSummaryHasText() {
        return summaryHasText;
    }

    /**
     * Sets the summary for this Preference with a resource ID. This will be displayed if the
     * preference has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current value will be substituted in its place.
     *
     * @param resId The summary as a resource.
     * @see #setSummaryHasText(CharSequence)
     */
    public void setSummaryHasText(@StringRes int resId) {
        setSummaryHasText(getContext().getString(resId));
    }

    /**
     * Sets the summary for this Preference with a CharSequence. This will be displayed if
     * the preference has a persisted value or the default value is set. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current value will be substituted in its place.
     *
     * @param summaryHasText The summary for the preference.
     */
    public void setSummaryHasText(@Nullable CharSequence summaryHasText) {
        if (summaryHasText == null && this.summaryHasText != null) {
            this.summaryHasText = null;
        } else if (summaryHasText != null && !summaryHasText.equals(this.summaryHasText)) {
            this.summaryHasText = summaryHasText.toString();
        }

        notifyChanged();
    }

    /**
     * Returns the substitute characters to be used for displaying passwords in the summary.
     *
     * @return The substitute characters to be used for displaying passwords in the summary.
     */
    public CharSequence getPasswordSubstitute() {
        return passwordSubstitute;
    }

    /**
     * Sets the substitute characters to be used for displaying passwords in the summary.
     *
     * @param resId The substitute characters as a resource.
     * @see #setPasswordSubstitute(CharSequence)
     */
    public void setPasswordSubstitute(@StringRes int resId) {
        setPasswordSubstitute(getContext().getString(resId));
    }

    /**
     * Sets the substitute characters to be used for displaying passwords in the summary.
     *
     * @param passwordSubstitute The substitute characters to be used for displaying passwords in
     *                           the summary.
     */
    public void setPasswordSubstitute(CharSequence passwordSubstitute) {
        this.passwordSubstitute = passwordSubstitute;
    }
}
