package com.kuehnkroeger.dartscounter.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.kuehnkroeger.dartscounter.CheckoutMap;
import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Match;
import com.kuehnkroeger.dartscounter.ui.DartboardDialogFragment;
import com.kuehnkroeger.dartscounter.ui.GameFinishedFragment;
import com.kuehnkroeger.dartscounter.ui.ScoreGestureListener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static com.kuehnkroeger.dartscounter.activities.MainActivity.getLocalResources;

public class ScoreboardActivity extends AppCompatActivity {

    /** colors for different ui themes */
    private int[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.YELLOW};

    private int score1;
    private int score2;
    private String player1;
    private String player2;
    private int player1_id;
    private int player2_id;
    private StringBuilder match_data = new StringBuilder();
    private int GAME_MODE;
    private int turn = 1;                       //1 is left, 2 is right
    private int turnCounter = 0;                //is used to check if 3 darts were thrown
    private int undoCounterLeft = 0;            //is used for the undo logic
    private int undoCounterRight = 0;
    private ArrayList<Integer> pts_history1 = new ArrayList<>();
    private ArrayList<Integer> pts_history2 = new ArrayList<>();
    private ArrayList<Integer> throw_history1 = new ArrayList<>();
    private ArrayList<Integer> throw_history2 = new ArrayList<>();
    private Match match;

    /** standard clicklistener to show {@link DartboardDialogFragment} */
    private View.OnClickListener finishListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int[] array_int = (int[]) view.getTag();
            Integer[] array_integer = new Integer[array_int.length];

            for (int i=0; i<array_int.length; i++) {
                array_integer[i] = array_int[i];
            }
            //Integer[] params = Arrays.stream((int[]) view.getTag() ).boxed().toArray( Integer[]::new );
            DartboardDialogFragment dialog = DartboardDialogFragment.newInstance(array_integer);
            dialog.show(getSupportFragmentManager(), "displayFinish");
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Toolbar toolbar = findViewById(R.id.scoreboard_toolbar);
        setSupportActionBar(toolbar);

        String lang = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language_setting", "en");
        if(lang != null) {
            Resources localRes = getLocalResources(this, new Locale(lang));
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(localRes.getString(R.string.app_name));
        }

        //-------- initializing Views --------
        final TextView scoreLeft = findViewById(R.id.score_left);
        final TextView scoreRight = findViewById(R.id.score_right);

        final TextView finishLeft = findViewById(R.id.finish_left);
        finishLeft.setOnClickListener(finishListener);
        //set text in finishLeft when finish is possible
        final TextView finishRight = findViewById(R.id.finish_right);
        finishRight.setOnClickListener(finishListener);
        //set text in finishRight when finish is possible

        Button[] buttons = new Button[22];

        ConstraintLayout layout = findViewById(R.id.input);

        for(int i = 22; i >= 1; i--) {
            Button b = new Button(this, null, 0, R.style.Widget_AppCompat_Button_Borderless);
            b.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
            if(i == 2) {
                b.setText(R.string.bull);
                b.setTag(25);
            }
            else if(i == 1) {
                b.setText(R.string.miss);
                b.setTag(0);
            }
            else {
                b.setText(String.valueOf(i-2));
                b.setTag(i-2);
            }
            b.setId(View.generateViewId());
            //access score with (int)view.getTag()
            b.setOnTouchListener(new ScoreGestureListener(this,
                    Resources.getSystem().getDisplayMetrics().heightPixels/10) {
                @Override
                public void onClick(View view) {
                    //logic for single hit
                    if(turn == 1){
                        scoreChange((int)view.getTag()+100, scoreLeft);
                    }else{
                        scoreChange((int)view.getTag()+100, scoreRight);
                    }
                }

                @Override
                public void onSwipeDown(View view) {
                    //logic for triple hit
                    if(turn == 1){
                        scoreChange((int)view.getTag()+300, scoreLeft);
                    }else{
                        scoreChange((int)view.getTag()+300, scoreRight);
                    }
                }

                @Override
                public void onSwipeUp(View view) {
                    //logic for double hit
                    if(turn == 1){
                        scoreChange((int)view.getTag()+200, scoreLeft);
                    }else{
                        scoreChange((int)view.getTag()+200, scoreRight);
                    }
                }
            });
            buttons[22-i] = b;
        }

        //create score button grid
        ConstraintLayout.LayoutParams params;

        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 4; x++) {
                Button b = buttons[y*4+x];
                params = new ConstraintLayout.LayoutParams(0,0);
                if(x == 0)
                    params.startToStart = layout.getId();
                else if(x == 3)
                    params.endToEnd = layout.getId();

                if(y == 0)
                    params.topToTop = layout.getId();
                else if(y == 4)
                    params.bottomToTop = buttons[20].getId();
                    //params.bottomToBottom = layout.getId();

                if(x != 3)
                    params.endToStart = buttons[y*4+(x+1)].getId();
                if(x != 0)
                    params.startToEnd = buttons[y*4+(x-1)].getId();

                if(y != 0)
                    params.topToBottom = buttons[(y-1)*4+x].getId();
                if(y != 4)
                    params.bottomToTop = buttons[(y+1)*4+x].getId();
                b.setLayoutParams(params);
                layout.addView(b);
            }
        }


        params = new ConstraintLayout.LayoutParams(0, 0);
        params.startToStart = layout.getId();
        params.endToStart = buttons[21].getId();
        params.bottomToBottom = layout.getId();
        params.topToBottom = buttons[16].getId();
        buttons[20].setLayoutParams(params);

        params = new ConstraintLayout.LayoutParams(0, 0);
        params.endToEnd = layout.getId();
        params.startToEnd = buttons[20].getId();
        params.bottomToBottom = layout.getId();
        params.topToBottom = buttons[16].getId();
        buttons[21].setLayoutParams(params);

        layout.addView(buttons[20]);
        layout.addView(buttons[21]);

        //-------- initializing logic --------

        Bundle args = getIntent().getExtras();
        if(args != null) {
            score1 = args.getInt("points_left", 501);
            score2 = args.getInt("points_right", 501);

            //for single out etc. later.. 1 for now
            GAME_MODE = args.getInt("x01_mode", 0);

            scoreLeft.setText(String.valueOf(score1));
            scoreRight.setText(String.valueOf(score2));
            pts_history1.add(score1);
            pts_history2.add(score2);

            player1 = args.getString("player1", getString(R.string.player1));
            player2 = args.getString("player2", getString(R.string.player2));
            player1_id = args.getInt("player1_id", -1);
            player2_id = args.getInt("player2_id", -1);

            //Initializes the names and GameModes, segregated by ";"
            //Data about match (player, mode) will be segregated from throws with "."
            //Turns are segregated by ";" as well. Throws by ","
            //Last dart (if legit finish) will append with an "!"
            match_data.append(score1).append(";").append(score2).append(";").append(GAME_MODE).append(";")
                    .append(player1).append(";").append(player2).append(".");

            TextView playerLeft = findViewById(R.id.player_left);
            playerLeft.setText(player1);
            TextView playerRight = findViewById(R.id.player_right);
            playerRight.setText(player2);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //update ui theme if there has been a change in preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = preferences.getString("color_setting", "0");
        if(color != null) {
            Toolbar toolbar = findViewById(R.id.scoreboard_toolbar);
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

    /**
     * overrides function on pressing the back button to show a confirmation dialog before
     * ending the match
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_match);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ScoreboardActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scoreboard_toolbar, menu);

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

    /**
     * Undo button to undo throws.
     * @param item item clicked
     * @return super of item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.undo) {
            if (turn == 1) {
                undoLeft();
            } else {
                undoRight();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Logic after a score change happened.
     * @param pts_scored points for current throw
     * @param side side which is throwing currently
     */
    public void scoreChange(int pts_scored, TextView side){
        int new_score;
        int throw_value = pts_scored;
        int multiplier = pts_scored/100;
        pts_scored = pts_scored % 100;

        if(undoCounterLeft > 0 || undoCounterRight > 0){
            updatePtsHistory();
        }

        if(GAME_MODE==3 && (multiplier != 2)){
            match_data.append(pts_scored+multiplier*100).append(",");
            if(turn==1){
                if(pts_history1.get(0).equals(pts_history1.get(pts_history1.size() - 1))){
                    pts_history1.add(score1);
                    throw_history1.add(throw_value);
                    switchPlayer();
                    return;
                }
            }else{
                if(pts_history2.get(0).equals(pts_history2.get(pts_history2.size() - 1))){
                    pts_history2.add(score2);
                    throw_history2.add(throw_value);
                    switchPlayer();
                    return;
                }
            }
        }
        if(GAME_MODE==4 && (multiplier != 3)){
            match_data.append(pts_scored+multiplier*100).append(",");
            if(turn==1){
                if(pts_history1.get(0).equals(pts_history1.get(pts_history1.size() - 1))){
                    pts_history1.add(score1);
                    throw_history1.add(throw_value);
                    switchPlayer();
                    return;
                }
            }else{
                if(pts_history2.get(0).equals(pts_history2.get(pts_history2.size() - 1))){
                    pts_history2.add(score2);
                    throw_history2.add(throw_value);
                    switchPlayer();
                    return;
                }
            }
        }

        if(turn == 1){
            new_score = score1 - pts_scored * multiplier;
        }else{
            new_score = score2 - pts_scored * multiplier;
        }

        if(new_score > 1){
            if(turn == 1){
                score1 = new_score;
                pts_history1.add(score1);
                throw_history1.add(throw_value);
            }else{
                score2 = new_score;
                pts_history2.add(score2);
                throw_history2.add(throw_value);
            }
            side.setText(String.valueOf(new_score));
            match_data.append(pts_scored+multiplier*100).append(",");
            turnCounter++;

        }else if(new_score == 1 || new_score < 0){
            if(turn == 1){
                score1 = pts_history1.get(pts_history1.size()-1-(turnCounter % 3));
                side.setText(String.valueOf(score1));
                pts_history1.add(score1);
                throw_history1.add(throw_value);
            }else{
                score2 = pts_history2.get(pts_history2.size()-1-(turnCounter % 3));
                side.setText(String.valueOf(score2));
                pts_history2.add(score2);
                throw_history2.add(throw_value);
            }
            match_data.append(pts_scored+multiplier*100);
            turnCounter = 0;

        }else checkFinish(pts_scored, multiplier);

        updateScoresThrown(pts_scored, multiplier); //update the middle text for last throws
        updateFinishSuggestion(0);                  //update finishing suggestion for current player

        if(turnCounter % 3 == 0){
            switchPlayer();
        }

    }

    /**
     * Helping Method to switch active player
     */
    public void switchPlayer(){
        if(match_data.charAt(match_data.length()- 1) == ','){
            match_data.setLength(match_data.length()-1);
        }
        match_data.append(";");

        RadioButton left = findViewById(R.id.ind_left);
        RadioButton right = findViewById(R.id.ind_right);
        if(turn == 1){
            left.setChecked(false);
            right.setChecked(true);
            pts_history1.add(-1);
            throw_history1.add(-1);
            Collections.swap(pts_history1, pts_history1.size()-1, pts_history1.size()-2);
            turn = 2;
        }else{
            right.setChecked(false);
            left.setChecked(true);
            pts_history2.add(-1);
            throw_history2.add(-1);
            Collections.swap(pts_history2, pts_history2.size()-1, pts_history2.size()-2);
            turn = 1;
        }
    }

    /**
     * Updates the throw history located between the players and shows the current turns throw
     * values or the last turns values if no darts were thrown so far
     * @param throw_value blank value of the current throw
     * @param multiplier multiplier of the throw
     */
    public void updateScoresThrown(int throw_value, int multiplier){
        TextView scoresThrown = findViewById(R.id.scores_thrown);
        if(turnCounter % 3 == 1) scoresThrown.setText(""); //if it was a switch, delete history

        String curr_scores_thrown = scoresThrown.getText().toString();
        String added_string = "";

        switch (multiplier) {
            case 1:
                added_string += "S";
                break;
            case 2:
                added_string += "D";
                break;
            default:
                added_string += "T";
                break;
        }
        added_string += String.valueOf(throw_value);
        curr_scores_thrown += " "+added_string;
        scoresThrown.setText(curr_scores_thrown);
    }

    /**
     * Helping method to update the suggestions for the current player. Uses the CheckoutMap.
     * Passes the current points and darts_left to the according method based on the GAME_MODE
     * and retrieves an array of the suggested throws, which is converted into text and added to
     * the TextView.
     * @param offset used only if called from an undo
     */
    public void updateFinishSuggestion(int offset){
        if(turn == 1){
            TextView finishLeft = findViewById(R.id.finish_left);
            StringBuilder finish_string = new StringBuilder();
            String temp_string;
            int[] finishes = {};
            //use pts_history1 instead of score1 because undo is easier to handle that way
            int currentScore = pts_history1.get(pts_history1.size()-1-offset);

            //everything is double-out
            if(GAME_MODE == 0 || GAME_MODE == 3 || GAME_MODE == 4) {
                if ( currentScore <= 170 && currentScore > 0) {
                    finishes = CheckoutMap.doubleOut(currentScore, 3 - (turnCounter % 3));
                }
            }
            if(GAME_MODE == 1){     //single-out
                if(currentScore <= 180 && currentScore > 0){
                    finishes = CheckoutMap.singleOut(currentScore, 3- (turnCounter % 3));
                }
            }if(GAME_MODE == 2){                  //master-out
                if(currentScore <= 180 && currentScore > 0){
                    finishes = CheckoutMap.masterOut(currentScore, 3- (turnCounter % 3));
                }
            }
            for (int value : finishes) {
                if (value > 0) {
                    int f_multiplier = value / 100;
                    value = value % 100;
                    switch (f_multiplier) {
                        case 3:
                            temp_string = "T";
                            break;
                        case 2:
                            temp_string = "D";
                            break;
                        default:
                            temp_string = "S";
                    }
                    temp_string += Integer.toString(value);
                    finish_string.append(" ").append(temp_string);
                }
            }
            finishLeft.setText(finish_string.toString());
            finishLeft.setTag(finishes);
        }else{
            TextView finishRight = findViewById(R.id.finish_right);
            StringBuilder finish_string = new StringBuilder();
            String temp_string;
            int[] finishes = {};
            int currentScore = pts_history2.get(pts_history2.size()-1-offset);

            //everything is double-out
            if(GAME_MODE == 0 || GAME_MODE == 3 || GAME_MODE == 4) {
                if (currentScore <= 170 && currentScore > 0) {
                    finishes = CheckoutMap.doubleOut(currentScore, 3 - (turnCounter % 3));
                }
            }
            if(GAME_MODE == 1){     //single-out
                if(currentScore <= 180 && currentScore > 0){
                    finishes = CheckoutMap.singleOut(currentScore, 3- (turnCounter % 3));
                }
            }else{                  //master-out
                if(currentScore <= 180 && currentScore > 0){
                    finishes = CheckoutMap.masterOut(currentScore, 3- (turnCounter % 3));
                }
            }
            for (int value : finishes) {
                if (value > 0) {
                    int f_multiplier = value / 100;
                    value = value % 100;
                    switch (f_multiplier) {
                        case 3:
                            temp_string = "T";
                            break;
                        case 2:
                            temp_string = "D";
                            break;
                        default:
                            temp_string = "S";
                    }
                    temp_string += Integer.toString(value);
                    finish_string.append(" ").append(temp_string);
                }
            }
            finishRight.setText(finish_string.toString());
            finishRight.setTag(finishes);
        }
    }

    /**
     * Will be called if the score has reached 0 for one player. Will check if the finish
     * was legit, based on the game mode and scored points. Will also reset the points if
     * it was a bust.
     * @param score score which was throws
     * @param multiplier multiplier of the throw
     */
    public void checkFinish(int score, int multiplier){
        TextView left_side = findViewById(R.id.score_left);
        TextView right_side = findViewById(R.id.score_right);
        switch (GAME_MODE){
            //double-out, double-in, triple-in
            case 0:
            case 3:
            case 4:
                if(multiplier == 2){
                    match_data.append(score+multiplier*100).append("!");
                    if(turn == 1){
                        score1 = score1 - score*multiplier;
                        left_side.setText(String.valueOf(score1));
                        pts_history1.add(score1);
                        throw_history1.add(score+(multiplier*100));
                        showGameFinishedDialog(player1, true);
                    }else{
                        score2 = score2 - score*multiplier;
                        right_side.setText(String.valueOf(score2));
                        pts_history2.add(score2);
                        throw_history2.add(score+(multiplier*100));
                        showGameFinishedDialog(player2, false);
                    }
                }else{
                    if(turn == 1){
                        score1 = pts_history1.get(pts_history1.size()-1-(turnCounter % 3));
                        left_side.setText(String.valueOf(score1));
                        pts_history1.add(score1);
                        throw_history1.add(score+(multiplier*100));
                    }else{
                        score2 = pts_history2.get(pts_history2.size()-1-(turnCounter % 3));
                        right_side.setText(String.valueOf(score2));
                        pts_history2.add(score2);
                        throw_history2.add(score+(multiplier*100));
                    }
                    match_data.append(score+multiplier*100);
                    turnCounter = 0;
                }
                break;
            //single-out
            case 1:
                match_data.append(score+multiplier*100).append("!");
                if(turn == 1){
                    score1 = score1 - score*multiplier;
                    left_side.setText(String.valueOf(score1));
                    pts_history1.add(score1);
                    throw_history1.add(score+(multiplier*100));
                    showGameFinishedDialog(player1, true);
                }else{
                    score2 = score2 - score*multiplier;
                    right_side.setText(String.valueOf(score2));
                    pts_history2.add(score2);
                    throw_history2.add(score+(multiplier*100));
                    showGameFinishedDialog(player2, false);
                }
                break;
            //master-out
            case 2:
                if(multiplier > 1){
                    match_data.append(score+multiplier*100).append("!");
                    if(turn == 1){
                        score1 = score1 - score*multiplier;
                        left_side.setText(String.valueOf(score1));
                        pts_history1.add(score1);
                        throw_history2.add(score+(multiplier*100));
                        showGameFinishedDialog(player1, true);
                    }else{
                        score2 = score2 - score*multiplier;
                        right_side.setText(String.valueOf(score2));
                        pts_history2.add(score2);
                        throw_history2.add(score+(multiplier*100));
                        showGameFinishedDialog(player2, false);
                    }
                }else{
                    if(turn == 1){
                        score1 = pts_history1.get(pts_history1.size()-1-(turnCounter % 3));
                        left_side.setText(String.valueOf(score1));
                        pts_history1.add(score1);
                        throw_history1.add(score+(multiplier*100));
                    }else{
                        score2 = pts_history2.get(pts_history2.size()-1-(turnCounter % 3));
                        right_side.setText(String.valueOf(score2));
                        pts_history2.add(score2);
                        throw_history2.add(score+(multiplier*100));
                    }
                    match_data.append(score+multiplier*100);
                    turnCounter = 0;
                }
                break;
        }
    }

    /**
     * UndoLogic for the left side. Runs back in the point history and only counts the amount
     * of undos accordingly.
     */
    public void undoLeft(){
        TextView left_side = findViewById(R.id.score_left);
        TextView right_side = findViewById(R.id.score_right);
        RadioButton left = findViewById(R.id.ind_left);
        RadioButton right = findViewById(R.id.ind_right);
        TextView scores_thrown = findViewById(R.id.scores_thrown);
        String scores_thrown_str = scores_thrown.getText().toString();

        if(!(scores_thrown_str.equals(" "))){
            do{
                scores_thrown_str = scores_thrown_str.substring(0, scores_thrown_str.length()-1);
            }while(scores_thrown_str.charAt(scores_thrown_str.length() - 1) != ' ');
            scores_thrown.setText(scores_thrown_str);
        }

        //start of the game, no undo possible
        if(pts_history1.size()-2-undoCounterLeft < 0) return;

        //if -1 (indicates a turn switch), undo for the other side
        if(pts_history1.get(pts_history1.size()-2-undoCounterLeft) == -1){
            turn = 2;
            left.setChecked(false);
            right.setChecked(true);
            right_side.setText(String.valueOf(pts_history2.get(pts_history2.size()-3-undoCounterRight)));
            undoCounterRight += 2; //skip over trailing end
            turnCounter--;
            updateFinishSuggestion(undoCounterRight+1);

        //regular undo after throw 1 or 2
        }else{
            left_side.setText(String.valueOf(pts_history1.get(pts_history1.size()-2-undoCounterLeft)));
            undoCounterLeft++;
            turnCounter--;
            updateFinishSuggestion(undoCounterLeft);
        }

    }

    /**
     * UndoLogic for the right side.
     */
    public void undoRight(){
        TextView right_side = findViewById(R.id.score_right);
        TextView left_side = findViewById(R.id.score_left);
        RadioButton left = findViewById(R.id.ind_left);
        RadioButton right = findViewById(R.id.ind_right);
        TextView scores_thrown = findViewById(R.id.scores_thrown);
        String scores_thrown_str = scores_thrown.getText().toString();

        if(!(scores_thrown_str.equals(" "))){
            do{
                scores_thrown_str = scores_thrown_str.substring(0, scores_thrown_str.length()-1);
            }while(scores_thrown_str.charAt(scores_thrown_str.length() - 1) != ' ');
            scores_thrown.setText(scores_thrown_str);
        }

        //if player2 array is empty, means player1 has thrown already, undo is possible
        if(pts_history2.size()-2-undoCounterRight < 0){
            turn = 1;
            right.setChecked(false);
            left.setChecked(true);
            left_side.setText(String.valueOf(pts_history1.get(pts_history1.size()-3-undoCounterLeft)));
            undoCounterLeft += 2;
            turnCounter--;
            updateFinishSuggestion(undoCounterLeft+1);
        }

        //if -1 (indicates a turn switch), undo for the other side
        else if(pts_history2.get(pts_history2.size()-2-undoCounterRight) == -1){
            turn = 1;
            right.setChecked(false);
            left.setChecked(true);
            left_side.setText(String.valueOf(pts_history1.get(pts_history1.size()-3-undoCounterLeft)));
            undoCounterLeft += 2; //skip over trailing end
            turnCounter--;
            updateFinishSuggestion(undoCounterLeft+1);

        //regular undo after throw 1 or 2
        }else{
            right_side.setText(String.valueOf(pts_history2.get(pts_history2.size()-2-undoCounterRight)));
            undoCounterRight++;
            turnCounter--;
            updateFinishSuggestion(undoCounterRight);
        }
    }

    /**
     * Is called after the game continues if undos occurred. Will calculate how to handle the
     * pts_history, throw_history and match_data so the logic is still robust.
     */
    public void updatePtsHistory(){
        score1 = pts_history1.get(pts_history1.size()-1-undoCounterLeft);
        while(undoCounterLeft > 0){
            pts_history1.remove(pts_history1.size()-1);
            throw_history1.remove(throw_history1.size()-1);

            if(match_data.charAt(match_data.length()-1) != '.'){
                if (match_data.charAt(match_data.length() - 1) == ';') {
                    match_data.setLength(match_data.length() - 1);
                } else {
                    if (match_data.charAt(match_data.length() - 1) == ',') {
                        match_data.setLength(match_data.length() - 1);
                    }
                    while ((match_data.charAt(match_data.length() - 1) == ',') ==
                            (match_data.charAt(match_data.length() - 1) == ';')) {
                        if(match_data.charAt(match_data.length()-1) == '.'){
                            break;
                        }
                        match_data.setLength(match_data.length() - 1);
                    }
                }
                undoCounterLeft--;
            }
        }
        score2 = pts_history2.get(pts_history2.size()-1-undoCounterRight);
        while(undoCounterRight > 0){
            pts_history2.remove(pts_history2.size()-1);
            throw_history2.remove(throw_history2.size()-1);

            if(match_data.charAt(match_data.length()-1) != '.') {
                if (match_data.charAt(match_data.length() - 1) == ';') {
                    match_data.setLength(match_data.length() - 1);
                } else {
                    if (match_data.charAt(match_data.length() - 1) == ',') {
                        match_data.setLength(match_data.length() - 1);
                    }
                    while ((match_data.charAt(match_data.length() - 1) == ',') ==
                            (match_data.charAt(match_data.length() - 1) == ';')) {
                        if(match_data.charAt(match_data.length()-1) == '.'){
                            break;
                        }
                        match_data.setLength(match_data.length() - 1);
                    }
                }
                undoCounterRight--;
            }
        }
    }

    /**
     * Dialog which will show up if the game is finished.
     * @param winner winner of the match
     * @param bool_winner true if left has won
     */
    private void showGameFinishedDialog(String winner, boolean bool_winner) {
        DartsRepository repo = new DartsRepository(getApplication());
        match = new Match(repo.getPlayerById(player1_id),repo.getPlayerById(player2_id),
                bool_winner, pts_history1, pts_history2, throw_history1, throw_history2);
        match.saveStatisticsFromMatch();
        repo.insert(match);
        FragmentManager fm = getSupportFragmentManager();
        GameFinishedFragment dialog = GameFinishedFragment.newInstance(winner);
        dialog.show(fm, "winner_dialog");
    }

    /**
     * Saves the current match to file storage when corresponding save button has been clicked
     * @param view View that the click originated from
     */
    public void onSaveGameRadioButtonClicked(View view){
        //save only when match data has already been created
        ArrayList<Match> matches = new ArrayList<>();
        //workaround because ObjectOutputStreams cannot be appended to
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
            if(match != null) {
                oos.writeObject(match);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Will put the match_data into an extra to share with an intent.
     * @param view the radioButton
     */
    public void onShareGameButtonClicked(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, match_data.toString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    /**
     * Gets you back to the main menu
     * @param view back button
     */
    public void onBackToMainButtonClicked(View view){
        Intent i = new Intent(ScoreboardActivity.this, MainActivity.class);
        startActivity(i);
    }
}
