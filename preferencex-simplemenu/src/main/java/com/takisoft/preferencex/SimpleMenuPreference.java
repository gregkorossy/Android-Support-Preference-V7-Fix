package com.takisoft.preferencex;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;

import com.takisoft.preferencex.simplemenu.R;
import com.takisoft.preferencex.simplemenu.SimpleMenuPopupWindow;

/**
 * A version of {@link ListPreference} that use
 * <a href="https://material.io/guidelines/components/menus.html#menus-simple-menus">Simple Menus</a>
 * in Material Design as drop down.
 * <p>
 * On pre-Lollipop, it will fallback {@link ListPreference}.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SimpleMenuPreference extends ListPreference {

    private static boolean sLightFixEnabled = false;
    private View mAnchor;
    private View mItemView;
    private SimpleMenuPopupWindow mPopupWindow;
    public SimpleMenuPreference(Context context) {
        this(context, null);
    }
    public SimpleMenuPreference(Context context, AttributeSet attrs) {
        this(context, attrs, Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? R.attr.dialogPreferenceStyle : R.attr.simpleMenuPreferenceStyle);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, R.style.Preference_SimpleMenuPreference);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SimpleMenuPreference, defStyleAttr, defStyleRes);

        int popupStyle = a.getResourceId(R.styleable.SimpleMenuPreference_pref_popupMenuStyle, R.style.Widget_Preference_SimpleMenuPreference_PopupMenu);
        int popupTheme = a.getResourceId(R.styleable.SimpleMenuPreference_pref_popupTheme, R.style.ThemeOverlay_Preference_SimpleMenuPreference_PopupMenu);
        Context popupContext;
        if (popupTheme != 0) {
            popupContext = new ContextThemeWrapper(context, popupTheme);
        } else {
            popupContext = context;
        }

        mPopupWindow = new SimpleMenuPopupWindow(popupContext, attrs, R.styleable.SimpleMenuPreference_pref_popupMenuStyle, popupStyle);
        mPopupWindow.setOnItemClickListener(new SimpleMenuPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int i) {
                String value = getEntryValues()[i].toString();
                if (callChangeListener(value)) {
                    setValue(value);
                }
            }
        });

        a.recycle();
    }

    public static boolean isLightFixEnabled() {
        return sLightFixEnabled;
    }

    public static void setLightFixEnabled(boolean lightFixEnabled) {
        sLightFixEnabled = lightFixEnabled;
    }

    @Override
    protected void onClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            super.onClick();
            return;
        }

        if (getEntries() == null || getEntries().length == 0) {
            return;
        }

        if (mPopupWindow == null) {
            return;
        }

        mPopupWindow.setEntries(getEntries());
        mPopupWindow.setSelectedIndex(findIndexOfValue(getValue()));

        View container = (View) mItemView   // itemView
                .getParent();               // -> list (RecyclerView)

        mPopupWindow.show(mItemView, container, (int) mAnchor.getX());
    }

    @Override
    public void setEntries(@NonNull CharSequence[] entries) {
        super.setEntries(entries);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        mPopupWindow.requestMeasure();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        mItemView = view.itemView;
        mAnchor = view.itemView.findViewById(android.R.id.empty);

        if (mAnchor == null) {
            throw new IllegalStateException("SimpleMenuPreference item layout must contain" +
                    "a view id is android.R.id.empty to support iconSpaceReserved");
        }
    }
}
