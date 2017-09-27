package com.takisoft.fix.support.v7.preference;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;

import com.takisoft.datetimepicker.TimePickerDialog;
import com.takisoft.datetimepicker.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

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

        Calendar cal = Calendar.getInstance();

        Date time = preference.getTime();
        Date pickerTime = preference.getPickerTime();

        if (time != null) {
            cal.setTime(time);
        } else if (pickerTime != null) {
            cal.setTime(pickerTime);
        }

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), preference.is24HourView());
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, preference.getPositiveButtonText(), this);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, preference.getNegativeButtonText(), this);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            ((TimePickerDialog) getDialog()).onClick(dialog, which);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        TimePickerPreference preference = getTimePickerPreference();

        if (positiveResult && preference.callChangeListener(new TimePickerPreference.TimeWrapper(pickedHour, pickedMinute))) {
            preference.setTime(pickedHour, pickedMinute);
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        pickedHour = hourOfDay;
        pickedMinute = minute;

        //onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
        super.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
    }
}
