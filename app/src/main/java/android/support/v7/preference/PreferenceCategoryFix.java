package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

public class PreferenceCategoryFix extends PreferenceCategory {
    private static final int[] COLOR_ACCENT_ID = new int[]{android.support.v7.appcompat.R.attr.colorAccent};

    public PreferenceCategoryFix(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceCategoryFix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreferenceCategoryFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceCategoryFix(Context context) {
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

            if (typedArray != null) {
                int accentColor = typedArray.getColor(0, 0);
                titleView.setTextColor(accentColor);
                typedArray.recycle();
            }
        }
    }
}
