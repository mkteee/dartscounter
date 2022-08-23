package com.kuehnkroeger.dartscounter.activities;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.kuehnkroeger.dartscounter.R;

import java.util.Locale;

import static com.kuehnkroeger.dartscounter.activities.MainActivity.getLocalResources;

public class SettingsActivity extends AppCompatActivity {

    /** colors for different ui themes */
    private int[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.YELLOW};

    /** listener to preference changes to update ui theme correspondingly */
    private final SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
            if(s.equals("color_setting")) {
                String color = preferences.getString("color_setting", "0");
                if(color != null) {
                    Toolbar toolbar = findViewById(R.id.settings_toolbar);
                    if(color.equals("0"))
                        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
                    else
                        toolbar.setBackgroundColor(colors[Integer.parseInt(color)-1]);

                    if(color.equals("3") || color.equals("5"))
                        toolbar.setTitleTextColor(Color.BLACK);
                    else {
                        TypedValue value = new TypedValue();
                        getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
                        toolbar.setTitleTextColor(value.data);
                    }

                    if(color.equals("4")) {
                        for(int i = 0; i < toolbar.getMenu().size(); i++) {
                            TypedValue value = new TypedValue();
                            getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
                            MenuItem item = toolbar.getMenu().getItem(i);
                            item.getIcon().setColorFilter(
                                    new PorterDuffColorFilter(value.data, PorterDuff.Mode.SRC_ATOP));
                        }
                    }
                    else
                        for(int i = 0; i < toolbar.getMenu().size(); i++)
                            toolbar.getMenu().getItem(i).getIcon().clearColorFilter();
                }
            }
            else if(s.equals("language_setting")) {
                String lang = preferences.getString("language_setting", "en");
                String msg = getString(R.string.lang_restart);
                if(lang != null) {
                    Resources localRes = getLocalResources(SettingsActivity.this, new Locale(lang));
                    msg = localRes.getString(R.string.lang_restart);
                }
                Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        String lang = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language_setting", "en");
        if(lang != null) {
            Resources localRes = getLocalResources(this, new Locale(lang));
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(localRes.getString(R.string.app_name));
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update ui theme if there has been a change in preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = preferences.getString("color_setting", "0");
        if(color != null) {
            Toolbar toolbar = findViewById(R.id.settings_toolbar);
            if(color.equals("0"))
                toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
            else
                toolbar.setBackgroundColor(colors[Integer.parseInt(color)-1]);

            if(color.equals("3") || color.equals("5"))
                toolbar.setTitleTextColor(Color.BLACK);
            else {
                TypedValue value = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
                toolbar.setTitleTextColor(value.data);
            }

            if(color.equals("4")) {
                for(int i = 0; i < toolbar.getMenu().size(); i++) {
                    TypedValue value = new TypedValue();
                    getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
                    MenuItem item = toolbar.getMenu().getItem(i);
                    item.getIcon().setColorFilter(
                            new PorterDuffColorFilter(value.data, PorterDuff.Mode.SRC_ATOP));
                }
            }
            else
                for(int i = 0; i < toolbar.getMenu().size(); i++)
                    toolbar.getMenu().getItem(i).getIcon().clearColorFilter();
        }
    }
}
