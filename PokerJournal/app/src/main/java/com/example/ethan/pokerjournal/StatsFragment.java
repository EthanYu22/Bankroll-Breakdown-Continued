package com.example.ethan.pokerjournal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// Displays a List of Statistical Analysis Under Statistics Tab
public class StatsFragment extends Fragment
{

    DatabaseHelper db;
    List<Session> sessionList;

    public StatsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate Layout
        return inflater.inflate(R.layout.stats, container, false);
    }

    // Action When On Stats Page
    public void onResume()
    {
        super.onResume();
        displayStats();
    }

    // Displays Overall Session Statistics
    public void displayStats()
    {
        Session session;
        sessionList = db.getAllSessions();
        double totalHours = 0;
        double avgSessionDuration;
        int netProfit;
        int buyIn = 0;
        int cashOut = 0;
        int avgBuyIn = 0;
        int avgCashOut = 0;
        double hourlyRate;
        int winningSession = 0;
        int losingSession = 0;
        int totalSessions = 0;
        int biggestWin = 0;
        int biggestLoss = 0;

        // Calculation for Statistics
        for (int i = 0; i < sessionList.size(); i++)
        {
            session = sessionList.get(i);
            totalHours += session.getTime() / 60.0; // Net Hours
            buyIn += session.getBuyIn(); // Net Buy In
            cashOut += session.getCashOut(); // Net Cash Out
            avgBuyIn = buyIn / sessionList.size(); // Average Buy In
            avgCashOut = cashOut / sessionList.size(); // Average Cash Out

            // Find Biggest Win and Loss
            int bigwin = session.getCashOut() - session.getBuyIn();
            if (bigwin > biggestWin)
            {
                biggestWin = bigwin;
            }
            else if (bigwin < biggestLoss)
            {
                biggestLoss = bigwin;
            }

            // Tally Number of Winning and Losing Sessions
            if (session.getCashOut() - session.getBuyIn() > 0)
            {
                winningSession++;
            }
            else if (session.getBuyIn() - session.getCashOut() > 0)
            {
                losingSession++;
            }

            // Get Total Sessions
            totalSessions = winningSession + losingSession;
            if (session.getCashOut() - session.getBuyIn() == 0)
            {
                totalSessions++;
            }
        }

        // Displays Session Statistics
        TextView x = (TextView) getView().findViewById(R.id.hoursPlayed); // Total Hours
        TextView ast = (TextView) getView().findViewById(R.id.avgHours); // Average Session Time
        TextView np = (TextView) getView().findViewById(R.id.netProfit); // Net Profit
        TextView hr = (TextView) getView().findViewById(R.id.hourlyRate); // Hourly Rate
        TextView ws = (TextView) getView().findViewById(R.id.winningSession); // Winning Sessions
        TextView ls = (TextView) getView().findViewById(R.id.losingSession); // Losing Sessions
        TextView ts = (TextView) getView().findViewById(R.id.totalSession); // Total Sessions
        TextView abi = (TextView) getView().findViewById(R.id.avgBuy); // Average Buy In
        TextView aco = (TextView) getView().findViewById(R.id.avgCashOut); // Average Cash Out
        TextView bw = (TextView) getView().findViewById(R.id.biggestWin); // Biggest Win
        TextView bl = (TextView) getView().findViewById(R.id.biggestLoss); // Biggest Loss

        if (sessionList.size() == 0)
        { // If No Sessions Are Played Display This
            // Set Text As Blank for Each TextView
            hr.setText("Hourly Rate: ");
            np.setText("Net Profit: ");
            x.setText("Hours Played: ");
            ast.setText("Avg Hourly Session Duration: ");
            ts.setText("Sessions Count: ");
            abi.setText("Avg Buy In: ");
            aco.setText("Avg Cash Out: ");
            ws.setText("Winning Sessions: ");
            ls.setText("Losing Sessions: ");
            bw.setText("Largest Win: ");
            bl.setText("Largest Loss: ");

        }
        else
        { // If Sessions Are Played Display This
            // Calculate Net Profit, Hourly Rate, and Average Session Duration
            netProfit = cashOut - buyIn;
            hourlyRate = netProfit / totalHours;
            avgSessionDuration = totalHours / sessionList.size();

            // Set Text for Each TextView
            if (hourlyRate < 0)
            {
                hr.setText("Hourly Rate: -$" + String.format("%.2f", Math.abs(hourlyRate)));
            }
            else
            {
                hr.setText("Hourly Rate: $" + String.format("%.2f", Math.abs(hourlyRate)));
            }
            if (netProfit < 0)
            {
                np.setText("Net Profit: -$" + Math.abs(netProfit));
            }
            else
            {
                np.setText("Net Profit: $" + netProfit);
            }
            x.setText("Hours Played: " + String.format("%.2f", totalHours));
            ast.setText("Avg Hourly Session Duration: " + String.format("%.2f", avgSessionDuration));
            ts.setText("Sessions Count: " + totalSessions);
            abi.setText("Avg Buy In: $" + avgBuyIn);
            aco.setText("Avg Cash Out: $" + avgCashOut);
            ws.setText("Winning Sessions: " + winningSession);
            ls.setText("Losing Sessions: " + losingSession);
            bw.setText("Largest Win: $" + biggestWin);
            bl.setText("Largest Loss: $" + Math.abs(biggestLoss));
        }
    }
}
