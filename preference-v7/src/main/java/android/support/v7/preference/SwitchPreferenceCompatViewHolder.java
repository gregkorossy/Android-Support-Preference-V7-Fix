package android.support.v7.preference;

import android.support.annotation.IdRes;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

public class SwitchPreferenceCompatViewHolder extends PreferenceViewHolder {
    private PreferenceViewHolder holder;

    public SwitchPreferenceCompatViewHolder(PreferenceViewHolder holder) {
        super(holder.itemView);
        this.holder = holder;
    }

    @Override
    public View findViewById(@IdRes int id) {
        View v = super.findViewById(id);
        if (v instanceof SwitchCompat) {
            return null;
        }

        return v;
    }

    @Override
    public void setDividerAllowedAbove(boolean allowed) {
        super.setDividerAllowedAbove(allowed);
        holder.setDividerAllowedAbove(allowed);
    }

    @Override
    public void setDividerAllowedBelow(boolean allowed) {
        super.setDividerAllowedBelow(allowed);
        holder.setDividerAllowedBelow(allowed);
    }
}
