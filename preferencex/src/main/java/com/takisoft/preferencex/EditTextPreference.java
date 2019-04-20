package com.takisoft.preferencex;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;

public class EditTextPreference extends androidx.preference.EditTextPreference {
    @Nullable
    private OnBindEditTextListener onBindEditTextListener;

    private SparseArrayCompat<TypedValue> editTextAttributes = new SparseArrayCompat<>();

    public EditTextPreference(Context context) {
        this(context, null);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextPreference(Context context, final AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processAttrs(attrs);

        super.setOnBindEditTextListener(new OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                int n = editTextAttributes.size();
                for (int i = 0; i < n; i++) {
                    int attr = editTextAttributes.keyAt(i);
                    TypedValue value = editTextAttributes.valueAt(i);

                    int data = value.data;
                    // TODO resolve resources?

                    switch (attr) {
                        case android.R.attr.inputType:
                            editText.setInputType(data);
                            break;
                        case android.R.attr.textAllCaps:
                            editText.setAllCaps(data == 1);
                            break;
                        case android.R.attr.lines:
                            editText.setLines(data);
                            break;
                        case android.R.attr.minLines:
                            editText.setMinLines(data);
                            break;
                        case android.R.attr.maxLines:
                            editText.setMaxLines(data);
                            break;
                        case android.R.attr.ems:
                            editText.setEms(data);
                            break;
                        case android.R.attr.minEms:
                            editText.setMinEms(data);
                            break;
                        case android.R.attr.maxEms:
                            editText.setMaxEms(data);
                            break;
                    }
                }

                if (onBindEditTextListener != null) {
                    onBindEditTextListener.onBindEditText(editText);
                }
            }
        });
    }

    private void processAttrs(AttributeSet attributeSet) {
        Log.d("LayoutInflater", "attributeSet: " + attributeSet);
        if (attributeSet == null) {
            return;
        }

        int n = attributeSet.getAttributeCount();
        Log.d("LayoutInflater", "attribute count: " + n);

        for (int i = 0; i < n; i++) {
            int nameRes = attributeSet.getAttributeNameResource(i);
            String name = attributeSet.getAttributeName(i);
            int resId = attributeSet.getAttributeResourceValue(i, 0);
            Log.d("LayoutInflater", "name: " + name + ", nameRes: " + nameRes + ", val: " + attributeSet.getAttributeValue(i));

            TypedValue value = null;

            switch (nameRes) {
                case android.R.attr.inputType:
                    value = new TypedValue();
                    value.resourceId = resId;
                    value.data = attributeSet.getAttributeIntValue(i, InputType.TYPE_CLASS_TEXT);
                    value.type = TypedValue.TYPE_INT_HEX;
                    break;
                case android.R.attr.minEms:
                case android.R.attr.maxEms:
                case android.R.attr.ems:
                case android.R.attr.minLines:
                case android.R.attr.maxLines:
                case android.R.attr.lines:
                    value = new TypedValue();
                    value.resourceId = resId;
                    value.data = attributeSet.getAttributeIntValue(i, -1);
                    value.type = TypedValue.TYPE_INT_DEC;
                    break;
                case android.R.attr.textAllCaps:
                    value = new TypedValue();
                    value.resourceId = resId;
                    value.data = attributeSet.getAttributeBooleanValue(i, false) ? 1 : 0;
                    value.type = TypedValue.TYPE_INT_BOOLEAN;
                    break;
            }

            if (value != null) {
                editTextAttributes.put(nameRes, value);
            }
        }
    }

    /**
     * Returns the {@link OnBindEditTextListener} used to configure the {@link EditText}
     * displayed in the corresponding dialog view for this preference.
     * <p>
     * NOTE that this will return the internal {@link OnBindEditTextListener} instead of the one set
     * via {@link #setOnBindEditTextListener(OnBindEditTextListener)}.
     *
     * @return The {@link OnBindEditTextListener} set for this preference, or {@code null} if
     * there is no OnBindEditTextListener set
     * @see OnBindEditTextListener
     */
    @Nullable
    @Override
    public OnBindEditTextListener getOnBindEditTextListener() {
        return super.getOnBindEditTextListener();
    }

    @Override
    public void setOnBindEditTextListener(@Nullable OnBindEditTextListener onBindEditTextListener) {
        this.onBindEditTextListener = onBindEditTextListener;
    }

    @Deprecated
    public EditText getEditText() {
        throw new UnsupportedOperationException("Use OnBindEditTextListener to modify the EditText");
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
