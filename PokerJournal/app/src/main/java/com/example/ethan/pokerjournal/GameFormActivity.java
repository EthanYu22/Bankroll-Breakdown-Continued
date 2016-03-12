package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Random;

public class GameFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_form);
    }

    public void onPause() {
        super.onPause();
        finish();
    }

    public void onClickGameButton(View v) {
        DatabaseHelper db = new DatabaseHelper(this);
        Game game = new Game();
        Random rand = new Random();
        //get values
        Spinner spin = (Spinner) findViewById(R.id.spinnerGameType);
        String type = spin.getSelectedItem().toString();
        EditText editLocation = (EditText) findViewById(R.id.editLocaiton);
        String location = editLocation.getText().toString();
        EditText editDate = (EditText) findViewById(R.id.editDate);
        String date = editDate.getText().toString();
        EditText editTime = (EditText) findViewById(R.id.editTime);
        int time = Integer.parseInt(editTime.getText().toString());
        EditText editBuyIn = (EditText) findViewById(R.id.editBuyIn);
        double buyIn = Double.parseDouble(editBuyIn.getText().toString());
        EditText editCashOut = (EditText) findViewById(R.id.editCashOut);
        double cashOut = Double.parseDouble(editCashOut.getText().toString());
        //set values
        //game.setId(rand.nextInt(1000));//will auto increment
        game.setType(type);
        game.setLocation(location);
        game.setDate(date);
        game.setTime(time);
        game.setBuyIn(buyIn);
        game.setCashOut(cashOut);

        db.createGame(game);

        finish();
    }
}
