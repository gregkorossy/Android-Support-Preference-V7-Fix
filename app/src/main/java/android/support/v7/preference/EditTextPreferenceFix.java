package android.support.v7.preference;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextPreferenceFix extends EditTextPreference {
    private EditText editText;

    public EditTextPreferenceFix(Context context) {
        this(context, null);
    }

    public EditTextPreferenceFix(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.preference.R.attr.editTextPreferenceStyle);
    }

    public EditTextPreferenceFix(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextPreferenceFix(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        editText = new AppCompatEditText(context, attrs);
        editText.setId(android.R.id.edit);
    }

    public EditText getEditText() {
        return editText;
    }
}
