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

    public HistoryFragment() {}

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

        // Sorts Games by Date
        adapter.sort(new Comparator<Game>() {
            public int compare(Game arg0, Game arg1) {
                return arg0.date.compareTo(arg1.date);
            }
        });

        // Arbitrary Code for Sorting + Displaying Adapter
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();


       /* private static List<Opportunity> sortOpportunitiesByDate(List<Opportunity> opportunities) {
            Collections.sort(opportunities, new Comparator<Opportunity>() {
                public int compare(Opportunity o1, Opportunity o2) {
                    DateFormat format = new SimpleDateFormat("MM/DD/YYYY",Locale.US);

                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1=format.parse(o1.getExpires());
                        date2=format.parse(o2.getExpires());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return date1.compareTo(date2);
                }
            });

            return opportunities;
        }*/
    }

}
