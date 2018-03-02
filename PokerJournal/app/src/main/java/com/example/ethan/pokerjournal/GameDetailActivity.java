package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

// Shows Game Details
public class GameDetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    Game game;
    int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail); // Runs layout.activity_game_detail
        gameId = MainActivity.gameId; // Refers to Game Listing ID
        db = new DatabaseHelper(this);
        game = db.getGame(gameId);
    }

    // Action When On Game Detail Page
    public void onResume() {
        super.onResume();
        displayDetails();
    }

    // Action When Off Game Detail Page
    public void onPause() {
        super.onPause();
        finish();
    }

    // Displays Game Details
    public void displayDetails() {
        TextView hourlyRate = (TextView) findViewById(R.id.textGameHourlyRate);
        TextView netProfit = (TextView) findViewById(R.id.textGameNetProfit);
        TextView type = (TextView) findViewById(R.id.textGameType);
        TextView blinds = (TextView) findViewById(R.id.textGameBlinds);
        TextView location = (TextView) findViewById(R.id.textGameLocation);
        TextView date = (TextView) findViewById(R.id.textGameDate);
        TextView time = (TextView) findViewById(R.id.textGameTime);
        TextView buyIn = (TextView) findViewById(R.id.textGameBuyIn);
        TextView cashOut = (TextView) findViewById(R.id.textGameCashOut);

        // Calculate Net Profit and Hourly Rate
        Double nProfit = game.getCashOut() - game.getBuyIn();
        Double hRate = nProfit/game.getTime();

        // Set Significant Figures to 2 for Net Profit, Hourly Rate, Buy In, and Cash Out
        String nP = String.format("%.2f", nProfit);
        String hR = String.format("%.2f", hRate);
        String bIn = String.format("%.2f", game.getBuyIn());
        String cOut = String.format("%.2f", game.getCashOut());

        // Set Text for Each TextView
        hourlyRate.setText("Session Hourly Rate: $" + hR);
        netProfit.setText("Session Net Profit: $" + nP);
        type.setText("Game: " + game.getType());
        blinds.setText("Blinds: " + game.getBlinds());
        location.setText("Location: " + game.getLocation());
        date.setText("Date: " + game.getDate());
        time.setText("Time: " + game.getTime() + " Hours");
        buyIn.setText("Buy In: $" + bIn);
        cashOut.setText("Cash Out: $" + cOut);
    }

    // Edit Game
    public void onClickEditGame(View v) {
        gameId = v.getId();
        Intent intent = new Intent(GameDetailActivity.this, GameEditActivity.class);
        startActivity(intent);
    }

    // Deletes Game
    public void onClickDeleteGame(View v) {
        db.deleteGame(gameId);
        Toast toast = Toast.makeText(getApplication(), "Game Deleted", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
