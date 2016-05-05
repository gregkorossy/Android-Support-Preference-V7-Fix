package android.support.v7.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;

import java.lang.reflect.Field;

public class PreferenceManagerFix extends PreferenceManager {

    private static Field editorField;
    private boolean noCommit;

    private boolean inflateInProgress;

    static {
        Field[] fields = android.support.v7.preference.PreferenceManager.class.getDeclaredFields();
        for (Field field : fields) {
            Log.d("FIELD", field.toString());
            if (field.getType() == SharedPreferences.Editor.class) {
                editorField = field;
                editorField.setAccessible(true);
                break;
            }
        }
    }

    public PreferenceManagerFix(Context context) {
        super(context);
    }

    @Override
    public PreferenceScreen inflateFromResource(Context context, int resId, PreferenceScreen rootPreferences) {
        try {
            inflateInProgress = true;
            setNoCommitFix(true);
            PreferenceInflater inflater = new PreferenceInflater(context, this);

            String[] defPacks = inflater.getDefaultPackages();

            String[] newDefPacks = new String[defPacks.length + 1];
            newDefPacks[0] = "com.takisoft.fix.support.v7.preference.";
            System.arraycopy(defPacks, 0, newDefPacks, 1, defPacks.length);

            inflater.setDefaultPackages(newDefPacks);

            rootPreferences = (PreferenceScreen) inflater.inflate(resId, rootPreferences);
            rootPreferences.onAttachedToHierarchy(this);
            setNoCommitFix(false);
            inflateInProgress = false;
            return rootPreferences;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            inflateInProgress = false;
        }

        return super.inflateFromResource(context, resId, rootPreferences);
    }

    @Override
    SharedPreferences.Editor getEditor() {
        if (!inflateInProgress || editorField == null) {
            return super.getEditor();
        }

        if (noCommit) {
            SharedPreferences.Editor editor = null;
            try {
                editor = (SharedPreferences.Editor) editorField.get(this);

                if (editor == null) {
                    editor = this.getSharedPreferences().edit();
                    editorField.set(this, editor);
                }
            } catch (IllegalAccessException e) {
                // TODO is this really what we want?
            }
            return editor;
        } else {
            return this.getSharedPreferences().edit();
        }
    }

    @Override
    boolean shouldCommit() {
        if (!inflateInProgress) {
            return super.shouldCommit();
        } else {
            return noCommit;
        }
    }

    private void setNoCommitFix(boolean noCommit) throws IllegalAccessException {
        SharedPreferences.Editor editor = (SharedPreferences.Editor) editorField.get(this);

        if (!noCommit && editor != null) {
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }

        this.noCommit = noCommit;
    }
}
