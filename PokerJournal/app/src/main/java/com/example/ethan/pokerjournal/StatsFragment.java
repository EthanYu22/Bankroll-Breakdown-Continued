package com.example.ethan.pokerjournal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Andrew on 3/9/2016.
 */
public class StatsFragment extends Fragment {

    DatabaseHelper db;
    List<Game> gameList;

    public StatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout
        return inflater.inflate(R.layout.stats, container, false);
    }

    //@Override
    public void onResume() {
        super.onResume();
        displayStats();
    }

    public void displayStats() {
        Game game;
        gameList = db.getAllGames();
        int totalHours = 0;
        double avgHours;
        int netProfit;
        int buyIn = 0;
        int cashOut = 0;
        int avgBuy = 0;
        double hourlyRate;
        int winningSession = 0;
        int losingSession = 0;
        int totalSessions = 0;
        double biggestWin = 0;
        double biggestLoss = 0;
        for (int i = 0; i < gameList.size(); i++) {
            game = gameList.get(i);
            totalHours += game.getTime();
            buyIn += game.getBuyIn();
            cashOut += game.getCashOut();
            avgBuy = buyIn / gameList.size();
            double bigwin = game.getCashOut() - game.getBuyIn();
            if(bigwin > biggestWin){
                biggestWin = bigwin;
            } else if(bigwin < biggestLoss){
                biggestLoss = bigwin;
            }

            if(game.getCashOut() - game.getBuyIn() > 0){
                winningSession++;
            }else if(game.getBuyIn() - game.getCashOut() > 0){
                losingSession++;
            }
            totalSessions = winningSession + losingSession;
        }
        if (gameList.size() == 0) {
            TextView x = (TextView) getView().findViewById(R.id.statText);
            x.setText("No Game");
        } else {
            avgHours = totalHours / gameList.size();
            netProfit = cashOut - buyIn;
            hourlyRate = netProfit / totalHours;
            TextView x = (TextView) getView().findViewById(R.id.statText);
            x.setText("Total Hours: " + totalHours + "\n" +
                    "Average Session Time: " + avgHours + "\n" +
                    "Net Profit: $" + netProfit + "\n" +
                    "Hourly Rate: $" + hourlyRate + "\n" +
                    "Winning Sessions: " + winningSession + "\n" +
                    "Losing Sessions: " + losingSession + "\n" +
                    "Total Sessions: " + totalSessions + "\n" +
                    "Average Buy In: $" + avgBuy + "\n" +
                    "Largest Win: $" + biggestWin + "\n" +
                    "Largest Loss: $" + biggestLoss);
        }
        
    }
}
