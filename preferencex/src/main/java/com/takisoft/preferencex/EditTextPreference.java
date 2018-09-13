package com.takisoft.preferencex;

import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextPreference extends androidx.preference.EditTextPreference {
    @LayoutRes
    private int editLayout;
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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextPreference, defStyleAttr, 0);
        editLayout = a.getResourceId(R.styleable.EditTextPreference_pref_editLayout, 0);
        a.recycle();

        if (editLayout == 0) {
            editText = new AppCompatEditText(context, attrs);
            editText.setId(android.R.id.edit);
        }
    }

    @LayoutRes
    public int getEditLayout() {
        return editLayout;
    }

    @Nullable
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
