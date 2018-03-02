package com.example.ethan.pokerjournal;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static com.example.ethan.pokerjournal.MainActivity.bankId;


public class GameEditActivity extends AppCompatActivity {

    DatabaseHelper db;
    Game game;
    int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_edit);
        gameId = MainActivity.gameId; // Refers to Game Listing ID
        db = new DatabaseHelper(this);
        game = db.getGame(gameId);
        final TextView displayLocation = (TextView) findViewById(R.id.editLocation); // Displays Location in TextView
        displayLocation.setText(game.getLocation());
        final TextView displayTime = (TextView) findViewById(R.id.editTime); // Displays Time in TextView
        displayTime.setText(Double.toString(game.getTime()));
        final TextView displayBuyIn = (TextView) findViewById(R.id.editBuyIn); // Displays Buy In Amount in TextView
        displayBuyIn.setText(Double.toString(game.getBuyIn()));
        final TextView displayCashOut = (TextView) findViewById(R.id.editCashOut); // Displays Cash Out Amount in TextView
        displayCashOut.setText(Double.toString(game.getCashOut()));
    }

    // Action When Off Game Form Page
    public void onPause() {
        super.onPause();
        finish();
    }

    // Submit Game Transaction
    public void onClickGameButton(View v) {
        DatabaseHelper db = new DatabaseHelper(this);
        Game game = new Game();
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        // Get Input for Poker Variation Type
        Spinner spinGameType = (Spinner) findViewById(R.id.spinnerGameType);
        String type = spinGameType.getSelectedItem().toString();

        // Get Input for Blind Amount
        Spinner spinGameBlinds = (Spinner) findViewById(R.id.spinnerGameBlinds);
        String blinds = spinGameBlinds.getSelectedItem().toString();

        // Get Location and Make Sure it's Valid
        EditText editLocation = (EditText) findViewById(R.id.editLocation);
        String location = editLocation.getText().toString();
        if(location.isEmpty()) {
            toast.show();
            return;
        }

        // Get Input of Date
        Spinner spinMonth = (Spinner) findViewById(R.id.spinnerGameMonth);
        String month = spinMonth.getSelectedItem().toString();
        Spinner spinDay = (Spinner) findViewById(R.id.spinnerGameDay);
        String day = spinDay.getSelectedItem().toString();
        Spinner spinYear = (Spinner) findViewById(R.id.spinnerGameYear);
        String year = spinYear.getSelectedItem().toString();
        String date = month + "/" + day + "/" + year;

        // Get Session Duration and Make Sure it's Valid
        EditText editTime = (EditText) findViewById(R.id.editTime);
        if (editTime.getText().toString().isEmpty()) {
            toast.show();
            return;
        }
        // Input Session Duration Entry
        double time = Double.parseDouble(editTime.getText().toString());

        // Get Buy In and Make Sure it's Valid
        EditText editBuyIn = (EditText) findViewById(R.id.editBuyIn);
        if (editBuyIn.getText().toString().isEmpty()) {
            toast.show();
            return;
        }
        // Input Buy In Entry
        double buyIn = Double.parseDouble(editBuyIn.getText().toString());

        // Get Cash Out and Make Sure it's Valid
        EditText editCashOut = (EditText) findViewById(R.id.editCashOut);
        if (editCashOut.getText().toString().isEmpty()) {
            toast.show();
            return;
        }

        // Input Cash Out Entry
        double cashOut = Double.parseDouble(editCashOut.getText().toString());

        // Set Entries into DB
        game.setType(type);
        game.setBlinds(blinds);
        game.setLocation(location);
        game.setDate(date);
        game.setTime(time);
        game.setBuyIn(buyIn);
        game.setCashOut(cashOut);
        game.setId(gameId);

        db.editGame(game);

        finish();
    }
}