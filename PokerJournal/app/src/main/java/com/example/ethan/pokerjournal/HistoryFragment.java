package com.example.ethan.pokerjournal;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;


public class HistoryFragment extends Fragment {

    DatabaseHelper db;
    List<Game> gameList;

    public HistoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate Layout
        return inflater.inflate(R.layout.history, container, false);
    }

    // Action When On History Page
    public void onResume() {
        super.onResume();
        displayGames();
    }

    // Displays All Games
    public void displayGames() {
        // Displays Games
        gameList = db.getAllGames();
        ListView lv = (ListView) getView().findViewById(R.id.listGames);
        GameArrayAdapter adapter = new GameArrayAdapter(getActivity(), gameList);

        // Sorts Games by Date
        adapter.sort(new Comparator<Game>() {
            public int compare(Game arg0, Game arg1) {
                return arg0.date.compareTo(arg1.date);
            }
        });

        // Arbitrary Code for Sorting + Displaying Adapter
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        /*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(getActivity(), "it works!", Toast.LENGTH_SHORT);
                toast.show();

            }
        });*/
    }

}
