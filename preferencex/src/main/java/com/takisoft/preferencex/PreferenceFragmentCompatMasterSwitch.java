package com.takisoft.preferencex;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceScreen;

public abstract class PreferenceFragmentCompatMasterSwitch extends PreferenceFragmentCompat {
    private static final int[] ATTRS = {R.attr.pref_masterSwitchBackgroundOn, R.attr.pref_masterSwitchBackgroundOff};

    protected View masterView;
    protected TextView masterTitle;
    protected SwitchCompat switchCompat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.pref_masterSwitchStyle, typedValue, true);

            ContextThemeWrapper ctx = new ContextThemeWrapper(requireContext(), typedValue.resourceId != 0 ? typedValue.resourceId : R.style.PreferenceMasterSwitch);
            //ctx.getTheme().applyStyle(typedValue.resourceId != 0 ? typedValue.resourceId : R.style.PreferenceMasterSwitch, true);
            LayoutInflater inf = inflater.cloneInContext(ctx);

            masterView = inf.inflate(R.layout.preference_list_master_switch, group, false);
            masterTitle = masterView.findViewById(android.R.id.title);
            switchCompat = masterView.findViewById(androidx.preference.R.id.switchWidget);

            setMasterBackground(ctx);
            refreshMasterSwitch();

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //getPreferenceManager().getSharedPreferences().edit().putBoolean("pref_chkbox", isChecked).apply();
                    refreshDependencies();
                }
            });

            //ViewGroup viewGroup = masterView.findViewById(android.R.id.widget_frame);
            //viewGroup.addView(new SwitchCompat(view.getContext()), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            masterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState = !masterView.isSelected();
                    masterView.setSelected(newState);
                    switchCompat.setChecked(newState);
                    getPreferenceScreen().getSharedPreferences().edit().putBoolean(getPreferenceScreen().getKey(), newState).commit();
                }
            });

            group.addView(masterView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            /*ProgressBar bar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal);
            bar.setIndeterminate(true);
            bar.setPadding(0, 0, 0, 0);

            ViewGroup.MarginLayoutParams progressBarParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBarParams.setMargins(0, 0, 0, 0);
            group.addView(bar, 1, progressBarParams);*/
        } else {
            throw new IllegalArgumentException("The root element must be an instance of ViewGroup");
        }

        refreshDependencies();

        return view;
    }

    private void setMasterBackground(@NonNull Context ctx) {
        TypedArray a = ctx.obtainStyledAttributes(ATTRS);
        int colorOn = a.getColor(a.getIndex(0), 0);
        int colorOff = a.getColor(a.getIndex(1), 0);
        a.recycle();

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(colorOn));
        drawable.addState(new int[]{}, new ColorDrawable(colorOff));
        masterView.setBackgroundDrawable(drawable);
    }

    protected void refreshDependencies() {
        getPreferenceScreen().notifyDependencyChange(!masterView.isSelected());
    }

    protected void refreshMasterSwitch() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen == null) {
            return;
        }

        if (masterTitle != null) {
            masterTitle.setText(preferenceScreen.getTitle());
        }

        if (masterView != null) {
            masterView.setSelected(preferenceScreen.getSharedPreferences().getBoolean(preferenceScreen.getKey(), false));

            if (switchCompat != null) {
                switchCompat.setChecked(masterView.isSelected());
            }
        }
    }

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        super.setPreferenceScreen(preferenceScreen);

        refreshMasterSwitch();
    }
}
