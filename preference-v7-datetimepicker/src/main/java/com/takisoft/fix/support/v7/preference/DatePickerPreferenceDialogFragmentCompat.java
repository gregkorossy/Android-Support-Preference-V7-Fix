package com.takisoft.fix.support.v7.preference;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;

import com.takisoft.datetimepicker.DatePickerDialog;
import com.takisoft.datetimepicker.widget.DatePicker;

public class DatePickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DatePickerDialog.OnDateSetListener {

    private int pickedYear;
    private int pickedMonth;
    private int pickedDay;

    private DatePickerPreference getDatePickerPreference() {
        return (DatePickerPreference) getPreference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerPreference preference = getDatePickerPreference();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, preference.getYear(), preference.getMonth(), preference.getDay());

        //dialog.setTitle(preference.getDialogTitle()); // this does not work in landscape
        //dialog.setIcon(preference.getDialogIcon());
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, preference.getPositiveButtonText(), this);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, preference.getNegativeButtonText(), this);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            ((DatePickerDialog) getDialog()).onClick(dialog, which);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        DatePickerPreference preference = getDatePickerPreference();

        if (positiveResult) {
            preference.setDate(pickedYear, pickedMonth, pickedDay);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        pickedYear = year;
        pickedMonth = month;
        pickedDay = day;

        super.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
    }
}
