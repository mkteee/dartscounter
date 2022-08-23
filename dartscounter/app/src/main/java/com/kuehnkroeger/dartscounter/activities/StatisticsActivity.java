package com.kuehnkroeger.dartscounter.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Player;
import com.kuehnkroeger.dartscounter.ui.AveragesFragment;
import com.kuehnkroeger.dartscounter.ui.OtherFragment;
import com.kuehnkroeger.dartscounter.ui.ScoresFragment;

import java.util.List;
import java.util.Locale;

import static com.kuehnkroeger.dartscounter.activities.MainActivity.getLocalResources;

public class StatisticsActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    /** colors for different ui themes */
    private int[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.YELLOW};
    /** id of currently selected {@link Player} */
    private int selectedPlayer;
    /** index of currently selected stat */
    private int selectedStat = 1;

    /** holds all players to choose from */
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);

        //tries to override language configuration for application
        String lang = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language_setting", "en");
        if(lang != null) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            //noinspection deprecation
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Resources localRes = getLocalResources(this, locale);
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(localRes.getString(R.string.app_name));
        }

        spinner = findViewById(R.id.choose_stat);
        spinner.setOnItemSelectedListener(this);

        DartsRepository repo = new DartsRepository(getApplication());
        repo.getAllPlayers().observe(this, new Observer<List<Player>>() {
            @Override
            public void onChanged(List<Player> players) {
                ArrayAdapter<Player> adapter = new ArrayAdapter<>(StatisticsActivity.this, android.R.layout.simple_list_item_1, players);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                //tries to recreate player and stat choice after rotation
                if(savedInstanceState != null) {
                    selectedStat = savedInstanceState.getInt("selectedStat");
                    spinner.setSelection(savedInstanceState.getInt("selectedPosition"), true);
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedStat", selectedStat);
        outState.putInt("selectedPosition", spinner.getSelectedItemPosition());
    }

    /**
     * navigates between different statistics categories
     * @param view button in buttonbar that called the function
     */
    public void navigateStats(View view) {
        Fragment frag;
        switch (view.getId()) {
            case R.id.stat2:
                frag = ScoresFragment.newInstance(selectedPlayer);
                selectedStat = 2;
                break;
            case R.id.stat3:
                frag = OtherFragment.newInstance(selectedPlayer);
                selectedStat = 3;
                break;
            case R.id.stat1:
            default:
                frag = AveragesFragment.newInstance(selectedPlayer);
                selectedStat = 1;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.show_stat, frag).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //update ui theme if there has been a change in preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = preferences.getString("color_setting", "0");
        if(color != null) {
            Toolbar toolbar = findViewById(R.id.statistics_toolbar);
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

    /** listens for selection in player spinner */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedPlayer = ((Player)adapterView.getItemAtPosition(i)).id;

        Fragment frag;
        if(selectedStat == 1)
            frag = AveragesFragment.newInstance(selectedPlayer);
        else if(selectedStat == 2)
            frag = ScoresFragment.newInstance(selectedPlayer);
        else
            frag = OtherFragment.newInstance(selectedPlayer);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.show_stat, frag)
                .commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
