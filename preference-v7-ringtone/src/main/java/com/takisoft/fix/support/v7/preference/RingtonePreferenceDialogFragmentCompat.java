package com.takisoft.fix.support.v7.preference;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.ringtone.R;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static android.app.Activity.RESULT_OK;

public class RingtonePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private static final String TAG = "RingtonePrefDialog";

    private static final int CUSTOM_RINGTONE_REQUEST_CODE = 0x9000;
    private static final int WRITE_FILES_PERMISSION_REQUEST_CODE = 0x9001;

    private RingtoneManager ringtoneManager;
    private int selectedIndex = -1;

    private boolean prepared;

    public static RingtonePreferenceDialogFragmentCompat newInstance(String key) {
        RingtonePreferenceDialogFragmentCompat fragment = new RingtonePreferenceDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key);
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
        prepared = false;
    }

    private Ringtone defaultRingtone;

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        RingtonePreference ringtonePreference = getRingtonePreference();

        if (!prepared) {
            ringtoneManager.setType(ringtonePreference.getRingtoneType());
        }
        ringtoneManager.setStopPreviousRingtone(true);

        prepared = true;

        Cursor cursor = ringtoneManager.getCursor();

        final List<String> ringtoneList = new ArrayList<>(cursor.getCount());

        final Context context = getContext();

        final Uri ringtoneUri = ringtonePreference.getRingtone();

        final int ringtoneType = ringtonePreference.getRingtoneType();
        final boolean showDefault = ringtonePreference.getShowDefault();
        final boolean showSilent = ringtonePreference.getShowSilent();
        final boolean showAdd = true; // TODO from ringtonePreference
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

        final int listSize = ringtoneList.size() + (showAdd ? 1 : 0);

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
                        } else if (showAdd && i == listSize - 1) {
                            newRingtone();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);

        ListView listView = dialog.getListView();
        View addRingtoneView = LayoutInflater.from(listView.getContext()).inflate(R.layout.add_ringtone_item, listView, false);
        listView.addFooterView(addRingtoneView);

        return dialog;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CUSTOM_RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
            final Uri fileUri = data.getData();
            final Context context = getContext();

            final RingtonePreference ringtonePreference = getRingtonePreference();
            final int ringtoneType = ringtonePreference.getRingtoneType();

            // FIXME static field leak
            @SuppressLint("StaticFieldLeak") final AsyncTask<Uri, Void, Uri> installTask = new AsyncTask<Uri, Void, Uri>() {
                @Override
                protected Uri doInBackground(Uri... params) {
                    try {
                        Log.d(TAG, "Adding ringtone: " + params[0]);
                        return addCustomExternalRingtone(context, params[0], ringtoneType);
                    } catch (IOException | IllegalArgumentException e) {
                        Log.e(TAG, "Unable to add new ringtone", e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Uri ringtoneUri) {
                    if (ringtoneUri != null) {
                        // TODO requeryForAdapter();

                        /*ListView listView = ((AlertDialog) getDialog()).getListView();
                        View addRingtoneView = LayoutInflater.from(listView.getContext()).inflate(android.support.v7.appcompat.R.layout.select_dialog_singlechoice_material, listView, false);
                        listView.addFooterView(addRingtoneView);*/

                        //((AlertDialog) getDialog()).getListView().addFooterView();

                        Log.d(TAG, "Ringtone added: " + ringtoneUri);
                    } else {
                        // Ringtone was not added, display error Toast
                        Toast.makeText(context, ":(", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            installTask.execute(fileUri);

            /*try {
                Uri uri = addCustomExternalRingtone(context, fileUri, ringtoneType);
                Log.d(TAG, "custom uri: " + uri);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_FILES_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            newRingtone();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void newRingtone() {
        boolean hasPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "has WRITE permission: " + hasPerm);

        ListView listView = ((AlertDialog) getDialog()).getListView();
        View addRingtoneView = LayoutInflater.from(listView.getContext()).inflate(android.support.v7.appcompat.R.layout.select_dialog_singlechoice_material, listView, false);
        CheckedTextView tv = addRingtoneView.findViewById(android.R.id.text1);
        tv.setText("Test " + System.currentTimeMillis());
        listView.addFooterView(addRingtoneView);

        /*if (hasPerm) {
            final Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("audio/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"audio/*", "application/ogg"});
            }
            startActivityForResult(chooseFile, CUSTOM_RINGTONE_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_FILES_PERMISSION_REQUEST_CODE);
        }*/
    }

    @WorkerThread
    public Uri addCustomExternalRingtone(Context context, @NonNull final Uri fileUri, final int type)
            throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("External storage is not mounted. Unable to install ringtones.");
        }

        // Sanity-check: are we actually being asked to install an audio file?
        final String mimeType = context.getContentResolver().getType(fileUri);
        if (mimeType == null || !(mimeType.startsWith("audio/") || mimeType.equals("application/ogg"))) {
            throw new IllegalArgumentException("Ringtone file must have MIME type \"audio/*\"."
                    + " Given file has MIME type \"" + mimeType + "\"");
        }

        // Choose a directory to save the ringtone. Only one type of installation at a time is
        // allowed. Throws IllegalArgumentException if anything else is given.
        final String subdirectory = getDirForType(type);

        // Find a filename. Throws FileNotFoundException if none can be found.
        final File outFile = getUniqueExternalFile(context, subdirectory, getFileDisplayNameFromUri(context, fileUri), mimeType);

        if (outFile != null) {
            // Copy contents to external ringtone storage. Throws IOException if the copy fails.
            final InputStream input = context.getContentResolver().openInputStream(fileUri);
            final OutputStream output = new FileOutputStream(outFile);

            if (input != null) {
                byte[] buffer = new byte[10240];

                for (int len; (len = input.read(buffer)) != -1; ) {
                    output.write(buffer, 0, len);
                }

                input.close();
            }

            output.close();

            // Tell MediaScanner about the new file. Wait for it to assign a {@link Uri}.
            NewRingtoneScanner scanner = null;
            try {
                scanner = new NewRingtoneScanner(outFile);
                return scanner.take();
            } catch (InterruptedException e) {
                throw new IOException("Audio file failed to scan as a ringtone", e);
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        } else {
            return null;
        }
    }

    private static String getDirForType(int type) {
        switch (type) {
            case RingtoneManager.TYPE_ALL:
            case RingtoneManager.TYPE_RINGTONE:
                return Environment.DIRECTORY_RINGTONES;
            case RingtoneManager.TYPE_NOTIFICATION:
                return Environment.DIRECTORY_NOTIFICATIONS;
            case RingtoneManager.TYPE_ALARM:
                return Environment.DIRECTORY_ALARMS;
            default:
                throw new IllegalArgumentException("Unsupported ringtone type: " + type);
        }
    }

    private static String getFileDisplayNameFromUri(Context context, Uri uri) {
        String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getLastPathSegment();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            String[] projection = {OpenableColumns.DISPLAY_NAME};

            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // This will only happen if the Uri isn't either SCHEME_CONTENT or SCHEME_FILE, so we assume
        // it already represents the file's name.
        return uri.toString();
    }

    /**
     * Creates a unique file in the specified external storage with the desired name. If the name is
     * taken, the new file's name will have '(%d)' to avoid overwriting files.
     *
     * @param context      {@link Context} to query the file name from.
     * @param subdirectory One of the directories specified in {@link android.os.Environment}
     * @param fileName     desired name for the file.
     * @param mimeType     MIME type of the file to create.
     * @return the File object in the storage, or null if an error occurs.
     */
    @Nullable
    private static File getUniqueExternalFile(Context context, String subdirectory, String fileName,
                                              String mimeType) {
        File externalStorage = Environment.getExternalStoragePublicDirectory(subdirectory);
        // Make sure the storage subdirectory exists
        //noinspection ResultOfMethodCallIgnored
        externalStorage.mkdirs();

        File outFile;
        try {
            // Ensure the file has a unique name, as to not override any existing file
            outFile = buildUniqueFile(externalStorage, mimeType, fileName);
        } catch (FileNotFoundException e) {
            // This might also be reached if the number of repeated files gets too high
            Log.e(TAG, "Unable to get a unique file name: " + e);
            return null;
        }
        return outFile;
    }

    @NonNull
    private static File buildUniqueFile(File externalStorage, String mimeType, String fileName) throws FileNotFoundException {
        final String[] parts = splitFileName(mimeType, fileName);

        String name = parts[0];
        String ext = (parts[1] != null) ? "." + parts[1] : "";

        File file = new File(externalStorage, name + ext);
        SecureRandom random = new SecureRandom();

        int n = 0;
        while (file.exists()) {
            if (n++ >= 32) {
                n = random.nextInt();
            }
            file = new File(externalStorage, name + " (" + n + ")" + ext);
        }

        return file;
    }

    @NonNull
    public static String[] splitFileName(String mimeType, String displayName) {
        String name;
        String ext;

        String mimeTypeFromExt;

        // Extract requested extension from display name
        final int lastDot = displayName.lastIndexOf('.');
        if (lastDot >= 0) {
            name = displayName.substring(0, lastDot);
            ext = displayName.substring(lastDot + 1);
            mimeTypeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    ext.toLowerCase());
        } else {
            name = displayName;
            ext = null;
            mimeTypeFromExt = null;
        }

        if (mimeTypeFromExt == null) {
            mimeTypeFromExt = "application/octet-stream";
        }

        final String extFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                mimeType);
        //noinspection StatementWithEmptyBody
        if (TextUtils.equals(mimeType, mimeTypeFromExt) || TextUtils.equals(ext, extFromMimeType)) {
            // Extension maps back to requested MIME type; allow it
        } else {
            // No match; insist that create file matches requested MIME
            name = displayName;
            ext = extFromMimeType;
        }


        if (ext == null) {
            ext = "";
        }

        return new String[]{name, ext};
    }

    /**
     * Creates a {@link android.media.MediaScannerConnection} to scan a ringtone file and add its
     * information to the internal database.
     * <p>
     * It uses a {@link java.util.concurrent.LinkedBlockingQueue} so that the caller can block until
     * the scan is completed.
     */
    private class NewRingtoneScanner implements Closeable, MediaScannerConnection.MediaScannerConnectionClient {
        private MediaScannerConnection mMediaScannerConnection;
        private File mFile;
        private LinkedBlockingQueue<Uri> mQueue = new LinkedBlockingQueue<>(1);

        private NewRingtoneScanner(File file) {
            mFile = file;
            mMediaScannerConnection = new MediaScannerConnection(getContext(), this);
            mMediaScannerConnection.connect();
        }

        @Override
        public void close() {
            mMediaScannerConnection.disconnect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMediaScannerConnection.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (uri == null) {
                // There was some issue with scanning. Delete the copied file so it is not oprhaned.
                //noinspection ResultOfMethodCallIgnored
                mFile.delete();
                return;
            }
            try {
                mQueue.put(uri);
            } catch (InterruptedException e) {
                Log.e(TAG, "Unable to put new ringtone Uri in queue", e);
            }
        }

        private Uri take() throws InterruptedException {
            return mQueue.take();
        }
    }
}
