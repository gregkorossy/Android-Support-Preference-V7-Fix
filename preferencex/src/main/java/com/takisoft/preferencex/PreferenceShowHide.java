package com.takisoft.preferencex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/** Expand/collapse <b>PreferenceCategory</b> by tapping it's title ...<br>
 <ul>Edit your sub-classed <b>PreferenceFragmentCompat</b> as follows: </ul>
 <li>Instantiate this class in <i>onCreatePreferencesFix()</i> <b>after</b> xml expansion and all
 dynamic modifications have taken place</li>
 <li>Supply <i>RecyclerView</i> by overriding <i>onCreateRecyclerView()</i> </li> <br>
 Refer to: PreferenceFragmentCompat.onCreatePreferencesFix() <br>

 <ul><b>Additionally ...</b>
 <li>Your PreferenceScreen must have a key (eg: "root")</li>
 <li>Every PreferenceCategory AND Preference will ideally have a unique key</li>
 <li>Every PreferenceCategory probably should have a title. (Otherwise there is nothing for the user to
 tap and thereby toggle child Preference visibility)</li>
 <li>If a PreferenceCategory has an empty title it's children will not be hidden.</li>
 <li>If a child Preference has no key, one will be generated.</li>
 <li>Keys are generally required for internal use by this program but vital should your
 application wish to display only a specific PreferenceCategory or Preference.</li>
 <li>Nested <i>PreferenceScreen</i> is tolerated but otherwise unsupported.</li></ul>

 <ul><b>Scrolling</b>
 <li>Default action is to smooth scroll newly-expanded category to top of View.</li>
 <li>User may suppress scrolling by issuing <i>setScroll(false).</i></li>
 <li>User may override scrolling. See {@link #setCustomScrollInterface(CustomScroll)}</li></ul>
 <b>Author:</b> Matt Arnold (GitHub user MPArnold)*/
public class PreferenceShowHide {
    PreferenceFragmentCompat pfc;               // References to caller
    private String root;                        // PreferenceScreen name.
    public String getRoot() { return root; }
    private String specPref;                    // Named PreferenceCategory or Preference
    public String getSpecPref() { return specPref; }
    private boolean specific;                   // Handy flag for above
    public boolean isSpecific(){ return specific; }
    private boolean debug;                      // Request/suppress logging
    private RecyclerView RV = null;
    /** User responsibility to supply RecyclerView! See 'onCreateRecyclerView()' */
    public void setRecyclerView(RecyclerView recyclerView) {
        if (!isInitOK()) return;
        RV = recyclerView;
        OITL = new RecyclerItemClickListener(pfc.getContext(), new CategoryListener() {
            @Override
            public void onItemClick(View view, int position) { clicked(view, position); }
        });
        if (RV!=null) RV.addOnItemTouchListener(OITL);
    }
    private List<String> categories = new ArrayList<>();
    private List<Boolean> collapsed = new ArrayList<>();
    private List<String[]> preferences = new ArrayList<>();
    public String lastLogged;
    private boolean initOK;
    public boolean isInitOK() { return initOK; }
    private boolean scroll=true;
    public void setScroll(boolean scroll) { this.scroll = scroll; }
    private CustomScroll customScrollInterface = null;
    public void setCustomScrollInterface(CustomScroll iFace) { customScrollInterface = iFace; }

    /** Constructor. (Caller may issue isInitOK() to test for success.)
     @param pfc          Your PreferenceFragmentCompat
     @param specPref     Key of specific PreferenceCategory OR specific Preference OR null/empty
     @param debug        true: log messages
     @see #isInitOK() */
    public PreferenceShowHide(PreferenceFragmentCompat pfc, String specPref, boolean debug) {
        this.pfc = pfc;
        this.specPref = specPref;
        specific = !(specPref==null || specPref.isEmpty());
        this.debug = debug;
        initOK = analyzeXML();
    }

    /** Analyze XML / Collapse all categories.
     @return false Exception described in 'lastLogged'  */
    private boolean analyzeXML() {
        PreferenceScreen ps = pfc.getPreferenceScreen();
        root = ps.getKey();
        if (root==null || root.isEmpty()) {
            logFatal("PreferenceScreen has null or empty key. Please repair");
            return false;
        }

        int generated = 0;
        for (int i=0; i<ps.getPreferenceCount(); i++) {
            Preference p = ps.getPreference(i);
            if (!(p instanceof PreferenceCategory)) {
                logFatal(format("Child of %s (%s) is NOT 'PreferenceCategory'", ps.getKey(), p.getKey()));
                return false;
            }

            // Build Array of PreferenceCategory keys
            PreferenceCategory pc = (PreferenceCategory)p;
            if (pc.getKey()==null) {
                logFatal("PreferenceCategory has null key. Please repair");
                return false;
            }
            categories.add(pc.getKey());
            collapsed.add(true);                // Track expanded / collapsed categories

            // Build Array of Preference Arrays
            String[] prefs = new String[pc.getPreferenceCount()];
            log(format("Tabulating category '%s' (%d children)", categories.get(i), prefs.length));
            String pk;                  // Key of preference (generated if null)
            for (int j=0; j<pc.getPreferenceCount(); j++) {
                pk = pc.getPreference(j).getKey();
                if (pk==null) {
                    generated ++;
                    pk = "AutoGen" + generated;
                    pc.getPreference(j).setKey(pk);
                }
                prefs[j] = pk;
                boolean vis = false;                    // Normal case
                // Category with empty title. [ Nothing to 'tap' ]
                // Therefore must leave the children visible!
                if (pc.getTitle().toString().isEmpty()) vis = true;
                if(!specific) pfc.findPreference(pk).setVisible(vis);
                everySinglePreference(pc.getPreference(j));
            }
            preferences.add(prefs);
        }
        if (generated>0) { log(format("%d Preference(s) without keys", generated)); }
        return true;
    }

    /** Toggle visibility of all children in the category */
    @SuppressLint("RestrictedApi")
    private void clicked(View v, int position) {
        Preference pref = ((PreferenceGroupAdapter) RV.getAdapter()).getItem(position);
        if (!(pref instanceof PreferenceCategory)) return;
        String cat = pref.getKey();
        int ix = categories.indexOf(cat);
        log(format("Clicked category '%s' [%d] collapsed=%b", cat, ix, collapsed.get(ix)));
        for (String key : preferences.get(ix)) {
            if (pfc.findPreference(key)==null) continue;    // Normal for non-category subset
            pfc.findPreference(key).setVisible(collapsed.get(ix));
        }
        collapsed.set(ix, !(collapsed.get(ix)));
        if (collapsed.get(ix)) return;

        // Scroll tapped category to top of screen
        if (!scroll) return;                            // User has issued 'setScroll(false)'
        if (customScrollInterface != null) {
            customScrollInterface.scroll(RV, position); // User-defined scrolling
            return;
        }
        smoothScrollQ(RV, position);                    // Default scrolling

    }

    //----------------------------------------------------------------------------------------------
    /** Gets called for every single preference [Experiment] */
    private void everySinglePreference(Preference pref) {
        if (true) return;
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            etp.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    // So what! Doesn't tell me what the XML EditTextPreference inputType was!
                }
            });
        }
    }

    //----------------------------------------------------------------------------------------------
    /** Remove all preferences except the one specified during construction.<br>
     If that was a PreferenceCategory retain visibility of the entire category. <br>
     Tip: Call this from your PreferenceFragmentCompat 'onStart()' override. It is never unsafe
     to make this call as no action is performed when all preferences were requested. */
    @SuppressLint("RestrictedApi")
    public void removeAllButPreviouslySpecifiedPreference() {
        if (!isInitOK() || !specific || RV == null) return;
        Preference keepPref = pfc.findPreference(specPref);
        if (keepPref == null) { return; }
        PreferenceGroup keepParent = getParent(keepPref);
        boolean keepCategory = (root.equals(keepParent.getKey()));
        log(format("Removing all preferences except '%s/%s'", keepParent.getKey(), specPref));
        for (int i = 0; i < RV.getAdapter().getItemCount(); i++) {
            Preference pref = ((PreferenceGroupAdapter) RV.getAdapter()).getItem(i);
            PreferenceGroup parent = getParent(pref);
            if (parent == null) continue;               // Already deleted!
            if (keepCategory) {
                if (pref.getKey().equals(specPref)) continue;
                if (parent.getKey().equals(specPref)) continue;
            } else {
                if(pref.getKey().equals(keepParent.getKey()) || pref.getKey().equals(specPref)) continue;
            }
            boolean rc = parent.removePreference(pref); // NB: If a parent, children go also!
            log(format("removePreference(%s/%s)=%b", parent.getKey(), pref.getKey(), rc));
        }
        log(format("remaining=%d", RV.getAdapter().getItemCount()));
    }

    /** Obtain the parent of specified child
     @param      child Preference (ie: PreferenceCategory OR specific Preference)
     @return     Parent PreferenceGroup (ie: PreferenceScreen OR PreferenceCategory */
    private PreferenceGroup getParent(Preference child) {
        PreferenceGroup parent = getParent(pfc.getPreferenceScreen(), child);
        if (parent!=null) log(format("Found child '%s' within parent '%s'", child.getKey(), parent.getKey()));
        return parent;
    }
    /** Recursively locate parent of specified child */
    private PreferenceGroup getParent(PreferenceGroup group, Preference child) {
        for (int i = 0; i < group.getPreferenceCount(); i++)  {
            Preference p = group.getPreference(i);
            if (p == child) return group;
            if (p instanceof PreferenceGroup) {     // ie: PreferenceScreen/PreferenceCategory
                PreferenceGroup parent = getParent((PreferenceGroup)p, child);
                if (parent != null) return parent;
            }
        }
        return null;                    // Not here OR previously removed
    }

    // ---------------------------------------------------------------------------------------------

    /** Smoothly scroll to specified position at 1/4 speed <br>
     This default scroller is extremely primitive but OK for short things like Preferences */
    private void smoothScrollQ(RecyclerView rv, int position) throws IllegalArgumentException {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(pfc.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
            @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                float millesecondsPerPixel = super.calculateSpeedPerPixel(displayMetrics);
                return millesecondsPerPixel * 4;
            }
        };
        smoothScroller.setTargetPosition(position);
        rv.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    // ---------------------------------------------------------------------------------------------

    /** Click listener reacting to PreferenceCategory */
    RecyclerView.OnItemTouchListener OITL;

    /** Custom RecyclerView touch listener.  */
    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private CategoryListener mListener;
        GestureDetector mGestureDetector;
        /** Constructor is supplied the callback for PreferenceCategory interception! */
        public RecyclerItemClickListener(Context context, CategoryListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }
        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
    }

    /** Interface supplying RecyclerView item position */
    public interface CategoryListener {
        void onItemClick(View view, int position);
    }

    // ---------------------------------------------------------------------------------------------

    /** Custom scroll Interface */
    public interface CustomScroll {
        void scroll(RecyclerView rv, int targetPosition);
    }

    // ---------------------------------------------------------------------------------------------
    /** Fatal error encountered */
    private void logFatal(String logString) {
        debug = true;
        log (logString);
    }

    /** Override this method via sub-classing to use your preferred logger. */
    public void log(String logString) {
        lastLogged = logString;
        if (!debug) return;
        Log.d("ShowHide", logString);
    }
}
