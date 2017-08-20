package com.takisoft.fix.support.v7.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceDialogFragmentCompat;

import com.takisoft.fix.support.v7.preference.extras.R;

import java.util.ArrayList;
import java.util.List;

public class RingtonePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private RingtoneManager ringtoneManager;
    private int selectedIndex = -1;

    public static RingtonePreferenceDialogFragmentCompat newInstance(String key) {
        RingtonePreferenceDialogFragmentCompat fragment = new RingtonePreferenceDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    private RingtonePreference getRingtonePreference() {
        return (RingtonePreference) getPreference();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ringtoneManager = new RingtoneManager(context);
    }

    private Ringtone defaultRingtone;

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        RingtonePreference ringtonePreference = getRingtonePreference();

        ringtoneManager.setType(ringtonePreference.getRingtoneType());
        ringtoneManager.setStopPreviousRingtone(true);

        Cursor cursor = ringtoneManager.getCursor();

        final List<String> ringtoneList = new ArrayList<>(cursor.getCount());

        final Context context = getContext();

        final Uri ringtoneUri = ringtonePreference.getRingtone();

        final int ringtoneType = ringtonePreference.getRingtoneType();
        final boolean showDefault = ringtonePreference.getShowDefault();
        final boolean showSilent = ringtonePreference.getShowSilent();
        final Uri defaultUri;

        if (showDefault) {
            defaultUri = RingtoneManager.getDefaultUri(ringtoneType);
            switch (ringtoneType) {
                case RingtoneManager.TYPE_ALARM:
                    ringtoneList.add(getString(R.string.alarm_sound_default));
                    break;
                case RingtoneManager.TYPE_NOTIFICATION:
                    ringtoneList.add(getString(R.string.notification_sound_default));
                    break;
                case RingtoneManager.TYPE_RINGTONE:
                case RingtoneManager.TYPE_ALL:
                default:
                    ringtoneList.add(getString(R.string.ringtone_default));
                    break;
            }
        } else {
            defaultUri = null;
        }

        if (showSilent) {
            ringtoneList.add(getString(R.string.ringtone_silent));
        }

        if (cursor.moveToFirst()) {
            do {
                ringtoneList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
            } while (cursor.moveToNext());
        }

        selectedIndex = ringtoneManager.getRingtonePosition(ringtoneUri);
        if (selectedIndex >= 0) {
            selectedIndex += (showDefault ? 1 : 0) + (showSilent ? 1 : 0);
        }

        if (selectedIndex < 0 && showDefault) {
            if (RingtoneManager.getDefaultType(ringtoneUri) != -1) {
                selectedIndex = 0;
            }
        }

        if (selectedIndex < 0 && showSilent) {
            selectedIndex = showDefault ? 1 : 0;
        }

        builder
                .setSingleChoiceItems(ringtoneList.toArray(new String[0]), selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedIndex = i;

                        int realIdx = i - (showDefault ? 1 : 0) - (showSilent ? 1 : 0);

                        if (defaultRingtone != null) {
                            defaultRingtone.stop();
                        }

                        ringtoneManager.stopPreviousRingtone();

                        if (showDefault && i == 0) {
                            if (defaultRingtone != null) {
                                defaultRingtone.play();
                            } else {
                                defaultRingtone = RingtoneManager.getRingtone(context, defaultUri);
                                defaultRingtone.play();
                            }
                        } else if (((showDefault && i == 1) || (!showDefault && i == 0)) && showSilent) {
                            ringtoneManager.stopPreviousRingtone(); // "playing" silence
                        } else {
                            Ringtone ringtone = ringtoneManager.getRingtone(realIdx);
                            ringtone.play();
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (defaultRingtone != null) {
                            defaultRingtone.stop();
                        }

                        RingtonePreferenceDialogFragmentCompat.this.onDismiss(dialogInterface);
                    }
                })
                //.setTitle(R.string.ringtone_picker_title)
                .setNegativeButton(android.R.string.cancel, this)
                .setPositiveButton(android.R.string.ok, this);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (defaultRingtone != null && defaultRingtone.isPlaying()) {
            defaultRingtone.stop();
        }

        defaultRingtone = null;

        if (ringtoneManager != null) {
            ringtoneManager.stopPreviousRingtone();
        }

        final RingtonePreference preference = getRingtonePreference();
        final boolean showDefault = preference.getShowDefault();
        final boolean showSilent = preference.getShowSilent();

        if (positiveResult && selectedIndex >= 0) {
            final Uri uri;

            if (showDefault && selectedIndex == 0) {
                uri = RingtoneManager.getDefaultUri(preference.getRingtoneType());
            } else if (((showDefault && selectedIndex == 1) || (!showDefault && selectedIndex == 0)) && showSilent) {
                uri = null;
            } else {
                uri = ringtoneManager.getRingtoneUri(selectedIndex - (showDefault ? 1 : 0) - (showSilent ? 1 : 0));
            }

            if (preference.callChangeListener(uri)) {
                preference.setRingtone(uri);
            }
        }
    }
}
