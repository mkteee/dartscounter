package com.kuehnkroeger.dartscounter.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.kuehnkroeger.dartscounter.R;
import com.kuehnkroeger.dartscounter.database.DartsRepository;
import com.kuehnkroeger.dartscounter.database.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link android.widget.AutoCompleteTextView} with custom {@link ArrayAdapter} to support
 * deleting entries and custom filtering. Also provides function to get the selected {@link Player}
 */
public class PlayerDropdownTextView extends AppCompatAutoCompleteTextView {

    /** item from dropdown list that has been selected */
    private Player selection;

    /** default constructor */
    public PlayerDropdownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * returns selected Player
     * @return selected Player
     */
    public Player getSelection() {
        return selection;
    }

    /**
     * sets {@link PlayerAdapter} (Dropdown) to provided List of players
     * @param context context of calling Activity. Is needed for Database operations
     * @param players List of players to display in dropdown menu
     */
    public void setPlayers(Context context, List<Player> players) {
        PlayerAdapter adapter = new PlayerAdapter(context, android.R.layout.select_dialog_item, players);
        setAdapter(adapter);
    }

    /**
     * Custom {@link ArrayAdapter} that supports deleting {@link Player}s from Database and
     * informing the {@link PlayerDropdownTextView} which player has been selected.
     */
    private class PlayerAdapter extends ArrayAdapter<Player> {

        /** Context needed for Database operations. Needs to be from an {@link AppCompatActivity}*/
        private Context context;
        /** resource of child views */
        private int resource;
        /** original list of players */
        private List<Player> items;
        /** list of players after a search query has been performed */
        private List<Player> filtered;

        /** default constructor */
        public PlayerAdapter(@NonNull Context context, int resource, @NonNull List<Player> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.items = objects;
            this.filtered = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null)
                convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            final Player item = filtered.get(position);
            ((TextView)convertView).setText(item.name);

            //setting long click to deleting player from databse (with confirming dialog)
            convertView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(String.format(getResources().getString(R.string.remove_player), item.name));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DartsRepository repo = new DartsRepository(((AppCompatActivity)context).getApplication());
                            repo.delete(item);


                            items.remove(item);
                            if(filtered != items)
                                filtered.remove(item);

                            notifyDataSetChanged();
                            dismissDropDown();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();

                    return false;
                }
            });

            //on normal click set selection of embedding dropdowntextview
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setText(item.name);
                    selection = item;
                    setSelection(item.name.length());
                    dismissDropDown();
                }
            });



            return convertView;
        }

        @Override
        public int getCount() {
            return filtered.size();
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    charSequence = charSequence.toString().toLowerCase();

                    ArrayList<Player> found = new ArrayList<>();
                    //perform simple contains query
                    if(charSequence.toString().length() > 0) {
                        for(Player item: items)
                            if(item.name.toLowerCase().contains(charSequence))
                                found.add(item);

                        results.values = found;
                        results.count = found.size();
                    }
                    else {
                        results.values = items;
                        results.count = items.size();
                    }
                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filtered = (ArrayList<Player>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
