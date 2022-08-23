package com.kuehnkroeger.dartscounter.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuehnkroeger.dartscounter.R;

/**
 * Generates a {@link DialogFragment} which shows the winner and provides options to
 * save, share or exit the game.
 */
public class GameFinishedFragment extends DialogFragment {

    /**
     * Creates an instance of the dialog
     * @param winner the winner of the match
     * @return instance to display
     */
    public static GameFinishedFragment newInstance(String winner) {
        GameFinishedFragment dialog = new GameFinishedFragment();
        Bundle args = new Bundle();
        args.putString("winner", winner);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_finished, container);
    }

    /**
     * Sets the winner dynamically with the help of getString
     * @param view view which is handled
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        String winner = getArguments().getString("winner", "Some");
        TextView winner_text = view.findViewById(R.id.winner_text);
        String winner_str = getString(R.string.winner_text, winner);
        winner_text.setText(winner_str);
    }
}
