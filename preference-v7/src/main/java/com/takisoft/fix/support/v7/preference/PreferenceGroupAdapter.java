package com.takisoft.fix.support.v7.preference;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.PreferenceViewHolderProxy;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

class PreferenceGroupAdapter extends android.support.v7.preference.PreferenceGroupAdapter {

    protected List mPreferenceLayouts;

    protected Field fieldResId;
    protected Field fieldWidgetResId;

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);

        try {
            Field preferenceLayoutsField = android.support.v7.preference.PreferenceGroupAdapter.class.getDeclaredField("mPreferenceLayouts");
            preferenceLayoutsField.setAccessible(true);
            mPreferenceLayouts = (List) preferenceLayoutsField.get(this);

            getReflectionFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReflectionFields() {
        try {
            Class<?> aClass = Class.forName("android.support.v7.preference.PreferenceGroupAdapter$PreferenceLayout");
            fieldResId = aClass.getDeclaredField("resId");
            fieldWidgetResId = aClass.getDeclaredField("widgetResId");

            fieldResId.setAccessible(true);
            fieldWidgetResId.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private int[] getReflectedIds(Object pl) {
        int[] ids = new int[2];

        if (fieldResId == null || fieldWidgetResId == null) {
            getReflectionFields(); // try to resolve them again if it didn't happen so far
        }

        try {
            ids[0] = (int) fieldResId.get(pl);
            ids[1] = (int) fieldWidgetResId.get(pl);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // ugly but should work
            ids[0] = 0;
            ids[1] = 0;
        }

        return ids;
    }

    @Override
    public PreferenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.onCreateViewHolder(parent, viewType);
        }

        final Object pl = this.mPreferenceLayouts.get(viewType);
        final int[] reflIds = getReflectedIds(pl);

        // quickfix if the reflected fields couldn't be resolved
        if (reflIds[0] == 0 && reflIds[1] == 0) {
            return super.onCreateViewHolder(parent, viewType);
        }

        int resId = reflIds[0], widgetResId = reflIds[1];

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TypedArray a = parent.getContext().obtainStyledAttributes(null, R.styleable.BackgroundStyle);
        Drawable background = a.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
        if (background == null) {
            //noinspection deprecation
            background = parent.getContext().getResources().getDrawable(android.R.drawable.list_selector_background);
        }

        a.recycle();
        View view = inflater.inflate(resId, parent, false);

        // BEGINNING of the bugfix
        if (view.getBackground() == null) {
            int[] padding = {ViewCompat.getPaddingStart(view), view.getPaddingTop(), ViewCompat.getPaddingEnd(view), view.getPaddingBottom()};
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                view.setBackgroundDrawable(background);
            } else {
                view.setBackground(background);
            }
            ViewCompat.setPaddingRelative(view, padding[0], padding[1], padding[2], padding[3]);
        }
        // END of bugfix

        ViewGroup widgetFrame = view.findViewById(android.R.id.widget_frame);
        if (widgetFrame != null) {
            if (widgetResId != 0) {
                inflater.inflate(widgetResId, widgetFrame);
            } else {
                widgetFrame.setVisibility(View.GONE);
            }
        }

        return new PreferenceViewHolderProxy(view);
    }
}
