package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.os.Build;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.PreferenceViewHolderFix;
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

    SwitchCompat switchCompat;

    /*@Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        switchCompat = (SwitchCompat) holder.findViewById(R.id.switchWidget);
        Log.d("SwitchCompat", "" + switchCompat);
    }*/

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(new PreferenceViewHolderFix(holder));
        //this.syncSwitchView(switchView);
        switchCompat = (SwitchCompat) holder.findViewById(R.id.switchWidget);
        if (switchCompat != null && switchCompat.isChecked() != this.mChecked) {
            switchCompat.setChecked(this.mChecked);
        }
        //this.syncSummaryView(holder);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        /*boolean changed = this.mChecked != checked;
        if (changed || !this.mCheckedSet) {
            this.mChecked = checked;
            this.mCheckedSet = true;
            this.persistBoolean(checked);
            if (changed) {
                this.notifyDependencyChange(this.shouldDisableDependents());
                this.notifyChanged();
            }
        }*/

        /*Log.d("SwitchPreferenceCompat", "setChecked: " + checked);
        Log.d("SwitchPreferenceCompat", "windowToken: " + switchCompat.getWindowToken());
        Log.d("SwitchPreferenceCompat", "isLaidOut: " + ViewCompat.isLaidOut(switchCompat));
        Log.d("SwitchPreferenceCompat", "isShown: " + switchCompat.isShown());*/

        if (switchCompat != null && switchCompat.isChecked() != checked) {
            switchCompat.setChecked(checked);
        }



        /*if (switchCompat.getWindowToken() != null && ViewCompat.isLaidOut(switchCompat) && switchCompat.isShown()) {
            try {
                Method animateThumbToCheckedState = SwitchCompat.class.getDeclaredMethod("animateThumbToCheckedState", Boolean.TYPE);
                animateThumbToCheckedState.setAccessible(true);
                animateThumbToCheckedState.invoke(switchCompat, checked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}
