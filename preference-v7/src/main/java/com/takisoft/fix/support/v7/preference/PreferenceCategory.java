package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * PreferenceCategory fix which allows one to use multiple themes. The original
 * "preference_fallback_accent_color" override would not allow this as it is not modifiable during
 * runtime.
 * If you use this class in your preference XML, you don't have to redefine
 * "preference_fallback_accent_color". Read the README.md for more info.
 */
public class PreferenceCategory extends android.support.v7.preference.PreferenceCategory {
    private static final int[] COLOR_ACCENT_ID = new int[]{android.support.v7.appcompat.R.attr.colorAccent};

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return;

        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);

        if (titleView != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(COLOR_ACCENT_ID);

            if (typedArray.length() > 0) {
                final int accentColor = typedArray.getColor(0, 0xff4081); // defaults to pink
                titleView.setTextColor(accentColor);
            }

            typedArray.recycle();
        }
    }
}
