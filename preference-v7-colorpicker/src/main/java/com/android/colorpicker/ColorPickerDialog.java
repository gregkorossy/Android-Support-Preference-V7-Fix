package com.android.colorpicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.takisoft.fix.support.v7.preference.colorpicker.R;

public class ColorPickerDialog extends AlertDialog {

    private final ColorPickerPalette mPalette;
    private final ProgressBar mProgress;

    public ColorPickerDialog(@NonNull Context context, ColorPickerSwatch.OnColorSelectedListener listener, int color) {
        this(context, 0, listener, color);
    }

    public ColorPickerDialog(@NonNull Context context, int themeResId, ColorPickerSwatch.OnColorSelectedListener listener, int color) {
        super(context, resolveDialogTheme(context, themeResId));

        final Context themeContext = getContext();

        View view = LayoutInflater.from(themeContext).inflate(R.layout.color_picker_dialog, null);
        setView(view);

        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mPalette = (ColorPickerPalette) view.findViewById(R.id.color_picker);
        // TODO mPalette.init(mSize, mColumns, this);

        /* TODO
        if (mColors != null) {
            showPaletteView();
        }*/

        /*
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);
         */
    }

    static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            final TypedValue outValue = new TypedValue();
            if (context.getTheme().resolveAttribute(R.attr.colorPickerDialogTheme, outValue, true)) {
                return outValue.resourceId;
            } else {
                return R.style.ThemeOverlay_Material_Dialog_ColorPicker;
            }
        } else {
            return resId;
        }
    }
}
