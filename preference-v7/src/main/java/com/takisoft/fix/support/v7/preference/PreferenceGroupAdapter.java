package com.takisoft.fix.support.v7.preference;

import android.annotation.TargetApi;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.PreferenceViewHolderProxy;
import android.util.Log;
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
            Log.d("Reflection", "list: " + mPreferenceLayouts);

            Class<?>[] classes = android.support.v7.preference.PreferenceGroupAdapter.class.getDeclaredClasses();
            for (Class<?> aClass : classes) {
                if ("PreferenceLayout".equals(aClass.getSimpleName())) {
                    fieldResId = aClass.getDeclaredField("resId");
                    fieldWidgetResId = aClass.getDeclaredField("widgetResId");

                    fieldResId.setAccessible(true);
                    fieldWidgetResId.setAccessible(true);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private int[] getReflectedIds(Object pl) {
        int[] ids = new int[2];

        try {
            ids[0] = (int) fieldResId.get(pl);
            ids[1] = (int) fieldWidgetResId.get(pl);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

        int resId = reflIds[0], widgetResId = reflIds[1];

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TypedArray a = parent.getContext().obtainStyledAttributes(null, R.styleable.BackgroundStyle);
        Drawable background = a.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
        if (background == null) {
            background = parent.getContext().getResources().getDrawable(/*17301602*/android.R.drawable.list_selector_background);
        }

        a.recycle();
        View view = inflater.inflate(resId, parent, false);

        // BEGINNING of the bugfix
        int[] padding = {ViewCompat.getPaddingStart(view), view.getPaddingTop(), ViewCompat.getPaddingEnd(view), view.getPaddingBottom()};
        view.setBackgroundDrawable(background);
        ViewCompat.setPaddingRelative(view, padding[0], padding[1], padding[2], padding[3]);
        // END of bugfix

        ViewGroup widgetFrame = (ViewGroup) view.findViewById(/*16908312*/android.R.id.widget_frame);
        if (widgetFrame != null) {
            if (widgetResId != 0) {
                inflater.inflate(widgetResId, widgetFrame);
            } else {
                widgetFrame.setVisibility(View.GONE);
            }
        }

        return new PreferenceViewHolderProxy(view);

        // TODO the bug is in super.onCreateViewHolder(...): view.setBackgroundDrawable(background); resets the padding

        //Log.d("PreferenceViewHolder", "viewType: " + viewType);

        /*LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutInflater inflater2 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.preference_material, parent, false);
        View view2 = inflater2.inflate(R.layout.preference_material, parent, false);

        final PreferenceViewHolder preferenceViewHolder = super.onCreateViewHolder(parent, viewType);

        final TypedArray typedArray2 = parent.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.listPreferredItemPaddingLeft, R.attr.listPreferredItemPaddingRight});
        if (typedArray2.length() > 0) {
            int pLeft = typedArray2.getDimensionPixelSize(0, -1);
            int pRight = typedArray2.getDimensionPixelSize(1, -1);

            Log.d("PreferenceCategory", "pLeft: " + pLeft + ", pRight: " + pRight + ", oLeft: " + preferenceViewHolder.itemView.getPaddingLeft() + ", parent: " + parent);
            //preferenceViewHolder.itemView.setPadding(pLeft, preferenceViewHolder.itemView.getPaddingTop(), pRight, preferenceViewHolder.itemView.getPaddingBottom());
        }

        Log.d("VIEW", "pLeft: " + view.getPaddingLeft());
        Log.d("VIEW2", "pLeft: " + view2.getPaddingLeft());
        Log.d("VIEW_orig", "pLeft: " + preferenceViewHolder.itemView.getPaddingLeft());
        Log.d("PreferenceCategory", "---------");

        return preferenceViewHolder;*/
    }
}
