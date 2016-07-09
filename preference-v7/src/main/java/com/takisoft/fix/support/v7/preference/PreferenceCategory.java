package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * PreferenceCategory fix which allows one to use multiple themes. The original
 * "preference_fallback_accent_color" override would not allow this as it is not modifiable during
 * runtime.
 * If you use this class in your preference XML, you don't have to redefine
 * "preference_fallback_accent_color". Read the README.md for more info.
 */
public class PreferenceCategory extends android.support.v7.preference.PreferenceCategory {
    private static final int[] CATEGORY_ATTRS = new int[]{R.attr.colorAccent, R.attr.preferenceCategory_marginBottom};

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

        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);

        if (titleView != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(CATEGORY_ATTRS);

            if (typedArray.length() > 0 && typedArray.getIndexCount() > 0) {
                final int accentColor = typedArray.getColor(typedArray.getIndex(0), 0xff4081); // defaults to pink
                titleView.setTextColor(accentColor);

                final int marginIdx = typedArray.getIndex(1);
                final TypedValue marginValue = typedArray.peekValue(marginIdx);

                if (typedArray.hasValue(marginIdx) && marginValue != null && marginValue.type == TypedValue.TYPE_DIMENSION) {
                    int bottomMargin = typedArray.getDimensionPixelSize(marginIdx, -1);
                    if (bottomMargin > -1) {
                        ((ViewGroup.MarginLayoutParams) titleView.getLayoutParams()).bottomMargin = bottomMargin;
                    }
                }
            }

            typedArray.recycle();
        }
    }
}
