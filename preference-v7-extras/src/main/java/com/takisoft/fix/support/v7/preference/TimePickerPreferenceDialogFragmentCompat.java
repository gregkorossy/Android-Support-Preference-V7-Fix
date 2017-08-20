package com.takisoft.fix.support.v7.preference;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.widget.TimePicker;

public class TimePickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements TimePickerDialog.OnTimeSetListener {

    private int pickedHour;
    private int pickedMinute;

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePickerPreference preference = getTimePickerPreference();
        return new TimePickerDialog(getActivity(), this, preference.getHourOfDay(), preference.getMinute(), preference.is24HourView());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        TimePickerPreference preference = getTimePickerPreference();

        if (positiveResult) {
            preference.setTime(pickedHour, pickedMinute);
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        pickedHour = hourOfDay;
        pickedMinute = minute;

        onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
    }
}
