package com.kuehnkroeger.dartscounter.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.database.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a {@link DialogFragment} and shows the course of a given {@link Match}
 * Usage -
 *  {@code MatchHistoryDialogFragment dialog = MatchHistoryDialogFragment.newInstance(param);}
 *  {@code dialog.show(getSupportFragmentManager(), tag);}
 */
public class MatchHistoryDialogFragment extends DialogFragment {

    /** context of calling activity */
    private Context context;

    /**
     * creates a new instance of this dialog
     * @param match match for which to show history
     * @return instance to display
     */
    public static MatchHistoryDialogFragment newInstance(Match match) {
        final MatchHistoryDialogFragment dialog = new MatchHistoryDialogFragment();
        final Bundle args = new Bundle(1);
        if(match != null)
            args.putSerializable("match", match);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_history, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            Match match = (Match)args.getSerializable("match");
            if(match != null) {
                ArrayList<Integer> ptsHistoryPlayer1 = match.pts_history1;
                ArrayList<Integer> ptsHistoryPlayer2 = match.pts_history2;
                ArrayList<Integer> throwHistoryPlayer1 = match.throw_history1;
                ArrayList<Integer> throwHistoryPlayer2 = match.throw_history2;

                //represent match history

                List<String[]> pairLeft = new ArrayList<>();
                List<String[]> pairRight = new ArrayList<>();

                //put throw and remaining scores as a pair in each element
                //and -1,-1 as divider after a completed throw

                for(int i = 0; i < ptsHistoryPlayer1.size(); i++) {
                    if(ptsHistoryPlayer1.get(i) != -1)
                        pairLeft.add(new String[]{"", String.valueOf(ptsHistoryPlayer1.get(i))});
                    if(i > 0 && ptsHistoryPlayer1.get(i-1) == -1)
                        pairLeft.add(new String[]{"-1", "-1"});
                }

                for(int i = 0, j = 1; i < throwHistoryPlayer1.size(); i++) {
                    int x = throwHistoryPlayer1.get(i);
                    if(pairLeft.get(j)[0].equals("-1"))
                        j++;
                    else if(x != -1) {
                        pairLeft.get(j)[0] = String.valueOf((x/100)*(x%100));
                        j++;
                    }
                }

                for(int i = 0; i < ptsHistoryPlayer2.size(); i++) {
                    if(ptsHistoryPlayer2.get(i) != -1)
                        pairRight.add(new String[]{"", String.valueOf(ptsHistoryPlayer2.get(i))});
                    if(i > 0 && ptsHistoryPlayer2.get(i-1) == -1)
                        pairRight.add(new String[]{"-1", "-1"});
                }

                for(int i = 0, j = 1; i < throwHistoryPlayer2.size(); i++) {
                    int x = throwHistoryPlayer2.get(i);
                    if(pairRight.get(j)[0].equals("-1"))
                        j++;
                    else if(x != -1) {
                        pairRight.get(j)[0] = String.valueOf((x/100)*(x%100));
                        j++;
                    }
                }

                TextView playerLeft = view.findViewById(R.id.show_player_left);
                TextView playerRight = view.findViewById(R.id.show_player_right);
                playerLeft.setText(match.player1.name);
                playerRight.setText(match.player2.name);

                ListView listLeft = view.findViewById(R.id.history_left);
                ListView listRight = view.findViewById(R.id.history_right);

                MatchHistoryAdapter adapterLeft = new MatchHistoryAdapter(context,
                        R.layout.match_history_list_item, pairLeft);
                listLeft.setAdapter(adapterLeft);

                MatchHistoryAdapter adapterRight = new MatchHistoryAdapter(context,
                        R.layout.match_history_list_item, pairRight);
                listRight.setAdapter(adapterRight);
            }
        }
    }

    /**
     * Custom {@link ArrayAdapter} that shows thrown value and remaining score side by side
     * and puts a divider after a completed throw
     */
    private static class MatchHistoryAdapter extends ArrayAdapter<String[]> {

        /** context from caller */
        private Context context;
        /** resource of child views except divider */
        private int resource;
        /** list of pairs of thrown value and remaining score */
        private List<String[]> objects;

        /** default constructor */
        public MatchHistoryAdapter(@NonNull Context context, int resource, @NonNull List<String[]> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (objects.get(position)[0].equals("-1")) ? 1 : 0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //check if current child is divider or not
            if(getItemViewType(position) == 0) {
                if(convertView == null)
                    convertView = LayoutInflater.from(context).inflate(resource, parent, false);

                String[] item = objects.get(position);

                TextView thrown = convertView.findViewById(R.id.item_left);
                TextView remaining = convertView.findViewById(R.id.item_right);

                thrown.setText(item[0]);
                remaining.setText(item[1]);
            }
            else {
                if(convertView == null)
                    convertView = LayoutInflater.from(context).inflate(R.layout.divider, parent, false);
            }

            return convertView;
        }
    }

}
