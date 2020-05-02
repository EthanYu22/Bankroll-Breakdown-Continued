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

// Displays Each Session Session Sorted By Date as a List Under History Tab
public class HistoryFragment extends Fragment
{

    DatabaseHelper db;
    List<Session> sessionList;

    public HistoryFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.history, container, false); // Inflate Layout
    }

    // Action When On History Page
    public void onResume()
    {
        super.onResume();
        displaySessions();
    }

    // Displays All Session Sessions
    public void displaySessions()
    {
        // Displays Session Sessions
        sessionList = db.getAllSessions();
        ListView lv = (ListView) getView().findViewById(R.id.listSessions);
        SessionArrayAdapter adapter = new SessionArrayAdapter(getActivity(), sessionList);

        // Sorts Sessions by Date
        adapter.sort(new Comparator<Session>()
        {
            public int compare(Session arg0, Session arg1)
            {
                return arg0.date.compareTo(arg1.date);
            }
        });

        // Descending Order (Most Recent on Top)
        Collections.reverse(sessionList);

        // Arbitrary Code for Sorting + Displaying Adapter
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}