package android.support.v7.preference;

import android.support.annotation.IdRes;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;

public class PreferenceViewHolderFix extends PreferenceViewHolder {
    public PreferenceViewHolderFix(PreferenceViewHolder holder) {
        super(holder.itemView);
    }

    @Override
    public View findViewById(@IdRes int id) {
        View v = super.findViewById(id);
        if (v instanceof SwitchCompat) {
            Log.d("ViewHolder", "Trying to read SwitchCompat... NOPE!");
            return null;
        }

        return super.findViewById(id);
    }
}
