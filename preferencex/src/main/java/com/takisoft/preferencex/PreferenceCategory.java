package com.takisoft.preferencex;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;

/**
 * PreferenceCategory fix which allows one to use multiple themes. The original
 * "preference_fallback_accent_color" override would not allow this as it is not modifiable during
 * runtime.
 * If you use this class in your preference XML, you don't have to redefine
 * "preference_fallback_accent_color". Read the README.md for more info.
 */
public class PreferenceCategory extends androidx.preference.PreferenceCategory {
    private static final int[] CATEGORY_ATTRS = new int[]{R.attr.colorAccent};

    protected int color;
    protected View itemView;

    private static int originalStartPadding = Integer.MIN_VALUE;

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreferenceCategory, defStyleAttr, 0);
        color = a.getColor(R.styleable.PreferenceCategory_pref_categoryColor, 0);
        a.recycle();
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceCategoryStyle, android.R.attr.preferenceCategoryStyle));
    }

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    private void setTitleVisibility(View itemView, boolean isVisible) {
        if (itemView == null) {
            return;
        }

        final RecyclerView.LayoutParams currentParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        final RecyclerView.LayoutParams param;

        final boolean wasHidden = itemView.getTag() != null && currentParams.width == 0;

        if (itemView.getTag() == null) {
            param = new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams) currentParams);
            itemView.setTag(param);
        } else {
            param = (RecyclerView.LayoutParams) itemView.getTag();
        }

        if (isVisible) {
            if (itemView.getVisibility() == View.GONE || wasHidden) {
                currentParams.width = param.width;
                currentParams.height = param.height;
                currentParams.leftMargin = param.leftMargin;
                currentParams.rightMargin = param.rightMargin;
                currentParams.topMargin = param.topMargin;
                currentParams.bottomMargin = param.bottomMargin;
                itemView.setVisibility(View.VISIBLE);
            }
        } else {
            if (itemView.getVisibility() == View.VISIBLE || !wasHidden) {
                currentParams.width = 0;
                currentParams.height = 0;
                currentParams.leftMargin = 0;
                currentParams.rightMargin = 0;
                currentParams.topMargin = 0;
                currentParams.bottomMargin = 0;
                itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        setTitleVisibility(itemView, !TextUtils.isEmpty(getTitle()));
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    public void setColorResource(@ColorRes int resId) {
        int color;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            color = getContext().getResources().getColor(resId);
        } else {
            color = getContext().getColor(resId);
        }

        setColor(color);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        itemView = holder.itemView;

        TextView titleView = (TextView) holder.findViewById(android.R.id.title);

        if (titleView != null) {
            // FIXME https://android-review.googlesource.com/c/platform/frameworks/support/+/720306
            //  if (color != 0) {
            //      titleView.setTextColor(color);
            //  }

            final TypedArray typedArray = getContext().obtainStyledAttributes(CATEGORY_ATTRS);

            if (typedArray.length() > 0 && typedArray.getIndexCount() > 0) {
                final int accentColor = typedArray.getColor(typedArray.getIndex(0), 0xff4081); // defaults to pink
                titleView.setTextColor(color == 0 ? accentColor : color);
            }

            typedArray.recycle();

            // FIXME  PreferenceCategory doesn't respect iconSpaceReserved set to false
            // https://issuetracker.google.com/issues/111662669
            // https://github.com/Gericop/Android-Support-Preference-V7-Fix/issues/132
            ViewGroup parent = (ViewGroup) titleView.getParent();

            if (originalStartPadding == Integer.MIN_VALUE) {
                originalStartPadding = ViewCompat.getPaddingStart(parent);
            }

            if (!isIconSpaceReserved()) {
                ViewCompat.setPaddingRelative(parent, 0, parent.getPaddingTop(), 0, parent.getPaddingBottom());
            } else {
                ViewCompat.setPaddingRelative(parent, originalStartPadding, parent.getPaddingTop(), 0, parent.getPaddingBottom());
            }
        }

        boolean isVisible = !TextUtils.isEmpty(getTitle());
        setTitleVisibility(holder.itemView, isVisible);
        /*if (!isVisible) {
            return;
        }*/
    }
}
