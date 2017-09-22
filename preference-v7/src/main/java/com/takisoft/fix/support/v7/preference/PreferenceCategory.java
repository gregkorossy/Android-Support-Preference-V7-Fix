package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
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

    protected TextView titleView;

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

    private void setTitleVisibility(View itemView, boolean isVisible) {
        if (itemView == null) {
            return;
        }

        final RecyclerView.LayoutParams currentParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        final RecyclerView.LayoutParams param;

        //final boolean firstTime = itemView.getTag() == null;

        if (itemView.getTag() == null) {
            param = new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams) currentParams);
            itemView.setTag(param);
        } else {
            param = (RecyclerView.LayoutParams) itemView.getTag();
        }

        if (isVisible) {
            //if (itemView.getVisibility() == View.GONE && !firstTime) {
            currentParams.width = param.width;
            currentParams.height = param.height;
            currentParams.leftMargin = param.leftMargin;
            currentParams.rightMargin = param.rightMargin;
            currentParams.topMargin = param.topMargin;
            currentParams.bottomMargin = param.bottomMargin;
            itemView.setVisibility(View.VISIBLE);
            //}
        } else {
            //if (itemView.getVisibility() == View.VISIBLE || firstTime) {
            currentParams.width = 0;
            currentParams.height = 0;
            currentParams.leftMargin = 0;
            currentParams.rightMargin = 0;
            currentParams.topMargin = 0;
            currentParams.bottomMargin = 0;
            itemView.setVisibility(View.GONE);
            //}
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        setTitleVisibility(titleView, !TextUtils.isEmpty(getTitle()));
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        titleView = (TextView) holder.findViewById(android.R.id.title);

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

            boolean isVisible = !TextUtils.isEmpty(getTitle());
            setTitleVisibility(holder.itemView, isVisible);
            /*if (!isVisible) {
                return;
            }*/
        }
    }
}
