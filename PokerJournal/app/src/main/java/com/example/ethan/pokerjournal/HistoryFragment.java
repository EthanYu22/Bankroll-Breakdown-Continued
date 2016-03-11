package com.example.ethan.pokerjournal;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Andrew on 3/9/2016.
 */

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
        //inflate layout
        return inflater.inflate(R.layout.history, container, false);
    }

    public void onResume() {
        super.onResume();
        gameList = db.getAllGames();
        displayGames();
    }

    public void displayGames() {
        ListView lv = (ListView) getView().findViewById(R.id.listGames);
        GameArrayAdapter adapter = new GameArrayAdapter(getActivity(), gameList);
        lv.setAdapter(adapter);
    }

}
