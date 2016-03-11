package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Andrew on 3/11/2016.
 */
public class GameArrayAdapter extends ArrayAdapter<Game> {
    public GameArrayAdapter(Context context, List<Game> gameList) {
        super(context, 0, gameList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Game game = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        TextView description = (TextView) convertView.findViewById(R.id.itemDescription);

        double total = game.getCashOut() - game.getBuyIn();
        int time = game.getTime();
        String msg = "$" + total + " in " + time + " hours.";

        date.setText(game.getDate());
        description.setText(msg);

        return convertView;
    }
}
