package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameDetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    Game game;
    int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        gameId = MainActivity.gameId;
        db = new DatabaseHelper(this);
        game = db.getGame(gameId);
    }

    public void onResume() {
        super.onResume();
        displayDetails();
    }

    public void onPause() {
        super.onPause();
        finish();
    }

    public void displayDetails() {
        TextView id = (TextView) findViewById(R.id.textGameId);
        TextView type = (TextView) findViewById(R.id.textGameType);
        TextView location = (TextView) findViewById(R.id.textGameLocation);
        TextView date = (TextView) findViewById(R.id.textGameDate);
        TextView time = (TextView) findViewById(R.id.textGameTime);
        TextView buyIn = (TextView) findViewById(R.id.textGameBuyIn);
        TextView cashOut = (TextView) findViewById(R.id.textGameCashOut);

        id.setText("ID: " + game.getId());
        type.setText("Game: " + game.getType());
        location.setText("Location: " + game.getLocation());
        date.setText("Date: " + game.getDate());
        time.setText("Time: " + game.getTime() + " Hours");
        buyIn.setText("Buy In: $" + game.getBuyIn());
        cashOut.setText("Cash Out: $" + game.getCashOut());
    }

    public void onClickDeleteGame(View v) {
        db.deleteGame(gameId);
        Toast toast = Toast.makeText(getApplication(), "Game Deleted", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
