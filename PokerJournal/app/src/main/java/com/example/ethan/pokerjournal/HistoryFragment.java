package com.example.ethan.pokerjournal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Displays Each Game Session Sorted By Date as a List Under History Tab
public class HistoryFragment extends Fragment {

    DatabaseHelper db;
    List<Game> gameList;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history, container, false); // Inflate Layout
    }

    // Action When On History Page
    public void onResume() {
        super.onResume();
        displayGames();
    }

    // Displays All Game Sessions
    public void displayGames() {
        // Displays Game Sessions
        gameList = db.getAllGames();
        ListView lv = (ListView) getView().findViewById(R.id.listGames);
        GameArrayAdapter adapter = new GameArrayAdapter(getActivity(), gameList);

        // Sorts Games by Date2
        adapter.sort(new Comparator<Game>() {
            public int compare(Game arg0, Game arg1) {
                return arg0.date2.compareTo(arg1.date2);
            }
        });

        // Descending Order (Most Recent on Top)
        Collections.reverse(gameList);

        // Arbitrary Code for Sorting + Displaying Adapter
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}