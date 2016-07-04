package android.support.v7.preference;

import android.view.View;

/**
 * A class used for proxying the package private PreferenceViewHolder constructor for
 * com.takisoft.fix.support.v7.preference.PreferenceGroupAdapter.
 */
public class PreferenceViewHolderProxy extends PreferenceViewHolder {
    public PreferenceViewHolderProxy(View itemView) {
        super(itemView);
    }
}
