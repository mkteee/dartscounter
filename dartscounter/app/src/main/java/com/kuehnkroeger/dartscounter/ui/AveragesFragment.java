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
import androidx.preference.PreferenceManager;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.activities.StatisticsActivity;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Match;
import com.kuehnkroeger.dartscounter.database.Statistics;

import java.util.List;
import java.util.Locale;

/**
 * Fragment to show statistics of category 'averages', uses database to get stats
 */
public class AveragesFragment extends Fragment {

    /** repository for database calls */
    private DartsRepository repo;

    /**
     * creates a new instance of this dialog
     * @param selectedPlayer id of player to show statistics for
     * @return instance to display
     */
    public static AveragesFragment newInstance(int selectedPlayer) {
        AveragesFragment frag = new AveragesFragment();
        Bundle args = new Bundle(1);
        args.putInt("id", selectedPlayer);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_averages, container, false);
    }

    /** populate {@link TextView}s with statistics data from database*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            int id = args.getInt("id");
            List<Match> matches = repo.getMatchesByPlayerId(id);

            int sumDart1 = 0,sumDart2 = 0, sumDart3 = 0,
                    amountDart1 = 0, amountDart2 = 0, amountDart3 = 0;

            for(Match match: matches) {
                Statistics stats = match.statistics;
                if(id == match.player1.id) {
                    sumDart1 += stats.sumDart1Player1;
                    sumDart2 += stats.sumDart2Player1;
                    sumDart3 += stats.sumDart3Player1;
                    amountDart1 += stats.amountDart1Player1;
                    amountDart2 += stats.amountDart2Player1;
                    amountDart3 += stats.amountDart3Player1;
                }
                else if(id == match.player2.id) {
                    sumDart1 += stats.sumDart1Player2;
                    sumDart2 += stats.sumDart2Player2;
                    sumDart3 += stats.sumDart3Player2;
                    amountDart1 += stats.amountDart1Player2;
                    amountDart2 += stats.amountDart2Player2;
                    amountDart3 += stats.amountDart3Player2;
                }
            }

            Locale l = Locale.getDefault();
            if(getContext() != null) {
                String lang = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString("language_setting", "en");
                if(lang != null)
                    l = new Locale(lang);
            }

            TextView globalAverage = view.findViewById(R.id.global_average);
            if(amountDart1+amountDart2+amountDart3 > 0)
                globalAverage.setText(String.format(l, "%.2f",3*(sumDart1+sumDart2+sumDart3)/
                        (float)(amountDart1+amountDart2+amountDart3)));
            else
                globalAverage.setText("N/A");

            TextView averageD1 = view.findViewById(R.id.average_d1);
            if(amountDart1 > 0)
                averageD1.setText(String.format(l, "%.2f", sumDart1/(float)amountDart1));
            else
                averageD1.setText("N/A");

            TextView averageD2 = view.findViewById(R.id.average_d2);
            if(amountDart2 > 0)
                averageD2.setText(String.format(l, "%.2f", sumDart2/(float)amountDart2));
            else
                averageD2.setText("N/A");

            TextView averageD3 = view.findViewById(R.id.average_d3);
            if(amountDart3 > 0)
                averageD3.setText(String.format(l, "%.2f", sumDart3/(float)amountDart3));
            else
                averageD3.setText("N/A");
        }

    }

    /** fetch instance of database repo with context of application*/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repo = new DartsRepository(((StatisticsActivity)context).getApplication());
    }
}
