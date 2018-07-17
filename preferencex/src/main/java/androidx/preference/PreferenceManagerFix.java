package androidx.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

public class PreferenceManagerFix extends PreferenceManager {

    private static Field editorField;
    private boolean noCommit;

    private boolean inflateInProgress;

    private static Set<String> packages = new ArraySet<>();

    static {
        Field[] fields = androidx.preference.PreferenceManager.class.getDeclaredFields();
        for (Field field : fields) {
            //Log.d("FIELD", field.toString());
            if (field.getType() == SharedPreferences.Editor.class) {
                editorField = field;
                editorField.setAccessible(true);
                break;
            }
        }

        registerPreferencePackage("com.takisoft.preferencex");
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

            /*String[] newDefPacks = new String[defPacks.length + 1];
            newDefPacks[0] = "com.takisoft.preferencex.";
            System.arraycopy(defPacks, 0, newDefPacks, 1, defPacks.length);*/

            String[] newDefPacks = new String[defPacks.length + packages.size()];
            packages.toArray(newDefPacks);
            System.arraycopy(defPacks, 0, newDefPacks, packages.size(), defPacks.length);

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
            editor.apply();
        }

        this.noCommit = noCommit;
    }

    /**
     * Registers a {@link Preference}'s package so the users don't need to supply fully-qualified
     * names in the XML files. Only the package part of the class will be used.
     *
     * @param preferenceClass the {@link Preference} to be registered
     */
    public static void registerPreferencePackage(@NonNull Class<Preference> preferenceClass) {
        registerPreferencePackage(preferenceClass.getPackage().getName());
    }

    /**
     * Registers a {@link Preference}'s package so the users don't need to supply fully-qualified
     * names in the XML files.
     *
     * @param preferencePackage the {@link Preference}'s package name to be registered
     */
    public static void registerPreferencePackage(@NonNull String preferencePackage) {
        packages.add(preferencePackage + (preferencePackage.endsWith(".") ? "" : "."));
    }
}
