package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        //get values + validate
        Spinner spin = (Spinner) findViewById(R.id.spinnerGameType);
        String type = spin.getSelectedItem().toString();
        EditText editLocation = (EditText) findViewById(R.id.editLocaiton);
        String location = editLocation.getText().toString();
        if(location.isEmpty()) {
            toast.show();
            return;
        }
        EditText editDate = (EditText) findViewById(R.id.editDate);
        String date = editDate.getText().toString();
        if (date.isEmpty()) {
            toast.show();
            return;
        }
        EditText editTime = (EditText) findViewById(R.id.editTime);
        if (editTime.getText().toString().isEmpty()) {
            toast.show();
            return;
        }
        int time = Integer.parseInt(editTime.getText().toString());
        EditText editBuyIn = (EditText) findViewById(R.id.editBuyIn);
        if (editBuyIn.getText().toString().isEmpty()) {
            toast.show();
            return;
        }
        double buyIn = Double.parseDouble(editBuyIn.getText().toString());
        EditText editCashOut = (EditText) findViewById(R.id.editCashOut);
        if (editCashOut.getText().toString().isEmpty()) {
            toast.show();
            return;
        }
        double cashOut = Double.parseDouble(editCashOut.getText().toString());

        //set values
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
