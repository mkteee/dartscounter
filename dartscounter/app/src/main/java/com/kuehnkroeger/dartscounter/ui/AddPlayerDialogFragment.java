package com.kuehnkroeger.dartscounter.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuehnkroeger.dartscounter.R;

/**
 * Generates a {@link DialogFragment} and displays an interface to input a name for the player
 * to be added.
 * Calling Activity must implement the {@link NameEnteredListener} interface to receive the
 * entered name on pressing done on keyboard.
 * Usage -
 *  {@code AddPlayerDialogFragment dialog = AddPlayerDialogFragment.newInstance();}
 *  {@code dialog.show(getSupportFragmentManager(), tag);}
 */
public class AddPlayerDialogFragment extends DialogFragment {

    /** listener to be called when dialog is finished */
    private NameEnteredListener listener;

    /**
     * creates a new instance of this dialog
     * @return instance to display
     */
    public static AddPlayerDialogFragment newInstance() {
        final AddPlayerDialogFragment dialog = new AddPlayerDialogFragment();
        final Bundle args = new Bundle();
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText addPlayer = view.findViewById(R.id.add_player_name);
        addPlayer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //notify listener text is set and close dialog
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    listener.onNameEntered(textView.getText().toString());
                    dismiss();
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //set listener to Activity fragment is attached to
        listener = (NameEnteredListener) context;
    }

    /**
     * Simple interface to forward a String from dialog to calling Activity
     */
    public interface NameEnteredListener {
        void onNameEntered(String name);
    }
}
