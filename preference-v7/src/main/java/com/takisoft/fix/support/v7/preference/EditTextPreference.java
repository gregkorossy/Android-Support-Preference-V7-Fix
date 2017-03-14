package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextPreference extends android.support.v7.preference.EditTextPreference {
    private EditText editText;

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
}
