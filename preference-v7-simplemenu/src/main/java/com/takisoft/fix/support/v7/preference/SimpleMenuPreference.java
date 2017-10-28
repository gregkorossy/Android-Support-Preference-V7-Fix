package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.takisoft.fix.support.v7.preference.simplemenu.R;
import com.takisoft.fix.support.v7.preference.widget.SimpleMenuPopupWindow;

/**
 * A version of {@link ListPreference} that use
 * <a href="https://material.io/guidelines/components/menus.html#menus-simple-menus">Simple Menus</a>
 * in Material Design as drop down.
 *
 * On pre-Lollipop, it will fallback {@link ListPreference}.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SimpleMenuPreference extends ListPreference {

    private View mItemView;
    private SimpleMenuPopupWindow mPopupWindow;

    public SimpleMenuPreference(Context context) {
        this(context, null);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs) {
        this(context, attrs, Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? 0 : R.attr.dialogPreferenceStyle);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        int popupStyle = R.style.Preference_SimpleMenuPreference_Popup; // TODO: hardcoded popupStyle

        mPopupWindow = new SimpleMenuPopupWindow(context, attrs, R.styleable.SimpleMenuPreference_popupStyle, popupStyle);
        mPopupWindow.setOnItemClickListener(new SimpleMenuPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int i) {
                String value = getEntryValues()[i].toString();
                if (callChangeListener(value)) {
                    setValue(value);
                }
            }
        });
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
        mPopupWindow.show(mItemView);
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
    }
}
