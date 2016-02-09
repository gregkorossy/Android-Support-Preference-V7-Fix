package android.support.v7.preference;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class PreferenceFragmentCompatFix extends PreferenceFragmentCompat {
    private static final String FRAGMENT_DIALOG_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    private RecyclerView.ItemDecoration itemDecoration;
    private boolean dividersEnabled = true;

    @Override
    public void onResume() {
        super.onResume();
        getListView().scrollBy(0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            enableDividers(dividersEnabled);
        }
    }

    public void enableDividers(boolean enabled) {
        RecyclerView recyclerView = getListView();
        dividersEnabled = enabled;

        if (recyclerView == null) {
            return;
        }

        if (!enabled) {
            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
                itemDecoration = null;
            }
        } else {
            if (itemDecoration == null) {
                createDividers();
                recyclerView.addItemDecoration(itemDecoration);
            }
        }
    }

    private void createDividers() {
        if (itemDecoration != null)
            return;

        itemDecoration = new RecyclerView.ItemDecoration() {
            final int dividerHeight;
            final Paint paint = new Paint();
            final Drawable divider;

            {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(Color.argb(102, 204, 204, 204));

                final int[] attrs = {android.R.attr.listDivider};
                TypedArray ta = getContext().obtainStyledAttributes(attrs);
                divider = ta.getDrawable(0);

                if (divider == null) {
                    dividerHeight = 2;
                } else {
                    dividerHeight = divider.getIntrinsicHeight();
                }

                ta.recycle();
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                LinearLayoutManager lm = (LinearLayoutManager) parent.getLayoutManager();

                final int first = lm.findFirstVisibleItemPosition();
                final int last = lm.findLastVisibleItemPosition();

                final int left = parent.getPaddingLeft();
                final int right = parent.getWidth() - parent.getPaddingRight();

                RecyclerView.Adapter adapter = parent.getAdapter();

                for (int i = first; i <= last; i++) {
                    if (adapter.getItemCount() - 1 <= i) {
                        continue;
                    }

                    final int viewType = adapter.getItemViewType(i);
                    final int viewTypeNext = adapter.getItemViewType(i + 1);

                    if (viewType == 0 || viewTypeNext == 0) {
                        continue; // skipping on and before categories
                    }

                    final View view = lm.findViewByPosition(i);

                    final int top = view.getBottom() + view.getPaddingBottom();
                    final int bottom = top + dividerHeight;

                    if (divider == null) {
                        c.drawRect(left, top, right, bottom, paint);
                    } else {
                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);
                    }
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, dividerHeight);
            }
        };
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (this.getFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG) == null) {
            Object f = null;

            if (preference instanceof EditTextPreferenceFix) {
                f = EditTextPreferenceDialogFragmentCompatFix.newInstance(preference.getKey());
            } else {
                super.onDisplayPreferenceDialog(preference);
            }

            if (f != null) {
                ((DialogFragment) f).setTargetFragment(this, 0);
                ((DialogFragment) f).show(this.getFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        }
    }
}
