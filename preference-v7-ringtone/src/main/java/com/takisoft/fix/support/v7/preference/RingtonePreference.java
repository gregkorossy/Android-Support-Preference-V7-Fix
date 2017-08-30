package com.takisoft.fix.support.v7.preference;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    private static final int CUSTOM_RINGTONE_REQUEST_CODE = 0x9000;
    private static final int WRITE_FILES_PERMISSION_REQUEST_CODE = 0x9001;

    private int ringtoneType;
    private boolean showDefault;
    private boolean showSilent;
    private boolean showAdd;

    private Uri ringtoneUri;

    private int miscCustomRingtoneRequestCode = CUSTOM_RINGTONE_REQUEST_CODE;
    private int miscPermissionRequestCode = WRITE_FILES_PERMISSION_REQUEST_CODE;

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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RingtonePreference, defStyleAttr, 0);
        showAdd = a.getBoolean(R.styleable.RingtonePreference_showAdd, true);
        a.recycle();
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

    /**
     * Returns whether to a show an item for 'Add new ringtone'.
     * <p>
     * Note that this requires {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}. If it's
     * not supplied in the manifest, the item won't be displayed.
     *
     * @return Whether to show an item for 'Add new ringtone'.
     */
    public boolean getShowAdd() {
        return showAdd;
    }

    boolean shouldShowAdd() {
        if (showAdd) {
            try {
                PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), PackageManager.GET_PERMISSIONS);
                String[] permissions = pInfo.requestedPermissions;
                for (String permission : permissions) {
                    if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        return true;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Sets whether to show an item for 'Add new ringtone'.
     * <p>
     * Note that this requires {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}. If it's
     * not supplied in the manifest, the item won't be displayed.
     *
     * @param showAdd Whether to show 'Add new ringtone'.
     */
    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    /**
     * This request code will be used to start the file picker activity that the user can use
     * to add new ringtones. The new ringtone will be delivered to
     * {@link RingtonePreferenceDialogFragmentCompat#onActivityResult(int, int, Intent)}.
     * <p>
     * The default value equals to {@link #CUSTOM_RINGTONE_REQUEST_CODE}
     * ({@value #CUSTOM_RINGTONE_REQUEST_CODE}).
     */
    public int getCustomRingtoneRequestCode() {
        return miscCustomRingtoneRequestCode;
    }

    /**
     * Sets the request code that will be used to start the file picker activity that the user can
     * use to add new ringtones. The new ringtone will be delivered to
     * {@link RingtonePreferenceDialogFragmentCompat#onActivityResult(int, int, Intent)}.
     * <p>
     * The default value equals to {@link #CUSTOM_RINGTONE_REQUEST_CODE}
     * ({@value #CUSTOM_RINGTONE_REQUEST_CODE}).
     *
     * @param customRingtoneRequestCode the request code for the file picker
     */
    public void setCustomRingtoneRequestCode(int customRingtoneRequestCode) {
        this.miscCustomRingtoneRequestCode = customRingtoneRequestCode;
    }

    /**
     * This request code will be used to ask for user permission to save (write) new ringtone
     * to one of the public external storage directories (only applies to API 23+). The result will
     * be delivered to
     * {@link RingtonePreferenceDialogFragmentCompat#onRequestPermissionsResult(int, String[], int[])}.
     * <p>
     * The default value equals to {@link #WRITE_FILES_PERMISSION_REQUEST_CODE}
     * ({@value #WRITE_FILES_PERMISSION_REQUEST_CODE}).
     */
    public int getPermissionRequestCode() {
        return miscPermissionRequestCode;
    }

    /**
     * Sets the request code that will be used to ask for user permission to save (write) new
     * ringtone to one of the public external storage directories (only applies to API 23+). The
     * result will be delivered to
     * {@link RingtonePreferenceDialogFragmentCompat#onRequestPermissionsResult(int, String[], int[])}.
     * <p>
     * The default value equals to {@link #WRITE_FILES_PERMISSION_REQUEST_CODE}
     * ({@value #WRITE_FILES_PERMISSION_REQUEST_CODE}).
     *
     * @param permissionRequestCode the request code for the file picker
     */
    public void setPermissionRequestCode(int permissionRequestCode) {
        this.miscPermissionRequestCode = permissionRequestCode;
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
