package com.kuehnkroeger.dartscounter.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.kuehnkroeger.dartscounter.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
