package com.takisoft.preferencex;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

// Author: Matt Arnold (GitHub user MPArnold)
/** Expand/collapse <b>PreferenceCategory</b> by tapping it's title ...<br>
 <ul>Edit your sub-classed <b>PreferenceFragmentCompat</b> as follows: </ul>
 <li>Instantiate this class in <i>onCreatePreferencesFix()</i> </li>
 <li>Supply <i>RecyclerView</i> by overriding <i>onCreateRecyclerView()</i> </li>
 Refer to the sample app {@link com.takisoft.preferencex.demo.MyPreferenceFragment} <br>
 <ul>Additionally ...
 <li>Your PreferenceScreen must have a key (eg: "root")</li>
 <li>Every PreferenceCategory AND Preference will ideally have a unique key</li>
 <li>Every PreferenceCategory probably should have a title. (Otherwise there is nothing for the user to
 tap and thereby toggle child Preference visibility)</li>
 <li>If a PreferenceCategory has an empty title it's children will not be hidden.</li>
 <li>If a child Preference has no key, one will be assigned.</li>
 <li>Keys are generally required for internal use by this program but vital should your
 application wish to display only a specific PreferenceCategory or Preference.</li>
 </ul> */
public class PreferenceShowHide {
    PreferenceFragmentCompat pfc;               // References to caller
    private String root;                        // PreferenceScreen name.
    public String getRoot() { return root; }
    private String specPref;                    // Named PreferenceCategory or Preference
    public String getSpecPref() { return specPref; }
    private boolean specific;                   // Handy flag for above
    public boolean isSpecific(){ return specific; };
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

    /** Constructor. (Caller may issue isInitOK() to test for success.)
    @param pfc          Your PreferenceFragmentCompat
    @param specPref     Key of specific PreferenceCategory OR specific Preference OR null/empty
    @param debug        true: log messages
    @see #removeAllButPreviouslySpecifiedPreference()
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
            pfc.findPreference(key).setVisible(collapsed.get(ix));
        }
        collapsed.set(ix, !(collapsed.get(ix)));
        if (collapsed.get(ix)) return;

        // Smoothly scroll tapped category to top of screen at 1/4 speed
        if (scroll) scroll(RV, position);

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
    Tip: Call this from your PreferenceFragmentCompat 'onStart()' override. */
    @SuppressLint("RestrictedApi")
    public void removeAllButPreviouslySpecifiedPreference() {
        if (!isInitOK()) return;
        Preference keepPref = pfc.findPreference(specPref);
        if (keepPref == null || RV == null) { return;}
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

    // Obtain the parent of a Preference [ PreferenceScreen OR PreferenceCategory ]
    private PreferenceGroup getParent(Preference pref) {
        return getParent(pfc.getPreferenceScreen(), pref);
    }
    private PreferenceGroup getParent(PreferenceGroup root, Preference pref) {
        for (int i = 0; i < root.getPreferenceCount(); i++)  {
            Preference p = root.getPreference(i);
            if (p == pref) return root;
            if (PreferenceGroup.class.isInstance(p)) {
                PreferenceGroup parent = getParent((PreferenceGroup)p, pref);
                if (parent != null) return parent;
            }
        }
        log("Could not find parent for " + pref.getKey());
        return null;
    }

    // ---------------------------------------------------------------------------------------------
    /** Smoothly scroll to specified position at 1/4 speed <br>
     Extremely primitive but OK for short things like Preferences */
    private void scroll(RecyclerView rv, int position) throws IllegalArgumentException {
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
        public void onItemClick(View view, int position);
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
