package android.support.v7.preference;

import android.content.Context;

import java.lang.reflect.Method;

public class PreferenceManagerFix extends PreferenceManager {

    public PreferenceManagerFix(Context context) {
        super(context);
    }

    @Override
    public PreferenceScreen inflateFromResource(Context context, int resId, PreferenceScreen rootPreferences) {
        try {
            Method setNoCommit = PreferenceManager.class.getDeclaredMethod("setNoCommit", Boolean.TYPE);
            setNoCommit.setAccessible(true);
            setNoCommit.invoke(this, true);
            PreferenceInflater inflater = new PreferenceInflater(context, this);

            String[] defPacks = inflater.getDefaultPackages();

            String[] newDefPacks = new String[defPacks.length + 1];
            newDefPacks[0] = "com.takisoft.fix.support.v7.preference.";
            System.arraycopy(defPacks, 0, newDefPacks, 1, defPacks.length);

            inflater.setDefaultPackages(newDefPacks);

            rootPreferences = (PreferenceScreen) inflater.inflate(resId, rootPreferences);
            rootPreferences.onAttachedToHierarchy(this);
            setNoCommit.invoke(this, false);
            setNoCommit.setAccessible(false);
            return rootPreferences;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.inflateFromResource(context, resId, rootPreferences);
    }
}
