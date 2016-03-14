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
        double totalHours = 0;
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
            if(game.getCashOut() - game.getBuyIn() == 0){
                totalSessions++;
            }
        }
        TextView x = (TextView) getView().findViewById(R.id.statText);
        TextView ast = (TextView) getView().findViewById(R.id.avgHours);
        TextView np = (TextView) getView().findViewById(R.id.netProfit);
        TextView hr = (TextView) getView().findViewById(R.id.hourlyRate);
        TextView ws = (TextView) getView().findViewById(R.id.winningSession);
        TextView ls = (TextView) getView().findViewById(R.id.lossingSession);
        TextView ts = (TextView) getView().findViewById(R.id.totalSession);
        TextView abi = (TextView) getView().findViewById(R.id.avgBuy);
        TextView bw = (TextView) getView().findViewById(R.id.biggestWin);
        TextView bl = (TextView) getView().findViewById(R.id.biggestLoss);
        if (gameList.size() == 0) {
            x.setText("Total Hours: ");
            ast.setText("Avg Session Time: ");
            np.setText("Net Profit: ");
            hr.setText("Hourly Rate: ");
            ws.setText("Winning Sessions: ");
            ls.setText("Losing Sessions: ");
            ts.setText("Total Sessions: ");
            abi.setText("Avg Buy In: ");
            bw.setText("Biggest Win: ");
            bl.setText("Biggest Loss: ");
        } else {
            avgHours = totalHours / gameList.size();
            String avgH = String.format("%.2f", avgHours);
            netProfit = cashOut - buyIn;
            hourlyRate = netProfit / totalHours;
            String hourlyR = String.format("%.2f", hourlyRate);
            x.setText("Total Hours: " + totalHours);
            ast.setText("Avg Session Time: " + avgH);
            np.setText("Net Profit: $" + netProfit);
            hr.setText("Hourly Rate: $" + hourlyR);
            ws.setText("Winning Sessions: " + winningSession);
            ls.setText("Losing Sessions: " + losingSession);
            ts.setText("Total Sessions: " + totalSessions);
            abi.setText("Avg Buy In: $" + avgBuy);
            bw.setText("Biggest Win: $" + biggestWin);
            bl.setText("Biggest Loss: $" + biggestLoss);
            /*x.setText("Total Hours: " + totalHours + "\n" +
                    "Average Session Time: " + avgHours + "\n" +
                    "Net Profit: $" + netProfit + "\n" +
                    "Hourly Rate: $" + hourlyRate + "\n" +
                    "Winning Sessions: " + winningSession + "\n" +
                    "Losing Sessions: " + losingSession + "\n" +
                    "Total Sessions: " + totalSessions + "\n" +
                    "Average Buy In: $" + avgBuy + "\n" +
                    "Largest Win: $" + biggestWin + "\n" +
                    "Largest Loss: $" + biggestLoss);*/
        }
        
    }
}
