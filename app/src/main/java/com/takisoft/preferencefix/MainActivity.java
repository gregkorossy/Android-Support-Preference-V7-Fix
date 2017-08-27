package com.takisoft.preferencefix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(this);

        try {
            Date date = dateFormat.parse("07:23");
            Log.d("MainActivity", "date: " + date.toString());
        } catch (ParseException e) {
            Log.w("MainActivity", "Parse 07:23", e);
        }

        try {
            Date date = dateFormat.parse("07:23 am");
            Log.d("MainActivity", "date: " + date.toString());
        } catch (ParseException e) {
            Log.w("MainActivity", "Parse 07:23 am", e);
        }

        try {
            Date date = dateFormat.parse("15:56");
            Log.d("MainActivity", "date: " + date.toString());
        } catch (ParseException e) {
            Log.w("MainActivity", "Parse 15:56", e);
        }

        try {
            Date date = dateFormat.parse("03:56 pm");
            Log.d("MainActivity", "date: " + date.toString());
        } catch (ParseException e) {
            Log.w("MainActivity", "Parse 3:56 pm", e);
        }

        try {
            Date date = dateFormat.parse("٢٣:٥٦");
            Log.d("MainActivity", "date: " + date.toString());
        } catch (ParseException e) {
            Log.w("MainActivity", "Parse ٢٣:٥٦", e);
        }

        SimpleDateFormat sdf_US = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat sdf_AR = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("ar"));

        Log.d("MainActivity", "Formatted (US): " + sdf_US.format(new Date()));
        Log.d("MainActivity", "Formatted (AR): " + sdf_AR.format(new Date()));

        if (savedInstanceState == null) {
            MyPreferenceFragment fragment = new MyPreferenceFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        MyPreferenceFragment fragment = new MyPreferenceFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();

        return true;
    }
}
