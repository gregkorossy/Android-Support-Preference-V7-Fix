package com.takisoft.preferencefix;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompatFix;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewFix;
import android.view.LayoutInflater;
import android.view.ViewGroup;


/**
 * Creates custom RecyclerView with scrollbar and makes scrollbar visible when fragment resume
 */
public abstract class PreferenceFragmentCompatFixScrollBar extends PreferenceFragmentCompatFix {

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView rv = getListView();
        if (rv instanceof RecyclerViewFix) {
            ((RecyclerViewFix) rv).forcedShowScrollbar();
        }
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater
                .inflate(R.layout.preference_recyclerview_custom, parent, false);

        recyclerView.setLayoutManager(onCreateLayoutManager());

        return recyclerView;
    }
}
