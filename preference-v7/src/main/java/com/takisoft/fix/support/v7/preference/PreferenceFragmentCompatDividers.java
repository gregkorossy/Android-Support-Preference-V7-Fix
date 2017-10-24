package com.takisoft.fix.support.v7.preference;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * As a user requested, here's a variant of PreferenceFragmentCompatFix that allows one to fully
 * customize the dividers between categories and items.
 */
abstract public class PreferenceFragmentCompatDividers extends PreferenceFragmentCompat {
    /**
     * Draws the divider implementation of the official support library.
     */
    public static final int DIVIDER_OFFICIAL = -1;

    /**
     * Draws no dividers.
     */
    public static final int DIVIDER_NONE = 0;

    /**
     * Draws a divider between adjacent categories.
     */
    public static final int DIVIDER_CATEGORY_BETWEEN = 1;

    /**
     * Draws a divider before the first item in a category group.
     * <p>For example: in case of multiple adjacent categories (so there are empty ones), this flag
     * will let the divider be drawn before the first category in this group.</p>
     * <p><i>Note that in some situations this behaves the same as {@link #DIVIDER_PREFERENCE_AFTER_LAST}.</i></p>
     */
    public static final int DIVIDER_CATEGORY_BEFORE_FIRST = 1 << 1;

    /**
     * Draws a divider after the last item in a category group.
     * <p>For example: in case of multiple adjacent categories (so there are empty ones), this flag
     * will let the divider be drawn after the last category in this group.</p>
     * <p><i>Note that in some situations this behaves the same as {@link #DIVIDER_PREFERENCE_BEFORE_FIRST}.</i></p>
     */
    public static final int DIVIDER_CATEGORY_AFTER_LAST = 1 << 2;

    /**
     * Draws a divider between adjacent preferences.
     */
    public static final int DIVIDER_PREFERENCE_BETWEEN = 1 << 4;

    /**
     * Draws a divider before the first item in a preference group.
     * <p><i>Note that in some situations this behaves the same as {@link #DIVIDER_CATEGORY_AFTER_LAST}.</i></p>
     */
    public static final int DIVIDER_PREFERENCE_BEFORE_FIRST = 1 << 5;

    /**
     * Draws a divider after the last item in a preference group.
     * <p><i>Note that in some situations this behaves the same as {@link #DIVIDER_CATEGORY_BEFORE_FIRST}.</i></p>
     */
    public static final int DIVIDER_PREFERENCE_AFTER_LAST = 1 << 6;

    /**
     * Draws the divider according to the preferences' padding. Can be set with {@link #DIVIDER_PADDING_PARENT}.
     */
    public static final int DIVIDER_PADDING_CHILD = 1 << 8;

    /**
     * Draws a divider using the parent's (RecyclerView) padding.  Can be set with {@link #DIVIDER_PADDING_CHILD}.
     */
    public static final int DIVIDER_PADDING_PARENT = 1 << 9;

    /**
     * Won't draw a divider before the very first item. This is useful in certain situations where
     * there would be a divider before the first element of the list (for example when using
     * {@link #DIVIDER_CATEGORY_BEFORE_FIRST} and the first item in the list is a preference
     * category).
     */
    public static final int DIVIDER_NO_BEFORE_FIRST = 1 << 16;

    /**
     * Won't draw a divider after the very last item. This is useful in certain situations where
     * there would be a divider after the last element of the list (for example when using
     * {@link #DIVIDER_PREFERENCE_AFTER_LAST} and the last item in the list is a preference).
     */
    public static final int DIVIDER_NO_AFTER_LAST = 1 << 17;

    /**
     * Draws the material guidelines compatible divider implementation.
     */
    public static final int DIVIDER_DEFAULT = DIVIDER_CATEGORY_BEFORE_FIRST | DIVIDER_CATEGORY_BETWEEN | DIVIDER_NO_BEFORE_FIRST;

    @IntDef(flag = true, value = {
            DIVIDER_OFFICIAL, DIVIDER_DEFAULT, DIVIDER_NONE,
            DIVIDER_CATEGORY_BETWEEN,
            DIVIDER_CATEGORY_BEFORE_FIRST,
            DIVIDER_CATEGORY_AFTER_LAST,
            DIVIDER_PREFERENCE_BETWEEN,
            DIVIDER_PREFERENCE_BEFORE_FIRST,
            DIVIDER_PREFERENCE_AFTER_LAST,
            DIVIDER_PADDING_CHILD,
            DIVIDER_PADDING_PARENT,
            DIVIDER_NO_BEFORE_FIRST,
            DIVIDER_NO_AFTER_LAST
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface DividerPrefFlags {
    }

    private boolean divPrefInvalid = false;
    @DividerPrefFlags
    private int divPrefFlags = DIVIDER_DEFAULT;

    private DividerItemDecoration divItemDecoration;

    /**
     * Sets the divider decoration flags. The values can be either
     * <ul>
     * <li>{@link #DIVIDER_DEFAULT}</li>
     * <li>{@link #DIVIDER_OFFICIAL}</li>
     * <li>{@link #DIVIDER_NONE}</li>
     * <li>or a combination of
     * <ul>
     * <li>{@link #DIVIDER_CATEGORY_BEFORE_FIRST}</li>
     * <li>{@link #DIVIDER_CATEGORY_BETWEEN}</li>
     * <li>{@link #DIVIDER_CATEGORY_AFTER_LAST}</li>
     * <li>{@link #DIVIDER_PREFERENCE_BEFORE_FIRST}</li>
     * <li>{@link #DIVIDER_PREFERENCE_BETWEEN}</li>
     * <li>{@link #DIVIDER_PREFERENCE_AFTER_LAST}</li>
     * <li>{@link #DIVIDER_PADDING_CHILD}</li>
     * <li>{@link #DIVIDER_PADDING_PARENT}</li>
     * <li>{@link #DIVIDER_NO_BEFORE_FIRST}</li>
     * <li>{@link #DIVIDER_NO_AFTER_LAST}</li>
     * </ul>
     * </li>
     * </ul>
     * <p>
     * Note that you <em>should not</em> combine {@linkplain #DIVIDER_DEFAULT}, {@linkplain #DIVIDER_OFFICIAL}, and {@linkplain #DIVIDER_NONE} with
     * each other nor the other flags mentioned above.
     *
     * @param flags The preferred divider drawing flags. Check {@link PreferenceFragmentCompatDividers} for possible values.
     */
    protected void setDividerPreferences(@DividerPrefFlags final int flags) {
        final RecyclerView recyclerView = getListView();

        if (recyclerView == null) {
            Log.w("PreferenceFragmentFix", "Warning: setDividerPreferences(flags) was called before the list was constructed. Please, move the method to onCreateView(...) after the super.onCreateView(...) call!");
            divPrefFlags = flags;
            divPrefInvalid = true;
            return;
        }

        if (divPrefFlags == flags && !divPrefInvalid) {
            return;
        }

        applyDividerPreference(recyclerView, flags);
    }

    void applyDividerPreference(final RecyclerView recyclerView, @DividerPrefFlags final int flags) {
        boolean decoratorRecreateNeeded = flags != divPrefFlags || divPrefInvalid;

        divPrefFlags = flags;
        divPrefInvalid = false;

        if (flags == DIVIDER_NONE) {
            setDivider(null);

            if (divItemDecoration != null) {
                recyclerView.removeItemDecoration(divItemDecoration);
                divItemDecoration = null;
            }
        } else if (flags == DIVIDER_OFFICIAL) {
            Drawable divider = getDividerDrawable();
            setDivider(divider);

            if (divItemDecoration != null) {
                recyclerView.removeItemDecoration(divItemDecoration);
                divItemDecoration = null;
            }
        } else {
            super.setDivider(null);

            if (divItemDecoration != null && decoratorRecreateNeeded) {
                recyclerView.removeItemDecoration(divItemDecoration);
                divItemDecoration = null;
            }

            if (divItemDecoration == null) {
                divItemDecoration = new DividerItemDecoration(getDividerDrawable());
                recyclerView.addItemDecoration(divItemDecoration);
            }
        }

        recyclerView.invalidateItemDecorations();
    }

    Drawable getDividerDrawable() {
        TypedArray a = getPreferenceManager().getContext().obtainStyledAttributes(null, R.styleable.PreferenceFragmentCompat, R.attr.preferenceFragmentCompatStyle, 0);
        Drawable divider = a.getDrawable(R.styleable.PreferenceFragmentCompat_android_divider);
        a.recycle();

        return divider;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        divPrefInvalid = true;
        setDividerPreferences(divPrefFlags);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        divPrefInvalid = true;
    }

    @Override
    public void setDivider(@Nullable Drawable divider) {
        super.setDivider(divider);

        if (divItemDecoration != null) {
            divItemDecoration.setDivider(divider);
        }
    }

    @Override
    public void setDividerHeight(int height) {
        super.setDividerHeight(height);

        if (divItemDecoration != null) {
            divItemDecoration.setDividerHeight(height);
        }
    }

    protected class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private static final byte TYPE_CATEGORY = 0;
        private static final byte TYPE_PREFERENCE = 1;
        private static final byte TYPE_UNKNOWN = -1;

        private Drawable divider;
        private int dividerHeight;

        private DividerItemDecoration(Drawable divider) {
            this.divider = divider;

            if (divider != null) {
                dividerHeight = divider.getIntrinsicHeight();
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (this.divider != null) {
                LinearLayoutManager lm = (LinearLayoutManager) parent.getLayoutManager();

                final int first = lm.findFirstVisibleItemPosition();
                final int last = lm.findLastVisibleItemPosition();

                final int lastInAdapter = lm.getItemCount() - 1;

                if (first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION) {
                    return;
                }

                final int left;
                final int right;

                if ((divPrefFlags & DIVIDER_PADDING_PARENT) == DIVIDER_PADDING_PARENT) {
                    left = parent.getPaddingLeft();
                    right = parent.getWidth() - parent.getPaddingRight();
                } else {
                    left = 0;
                    right = parent.getWidth();
                }

                final byte[] types = new byte[]{TYPE_UNKNOWN, TYPE_UNKNOWN};
                int typePointer = 0;

                for (int i = first; i <= last; i++) {
                    int top;
                    final int baseY;

                    final int viewLeft;
                    final int viewRight;

                    final View view = lm.findViewByPosition(i);

                    if (view == null) {
                        return;
                    }

                    if ((divPrefFlags & DIVIDER_PADDING_CHILD) == DIVIDER_PADDING_CHILD) {
                        viewLeft = left + view.getPaddingLeft();
                        viewRight = right - view.getPaddingRight();
                    } else {
                        viewLeft = left;
                        viewRight = right;
                    }

                    if (i == first) {
                        types[typePointer] = getViewType(view);
                    }

                    if (i < last) {
                        final View viewNext = lm.findViewByPosition(i + 1);
                        types[(typePointer + 1) % 2] = getViewType(viewNext);
                    } else {
                        types[(typePointer + 1) % 2] = TYPE_UNKNOWN;
                    }

                    baseY = (int) view.getY();

                    if (i == 0 && hasDividerAbove(types[typePointer])
                            && (divPrefFlags & DIVIDER_NO_BEFORE_FIRST) != DIVIDER_NO_BEFORE_FIRST) {
                        top = baseY;
                        divider.setBounds(viewLeft, top, viewRight, top + this.dividerHeight);
                        divider.draw(c);
                    }

                    if (hasDividerBelow(types[typePointer], types[(typePointer + 1) % 2])
                            && !(i == lastInAdapter
                            && (divPrefFlags & DIVIDER_NO_AFTER_LAST) == DIVIDER_NO_AFTER_LAST)) {
                        top = baseY + view.getHeight() + view.getPaddingBottom() + view.getPaddingTop();
                        divider.setBounds(viewLeft, top, viewRight, top + this.dividerHeight);
                        divider.draw(c);
                    }

                    typePointer++;
                    typePointer %= 2;
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final byte current = getViewType(view);
            final byte next;

            final LinearLayoutManager lm = (LinearLayoutManager) parent.getLayoutManager();

            final int index = parent.indexOfChild(view);
            if (index < parent.getChildCount() - 1) {
                View viewNext = parent.getChildAt(index + 1);
                next = getViewType(viewNext);
            } else {
                next = TYPE_UNKNOWN;
            }

            if (parent.getChildAdapterPosition(view) == 0 && hasDividerAbove(current)
                    && (divPrefFlags & DIVIDER_NO_BEFORE_FIRST) != DIVIDER_NO_BEFORE_FIRST) {
                outRect.top = dividerHeight;
            }

            if (hasDividerBelow(current, next)
                    && !(parent.getChildAdapterPosition(view) == lm.getItemCount() - 1
                    && (divPrefFlags & DIVIDER_NO_AFTER_LAST) == DIVIDER_NO_AFTER_LAST)) {
                outRect.bottom = dividerHeight;
            }
        }

        private boolean hasDividerAbove(byte current) {
            switch (current) {
                case TYPE_CATEGORY:
                    return (divPrefFlags & DIVIDER_CATEGORY_BEFORE_FIRST) == DIVIDER_CATEGORY_BEFORE_FIRST;
                case TYPE_PREFERENCE:
                    return (divPrefFlags & DIVIDER_PREFERENCE_BEFORE_FIRST) == DIVIDER_PREFERENCE_BEFORE_FIRST;
                default:
                    return false;
            }
        }

        private boolean hasDividerBelow(byte current, byte next) {
            switch (current) {
                case TYPE_CATEGORY:
                    switch (next) {
                        case TYPE_CATEGORY:
                            return (divPrefFlags & DIVIDER_CATEGORY_BETWEEN) == DIVIDER_CATEGORY_BETWEEN;
                        case TYPE_PREFERENCE:
                            return (divPrefFlags & DIVIDER_CATEGORY_AFTER_LAST) == DIVIDER_CATEGORY_AFTER_LAST || (divPrefFlags & DIVIDER_PREFERENCE_BEFORE_FIRST) == DIVIDER_PREFERENCE_BEFORE_FIRST;
                        default:
                            return (divPrefFlags & DIVIDER_CATEGORY_AFTER_LAST) == DIVIDER_CATEGORY_AFTER_LAST;
                    }
                case TYPE_PREFERENCE:
                    switch (next) {
                        case TYPE_PREFERENCE:
                            return (divPrefFlags & DIVIDER_PREFERENCE_BETWEEN) == DIVIDER_PREFERENCE_BETWEEN;
                        case TYPE_CATEGORY:
                            return (divPrefFlags & DIVIDER_PREFERENCE_AFTER_LAST) == DIVIDER_PREFERENCE_AFTER_LAST || (divPrefFlags & DIVIDER_CATEGORY_BEFORE_FIRST) == DIVIDER_CATEGORY_BEFORE_FIRST;
                        default:
                            return (divPrefFlags & DIVIDER_PREFERENCE_AFTER_LAST) == DIVIDER_PREFERENCE_AFTER_LAST;
                    }
            }

            return false;
        }

        private byte getViewType(View view) {
            return (view instanceof ViewGroup) ? TYPE_PREFERENCE : TYPE_CATEGORY;
        }

        public void setDivider(@Nullable Drawable divider) {
            if (divider != null) {
                dividerHeight = divider.getIntrinsicHeight();
            } else {
                dividerHeight = 0;
            }

            this.divider = divider;
            PreferenceFragmentCompatDividers.this.getListView().invalidateItemDecorations();
        }

        public void setDividerHeight(int dividerHeight) {
            this.dividerHeight = dividerHeight;
            PreferenceFragmentCompatDividers.this.getListView().invalidateItemDecorations();
        }
    }
}
