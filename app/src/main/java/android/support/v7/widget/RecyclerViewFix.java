package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * RecyclerViewFix provides a method that allows to show the scrollbar
 */
public class RecyclerViewFix extends RecyclerView {

    public RecyclerViewFix(Context context) {
        super(context);
    }

    public RecyclerViewFix(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewFix(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void forcedShowScrollbar() {
        awakenScrollBars();
    }
}
