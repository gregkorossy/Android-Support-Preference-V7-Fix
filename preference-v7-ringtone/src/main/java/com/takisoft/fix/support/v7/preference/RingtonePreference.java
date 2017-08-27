package com.takisoft.fix.support.v7.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.ringtone.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RingtonePreference extends DialogPreference {
    private int ringtoneType;
    private boolean showDefault;
    private boolean showSilent;

    private Uri ringtoneUri;

    @IntDef({
            RingtoneManager.TYPE_ALL,
            RingtoneManager.TYPE_ALARM,
            RingtoneManager.TYPE_NOTIFICATION,
            RingtoneManager.TYPE_RINGTONE
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface RingtoneType {
    }

    static {
        PreferenceFragmentCompat.addDialogPreference(RingtonePreference.class, RingtonePreferenceDialogFragmentCompat.class);
    }

    public RingtonePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        android.preference.RingtonePreference proxyPreference;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            proxyPreference = new android.preference.RingtonePreference(context, attrs, defStyleAttr, defStyleRes);
        } else {
            proxyPreference = new android.preference.RingtonePreference(context, attrs, defStyleAttr);
        }

        ringtoneType = proxyPreference.getRingtoneType();
        showDefault = proxyPreference.getShowDefault();
        showSilent = proxyPreference.getShowSilent();
    }

    public RingtonePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public RingtonePreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public RingtonePreference(Context context) {
        this(context, null);
    }

    /**
     * Returns the sound type(s) that are shown in the picker.
     *
     * @return The sound type(s) that are shown in the picker.
     * @see #setRingtoneType(int)
     */
    @RingtoneType
    public int getRingtoneType() {
        return ringtoneType;
    }

    /**
     * Sets the sound type(s) that are shown in the picker. See {@link RingtoneManager} for the
     * possible values.
     *
     * @param ringtoneType The sound type(s) that are shown in the picker.
     */
    public void setRingtoneType(@RingtoneType int ringtoneType) {
        this.ringtoneType = ringtoneType;
    }

    /**
     * Returns whether to a show an item for the default sound/ringtone.
     *
     * @return Whether to show an item for the default sound/ringtone.
     */
    public boolean getShowDefault() {
        return showDefault;
    }

    /**
     * Sets whether to show an item for the default sound/ringtone. The default
     * to use will be deduced from the sound type(s) being shown.
     *
     * @param showDefault Whether to show the default or not.
     */
    public void setShowDefault(boolean showDefault) {
        this.showDefault = showDefault;
    }

    /**
     * Returns whether to a show an item for 'None'.
     *
     * @return Whether to show an item for 'None'.
     */
    public boolean getShowSilent() {
        return showSilent;
    }

    /**
     * Sets whether to show an item for 'None'.
     *
     * @param showSilent Whether to show 'None'.
     */
    public void setShowSilent(boolean showSilent) {
        this.showSilent = showSilent;
    }

    public Uri getRingtone() {
        return onRestoreRingtone();
    }

    public void setRingtone(Uri uri) {
        setInternalRingtone(uri, false);
    }

    private void setInternalRingtone(Uri uri, boolean force) {
        Uri oldUri = onRestoreRingtone();

        final boolean changed = (oldUri != null && !oldUri.equals(uri)) || (uri != null && !uri.equals(oldUri));

        if (changed || force) {
            final boolean wasBlocking = shouldDisableDependents();

            ringtoneUri = uri;
            onSaveRingtone(uri);

            final boolean isBlocking = shouldDisableDependents();

            notifyChanged();

            if (isBlocking != wasBlocking) {
                notifyDependencyChange(isBlocking);
            }
        }
    }

    /**
     * Called when a ringtone is chosen.
     * <p>
     * By default, this saves the ringtone URI to the persistent storage as a
     * string.
     *
     * @param ringtoneUri The chosen ringtone's {@link Uri}. Can be null.
     */
    protected void onSaveRingtone(Uri ringtoneUri) {
        persistString(ringtoneUri != null ? ringtoneUri.toString() : "");
    }

    /**
     * Called when the chooser is about to be shown and the current ringtone
     * should be marked. Can return null to not mark any ringtone.
     * <p>
     * By default, this restores the previous ringtone URI from the persistent
     * storage.
     *
     * @return The ringtone to be marked as the current ringtone.
     */
    protected Uri onRestoreRingtone() {
        final String uriString = getPersistedString(ringtoneUri == null ? null : ringtoneUri.toString());
        return !TextUtils.isEmpty(uriString) ? Uri.parse(uriString) : null;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValueObj) {
        final String defaultValue = (String) defaultValueObj;
        setInternalRingtone(restoreValue ? onRestoreRingtone() : (!TextUtils.isEmpty(defaultValue) ? Uri.parse(defaultValue) : null), true);
    }

    @Override
    public boolean shouldDisableDependents() {
        return super.shouldDisableDependents() || onRestoreRingtone() == null;
    }
}
