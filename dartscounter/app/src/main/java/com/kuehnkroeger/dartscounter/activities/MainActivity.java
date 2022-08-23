package com.kuehnkroeger.dartscounter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Match;
import com.kuehnkroeger.dartscounter.database.Player;
import com.kuehnkroeger.dartscounter.ui.AddPlayerDialogFragment;
import com.kuehnkroeger.dartscounter.ui.MatchHistoryDialogFragment;
import com.kuehnkroeger.dartscounter.ui.PlayerDropdownTextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AddPlayerDialogFragment.NameEnteredListener {

    /** repository for database calls */
    private DartsRepository repo;
    /** colors for different ui themes */
    private int[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.YELLOW};
    private boolean SHARED_DATA_ADDED = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        }

        setContentView(R.layout.activity_main);

        repo = new DartsRepository(getApplication());

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if(lang != null) {
            Resources localRes = getLocalResources(this, new Locale(lang));
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(localRes.getString(R.string.app_name));
        }

        //initialize and populate all main menu ui elements
        final Spinner pointsLeft = findViewById(R.id.points_left_hc);
        ArrayAdapter<CharSequence> adapterGamemodes = ArrayAdapter
                .createFromResource(this, R.array.gamemodes,
                        android.R.layout.simple_spinner_item);
        adapterGamemodes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointsLeft.setAdapter(adapterGamemodes);

        final Spinner pointsRight = findViewById(R.id.points_right_hc);
        pointsRight.setAdapter(adapterGamemodes);

        final Spinner points = findViewById(R.id.points);
        points.setAdapter(adapterGamemodes);

        final Spinner X01ModeHc = findViewById(R.id.X01_mode_hc);
        ArrayAdapter<CharSequence> adapterX01Modes = ArrayAdapter
                .createFromResource(this, R.array.X01_modes,
                        android.R.layout.simple_spinner_item);
        adapterX01Modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        X01ModeHc.setAdapter(adapterX01Modes);

        final Spinner X01Mode = findViewById(R.id.X01_mode);
        X01Mode.setAdapter(adapterX01Modes);

        final PlayerDropdownTextView selectLeft = findViewById(R.id.select_player_left);
        final PlayerDropdownTextView selectRight = findViewById(R.id.select_player_right);


        repo.getAllPlayers().observe(this, new Observer<List<Player>>() {
            @Override
            public void onChanged(final List<Player> players) {
                selectLeft.setPlayers(MainActivity.this, players);
                selectRight.setPlayers(MainActivity.this, players);
            }
        });


        ImageButton addPlayer = findViewById(R.id.add_player);
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open dialog for entering name of player to be added
                AddPlayerDialogFragment dialog = AddPlayerDialogFragment.newInstance();
                dialog.show(getSupportFragmentManager(), "add_player");
            }
        });

        View.OnTouchListener showDropdown = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((AutoCompleteTextView)view).showDropDown();
                return false;
            }
        };

        selectLeft.setOnTouchListener(showDropdown);
        selectRight.setOnTouchListener(showDropdown);

        selectRight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //clear focus and hide keyboard on done
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus();
                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                }
                return false;
            }
        });

        final CheckBox handicap = findViewById(R.id.handicap);
        handicap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LinearLayout hc1  = findViewById(R.id.mode_description_hc);
                LinearLayout hc2  = findViewById(R.id.mode_selection_hc);
                LinearLayout nhc1 = findViewById(R.id.mode_description);
                LinearLayout nhc2 = findViewById(R.id.mode_selection);

                hc1.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                hc2.setVisibility(b ? View.VISIBLE : View.INVISIBLE);

                nhc1.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
                nhc2.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
            }
        });

        Button play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectLeft.getSelection() != null && selectRight.getSelection() != null) {
                    Intent i = new Intent(MainActivity.this, ScoreboardActivity.class);
                    Bundle args = new Bundle();
                    if(handicap.isChecked()) {
                        args.putInt("points_left", Integer.parseInt(pointsLeft.getSelectedItem().toString()));
                        args.putInt("points_right", Integer.parseInt(pointsRight.getSelectedItem().toString()));
                        args.putInt("x01_mode", X01ModeHc.getSelectedItemPosition());
                    }
                    else {
                        args.putInt("points_left", Integer.parseInt(points.getSelectedItem().toString()));
                        args.putInt("points_right", Integer.parseInt(points.getSelectedItem().toString()));
                        args.putInt("x01_mode", X01Mode.getSelectedItemPosition());
                    }
                    args.putString("player1", (selectLeft).getSelection().name);
                    args.putString("player2", (selectRight).getSelection().name);
                    args.putInt("player1_id", selectLeft.getSelection().id);
                    args.putInt("player2_id", selectRight.getSelection().id);
                    i.putExtras(args);
                    startActivity(i);
                }
                else
                    Toast.makeText(MainActivity.this, R.string.not_enough_players, Toast.LENGTH_SHORT).show();
            }
        });

        //check if data was added and handle accordingly
        if (savedInstanceState != null) {
            SHARED_DATA_ADDED = savedInstanceState.getBoolean("shared_data_added");
        }
        if(!SHARED_DATA_ADDED) handleSharedData();

        loadSavedMatches();
    }

    /**
     * Saves a Boolean to register if data was already shared in this instance of the app.
     * @param outState Bundle to attach info
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("shared_data_added", SHARED_DATA_ADDED);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update ui theme if there has been a change in preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = preferences.getString("color_setting", "0");
        if(color != null) {
            Toolbar toolbar = findViewById(R.id.main_toolbar);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        //update ui theme if there has been a change in preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = preferences.getString("color_setting", "0");

        if(color != null && color.equals("4"))
            for(int i = 0; i < menu.size(); i++) {
                TypedValue value = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
                MenuItem item = menu.getItem(i);
                item.getIcon().setColorFilter(
                        new PorterDuffColorFilter(value.data, PorterDuff.Mode.SRC_ATOP));
            }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //go to settings
                Intent i_settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i_settings);
                break;
            case R.id.stats:
                //go to statistics
                Intent i_stats = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(i_stats);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /** listens for name entered in {@link AddPlayerDialogFragment}. */
    @Override
    public void onNameEntered(String name) {
        //database logic for adding new player
        repo.insert(new Player(name));
    }

    /**
     * Generates localized resources to access e.g. strings in preferred language
     * @param context context of caller
     * @param locale preferred locale/language
     * @return localized resources
     */
    public static Resources getLocalResources(Context context, Locale locale) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config).getResources();
    }

    /**
     * Handles the shared Data, when it is passed. Will extract the info of the shared match
     * and create a new match object to be able to view it in listView.
     */
    public void handleSharedData() {
        Intent i = getIntent();
        String action = i.getAction();
        String type = i.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String match_data = i.getStringExtra(Intent.EXTRA_TEXT);
                String[] split_arr;
                //split the data into match_arr and data_arr (game points and information)
                try {
                    assert match_data != null;
                    split_arr = match_data.split("\\.");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                String[] match_arr;
                String[] data_arr;
                //split those arrays into smaller parts
                try {
                    match_arr = split_arr[1].split(";");
                    data_arr = split_arr[0].split(";");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                int points_player1;
                int points_player2;
                //int game_mode;
                //game mode not needed for now
                String player1;
                String player2;
                boolean winner = true;
                if(data_arr.length == 5){
                    //if appropriate data is passed extract it
                    try {
                        points_player1 = Integer.parseInt(data_arr[0]);
                        points_player2 = Integer.parseInt(data_arr[1]);
                        //game_mode = Integer.parseInt(data_arr[2]);
                        player1 = data_arr[3];
                        player2 = data_arr[4];
                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }
                }else return;

                ArrayList<Integer> pts_history1 = new ArrayList<>();
                ArrayList<Integer> pts_history2 = new ArrayList<>();
                ArrayList<Integer> throw_history1 = new ArrayList<>();
                ArrayList<Integer> throw_history2 = new ArrayList<>();

                pts_history1.add(points_player1);
                pts_history2.add(points_player2);

                int turn;
                int temp_points1 = points_player1;
                int temp_points2 = points_player2;
                int points_before_round; //value to go back to if bust
                String [] throw_values;

                //run through the array, every increment indicates a player switch
                for(turn = 0; turn < (match_arr.length -1); turn++){
                    //split one turn into separate throws
                    try{
                        throw_values = match_arr[turn].split(",");
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                    //initialize current points before throw
                    if(turn % 2 == 0){
                        points_before_round = temp_points1;
                    }else{
                        points_before_round = temp_points2;
                    }

                    //run through the throws of a player of one turn
                    for(int ind = 0; ind < throw_values.length; ind++) {
                        int value = Integer.parseInt(throw_values[ind]);

                        //if it is player1 turn
                        if(turn % 2 == 0){

                            throw_history1.add(value); //add the throw value to the throw_history

                            //if it is not the last throw, subtract throw value regularly
                            if(ind < throw_values.length -1){
                                temp_points1 -= (value/100)*(value%100);
                                pts_history1.add(temp_points1);
                            }else{
                                pts_history1.add(-1); //indicates switch of turns
                                //If pts go below 2 it is a bust, because game is finished after the
                                //for-loop, thus just reset the pts_history. Else continue regularly
                                if((temp_points1 - (value/100)*(value%100)) < 2){
                                    pts_history1.add(points_before_round);
                                    temp_points1 = points_before_round;
                                }else{
                                    temp_points1 -= (value/100)*(value%100);
                                    pts_history1.add(temp_points1);
                                }
                            }
                        }else{
                            //same goes for player2
                            throw_history2.add(value);
                            if(ind < throw_values.length -1){
                                temp_points2 -= (value/100)*(value%100);
                                pts_history2.add(temp_points2);
                            }else{
                                pts_history2.add(-1);
                                if((temp_points2 - (value/100)*(value%100)) < 2){
                                    pts_history2.add(points_before_round);
                                    temp_points2 = points_before_round;
                                }else{
                                    temp_points2 -= (value/100)*(value%100);
                                    pts_history2.add(temp_points2);
                                }
                            }
                        }
                    }
                    //at end of loop for one turn add -1 to throw_history
                    if(turn % 2 == 0){
                        throw_history1.add(-1);
                    }else{
                        throw_history2.add(-1);
                    }
                }
                //we are now at the last turn
                //remove the last character "!"
                match_arr[turn] = match_arr[turn].substring(0, match_arr[turn].length()-1);
                try{
                    throw_values = match_arr[turn].split(",");
                }catch(Exception e){
                    e.printStackTrace();
                    return;
                }

                if(turn % 2 == 1) winner = false; //means player2 has won, default is player1

                for (String throw_value : throw_values) {
                    int value = Integer.parseInt(throw_value);

                    if(turn % 2 == 0){
                        throw_history1.add(value);
                        temp_points1 -= (value/100) * (value%100);
                        pts_history1.add(temp_points1);
                    }else{
                        throw_history2.add(value);
                        temp_points2 -= (value/100) * (value%100);
                        pts_history2.add(temp_points1);
                    }
                }


                Match match = new Match(new Player(player1), new Player(player2),
                        winner, pts_history1, pts_history2, throw_history1, throw_history2);

                ArrayList<Match> matches = new ArrayList<>();
                try (ObjectInputStream ois = new ObjectInputStream(openFileInput(getString(R.string.save_location)))) {
                    Match m;
                    while((m = (Match)ois.readObject()) != null)
                        matches.add(m);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try (ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(getString(R.string.save_location), MODE_PRIVATE))) {
                    for(Match m: matches)
                        oos.writeObject(m);

                    oos.writeObject(match);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        SHARED_DATA_ADDED = true;
    }

    /**
     * populates listview that displays saved matches
     */
    private void loadSavedMatches() {
        final String lang = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language_setting", "en");

        ListView savedGames = findViewById(R.id.saved_games);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        savedGames.setAdapter(adapter);
        //populate listview with games from storage
        final ArrayList<Match> matches = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(openFileInput(getString(R.string.save_location)))) {
            Match m;
            while((m = (Match)ois.readObject()) != null) {
                matches.add(m);
                //show date in local date format
                if(lang != null)
                    adapter.add(m.toString(new Locale(lang)));
                else
                    adapter.add(m.toString(Locale.getDefault()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            adapter.notifyDataSetChanged();
        }

        savedGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MatchHistoryDialogFragment dialog = MatchHistoryDialogFragment
                        .newInstance(matches.get(i));
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        savedGames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.delete_match));
                if(lang != null)
                    builder.setMessage(matches.get(i).toString(new Locale(lang)));
                else
                    builder.setMessage(matches.get(i).toString(Locale.getDefault()));
                final int index = i;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //clear adapter
                        adapter.clear();
                        //remove item
                        matches.remove(index);
                        //repopulate adapter
                        for(Match match: matches) {
                            if(lang != null)
                                adapter.add(match.toString(new Locale(lang)));
                            else
                                adapter.add(match.toString(Locale.getDefault()));
                        }
                        adapter.notifyDataSetChanged();

                        //write updated contents to file
                        try (ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(getString(R.string.save_location), MODE_PRIVATE))) {
                            for(Match m: matches)
                                oos.writeObject(m);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

                return true;
            }
        });
    }
}