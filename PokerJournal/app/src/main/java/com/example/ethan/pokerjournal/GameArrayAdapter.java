package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Creates Each Poker Session as a List with Specific Content
public class GameArrayAdapter extends ArrayAdapter<Game> {

    // Initialize GameArrayAdapter
    public GameArrayAdapter(Context context, List<Game> gameList) {
        super(context, 0, gameList);
    }

    @Override
    // Fills Out Information to Be Displayed For Each Poker Session
    public View getView(int position, View convertView, ViewGroup parent) {
        Game game = getItem(position);
        String line_2 = new String();
        // Display Each Game as a Game in a Listing
        if (convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);}

        // Line Displays for Each Game Listing
        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        TextView description_1 = (TextView) convertView.findViewById(R.id.itemLocation);
        TextView description_2 = (TextView) convertView.findViewById(R.id.itemDescription);

        String location = game.getLocation();
        double netProfit = game.getCashOut() - game.getBuyIn();
        double sessionLength = game.getTime();
        String line_1 = "Location: " + location;
        if(netProfit < 0) {
            line_2 = "-$" + -netProfit + " in " + sessionLength + " hours.";
        }else{
            line_2 = "$" + netProfit + " in " + sessionLength + " hours.";
        }

        // Initialize The Display for Each Game Listing
        date.setText(game.getDate());
        description_1.setText(line_1);
        description_2.setText(line_2);

        // Labels ViewId as GameId
        convertView.setId(game.getId());
        return convertView;
    }
}
