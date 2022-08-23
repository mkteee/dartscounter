package com.kuehnkroeger.dartscounter.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.activities.StatisticsActivity;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Match;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment to show statistics of category 'other', uses database to get stats
 */
public class OtherFragment extends Fragment {

    /** repository for database calls */
    private DartsRepository repo;

    /**
     * creates a new instance of this dialog
     * @param selectedPlayer id of player to show statistics for
     * @return instance to display
     */
    public static OtherFragment newInstance(int selectedPlayer) {
        OtherFragment frag = new OtherFragment();
        Bundle args = new Bundle(1);
        args.putInt("id", selectedPlayer);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    /** populate {@link TextView}s with statistics data from database*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            int id = args.getInt("id");
            List<Match> matches = repo.getMatchesByPlayerId(id);

            int gamesPlayed = matches.size(), gamesWon = 0;
            Map<Integer, Integer> countOpponents = new HashMap<>();

            for(Match match: matches) {
                if(match.winner == (id == match.player1.id))
                    gamesWon++;

                if(id == match.player1.id) {
                    if(!countOpponents.containsKey(match.player2.id))
                        countOpponents.put(match.player2.id, 1);
                    else {
                        Integer count = countOpponents.get(match.player2.id);
                        if(count != null)
                            countOpponents.put(match.player2.id, count+1);
                    }
                }
                else if(id == match.player2.id) {
                    if(!countOpponents.containsKey(match.player1.id))
                        countOpponents.put(match.player1.id, 1);
                    else {
                        Integer count = countOpponents.get(match.player1.id);
                        if(count != null)
                            countOpponents.put(match.player1.id, count+1);
                    }
                }


            }

            int max = 0, maxId = -1;
            for(Map.Entry<Integer, Integer> entry: countOpponents.entrySet()) {
                //only update opponent if they are still in the database
                if(entry.getValue() > max && repo.getPlayerById(entry.getKey()) != null) {
                    max = entry.getValue();
                    maxId = entry.getKey();
                }
            }

            TextView tGamesPlayed = view.findViewById(R.id.games_played);
            tGamesPlayed.setText(String.valueOf(gamesPlayed));

            TextView tGamesWon = view.findViewById(R.id.games_won);
            tGamesWon.setText(String.valueOf(gamesWon));

            TextView mostFrequent = view.findViewById(R.id.most_frequent);
            if(maxId != -1)
                mostFrequent.setText(repo.getPlayerById(maxId).name);
            else
                mostFrequent.setText("N/A");
        }
    }

    /** fetch instance of database repo with context of application*/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repo = new DartsRepository(((StatisticsActivity)context).getApplication());
    }
}
