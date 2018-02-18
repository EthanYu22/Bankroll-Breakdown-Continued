package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GameArrayAdapter extends ArrayAdapter<Game> {
    public GameArrayAdapter(Context context, List<Game> gameList) {
        super(context, 0, gameList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Game game = getItem(position);

        // Display Each Game as a Game in a Listing
        if (convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);}

        // Variables for Each Game Listing
        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        TextView description_1 = (TextView) convertView.findViewById(R.id.itemLocation);
        TextView description_2 = (TextView) convertView.findViewById(R.id.itemDescription);

        String location = game.getLocation();
        double netProfit = game.getCashOut() - game.getBuyIn();
        double sessionLength = game.getTime();
        String msg_1 = "Location: " + location;
        String msg_2 = "$" + netProfit + " in " + sessionLength + " hours.";

        // The Display for Each Game Listing
        date.setText(game.getDate());
        description_1.setText(msg_1);
        description_2.setText(msg_2);

        // Labels ViewId as GameId
        convertView.setId(game.getId());
        return convertView;
    }
}
