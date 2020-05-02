package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Creates Each Poker Session as a List with Specific Content
public class SessionArrayAdapter extends ArrayAdapter<Session>
{

    // Initialize SessionArrayAdapter
    public SessionArrayAdapter(Context context, List<Session> sessionList)
    {
        super(context, 0, sessionList);
    }

    @Override
    // Fills Out Information to Be Displayed For Each Poker Session
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Session session = getItem(position);
        String line_2 = "";
        // Display Each Session as a Session in a Listing
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_item, parent, false);
        }

        // Line Displays for Each Session Listing
        TextView sessionDescription = (TextView) convertView.findViewById(R.id.sessionDescription);

        // Initialize The Display for Each Session Listing
        sessionDescription.setText(session.toString());

        // Labels ViewId as SessionId
        convertView.setId(session.getId());
        return convertView;
    }
}
