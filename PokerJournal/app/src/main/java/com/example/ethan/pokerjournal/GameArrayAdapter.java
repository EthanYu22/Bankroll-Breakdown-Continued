package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Creates Each Poker Session as a List with Specific Content
public class GameArrayAdapter extends ArrayAdapter<Game>
{

    // Initialize GameArrayAdapter
    public GameArrayAdapter(Context context, List<Game> gameList)
    {
        super(context, 0, gameList);
    }

    @Override
    // Fills Out Information to Be Displayed For Each Poker Session
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Game game = getItem(position);
        String line_2 = "";
        // Display Each Game as a Game in a Listing
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);
        }

        // Line Displays for Each Game Listing
        TextView gameDescription = (TextView) convertView.findViewById(R.id.gameDescription);

        // Initialize The Display for Each Game Listing
        gameDescription.setText(game.toString());

        // Labels ViewId as GameId
        convertView.setId(game.getId());
        return convertView;
    }
}
