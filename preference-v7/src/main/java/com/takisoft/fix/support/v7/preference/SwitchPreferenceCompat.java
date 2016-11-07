package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.SwitchPreferenceCompatViewHolder;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

public class SwitchPreferenceCompat extends android.support.v7.preference.SwitchPreferenceCompat {

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchPreferenceCompat(Context context) {
        super(context);
    }

    protected SwitchCompat switchCompat;

    @Override
    protected void onClick() {
        //super.onClick();

        final boolean newValue = !isChecked();
        if (callChangeListener(newValue)) {
            setCheckedAnimated(newValue);
        }
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(new SwitchPreferenceCompatViewHolder(holder));

        switchCompat = (SwitchCompat) holder.findViewById(R.id.switchWidget);
        if (switchCompat != null && switchCompat.isChecked() != this.mChecked) {
            switchCompat.setChecked(this.mChecked);
        }
    }

    public void setCheckedAnimated(boolean checked) {
        super.setChecked(checked);

        if (switchCompat != null && switchCompat.isChecked() != checked) {
            switchCompat.setChecked(checked);
        }
    }
}
