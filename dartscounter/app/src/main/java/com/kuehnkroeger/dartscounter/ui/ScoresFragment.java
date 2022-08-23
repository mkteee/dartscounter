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
import com.kuehnkroeger.dartscounter.database.Statistics;

import java.util.List;

/**
 * Fragment to show statistics of category 'scores', uses database to get stats
 */
public class ScoresFragment extends Fragment {

    /** repository for database calls */
    private DartsRepository repo;

    /**
     * creates a new instance of this dialog
     * @param selectedPlayer id of player to show statistics for
     * @return instance to display
     */
    public static ScoresFragment newInstance(int selectedPlayer) {
        ScoresFragment frag = new ScoresFragment();
        Bundle args = new Bundle(1);
        args.putInt("id", selectedPlayer);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scores, container, false);
    }

    /** populate {@link TextView}s with statistics data from database*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            int id = args.getInt("id");
            List<Match> matches = repo.getMatchesByPlayerId(id);

            int highestScore = 0, amountHighestScore = 0, highestFinish = 0, amountHighestFinish = 0,
                    perfectGames = 0, score70 = 0, score90 = 0, score130 = 0, tonOuts = 0;

            for(Match match: matches) {
                Statistics stats = match.statistics;
                int mode = 0, amountDarts = 0, sumDarts = 0;
                if(id == match.player1.id) {
                    mode = stats.pointsPlayer1;
                    amountDarts = stats.amountDart1Player1 + stats.amountDart2Player1
                            + stats.amountDart3Player1;

                    if(stats.highestScorePlayer1 > highestScore) {
                        highestScore = stats.highestScorePlayer1;
                        amountHighestScore = 1;
                    }
                    else if(stats.highestScorePlayer1 == highestScore)
                        amountHighestScore++;
                    if(stats.finishPlayer1 > highestFinish) {
                        highestFinish = stats.finishPlayer1;
                        amountHighestFinish = 1;
                    }
                    else if(stats.finishPlayer1 == highestFinish)
                        amountHighestFinish++;
                    if(stats.finishPlayer1 >= 100)
                        tonOuts++;
                    score70 += stats.plus70Player1;
                    score90 += stats.plus90Player1;
                    score130 += stats.plus130Player1;
                }
                else if(id == match.player2.id) {
                    mode = stats.pointsPlayer2;
                    amountDarts = stats.amountDart1Player2 + stats.amountDart2Player2
                            + stats.amountDart3Player2;

                    if(stats.highestScorePlayer2 > highestScore) {
                        highestScore = stats.highestScorePlayer2;
                        amountHighestScore = 1;
                    }
                    else if(stats.highestScorePlayer2 == highestScore)
                        amountHighestScore++;
                    if(stats.finishPlayer2 > highestFinish) {
                        highestFinish = stats.finishPlayer2;
                        amountHighestFinish = 1;
                    }
                    else if(stats.finishPlayer2 == highestFinish)
                        amountHighestFinish++;
                    if(stats.finishPlayer2 >= 100)
                        tonOuts++;
                    score70 += stats.plus70Player2;
                    score90 += stats.plus90Player2;
                    score130 += stats.plus130Player2;
                }

                if((mode == 301 && amountDarts == 6) || (mode == 501 && amountDarts == 9) ||
                        (mode == 701 && amountDarts == 12) || (mode == 1001 && amountDarts == 17))
                    perfectGames++;
            }

            TextView tHighestScore = view.findViewById(R.id.highest_score);
            tHighestScore.setText(String.valueOf(highestScore).concat(" (x")
                    .concat(String.valueOf(amountHighestScore)).concat(")"));

            TextView tHighestFinish = view.findViewById(R.id.highest_finish);
            tHighestFinish.setText(String.valueOf(highestFinish).concat(" (x")
                    .concat(String.valueOf(amountHighestFinish)).concat(")"));

            TextView tPerfectGames = view.findViewById(R.id.perfect_game);
            tPerfectGames.setText(String.valueOf(perfectGames));

            TextView tScore70 = view.findViewById(R.id.score_70);
            tScore70.setText(String.valueOf(score70));

            TextView tScore90 = view.findViewById(R.id.score_90);
            tScore90.setText(String.valueOf(score90));

            TextView tScore130 = view.findViewById(R.id.score_130);
            tScore130.setText(String.valueOf(score130));

            TextView tTonOuts = view.findViewById(R.id.ton_outs);
            tTonOuts.setText(String.valueOf(tonOuts));
        }
    }

    /** fetch instance of database repo with context of application*/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repo = new DartsRepository(((StatisticsActivity)context).getApplication());
    }
}
