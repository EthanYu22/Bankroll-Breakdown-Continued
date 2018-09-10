package com.example.ethan.pokerjournal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

// Edits Game Entries
public class GameEditActivity extends AppCompatActivity {

    DatabaseHelper db;
    private android.support.v7.widget.Toolbar toolbar;
    Game game;
    int gameId;

    Spinner spinType;
    Spinner spinBlinds;
    Spinner spinMonth;
    Spinner spinDay;
    Spinner spinYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_edit);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        gameId = MainActivity.gameId;
        game = db.getGame(gameId);

        final TextView displayLocation = (TextView) findViewById(R.id.editLocation);
        displayLocation.setText(game.getLocation());
        final TextView displayTime = (TextView) findViewById(R.id.editTime);
        displayTime.setText(Double.toString(game.getTime()));
        final TextView displayBuyIn = (TextView) findViewById(R.id.editBuyIn);
        displayBuyIn.setText(Double.toString(game.getBuyIn()));
        final TextView displayCashOut = (TextView) findViewById(R.id.editCashOut);
        displayCashOut.setText(Double.toString(game.getCashOut()));

        // Set Up Type & Blinds Spinner
        spinType = (Spinner) findViewById(R.id.spinnerGameType);
        spinBlinds = (Spinner) findViewById(R.id.spinnerGameBlinds);

        // Set Up Date Spinners
        spinMonth = (Spinner) findViewById(R.id.spinnerGameMonth);
        spinDay = (Spinner) findViewById(R.id.spinnerGameDay);
        spinYear = (Spinner) findViewById(R.id.spinnerGameYear);

        final ArrayAdapter<CharSequence> monthsArray = ArrayAdapter.createFromResource(this,R.array.months, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> days29Array = ArrayAdapter.createFromResource(this, R.array.days29, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> days30Array = ArrayAdapter.createFromResource(this, R.array.days30, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> daysArray = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> yearsArray = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_dropdown_item);

        monthsArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days29Array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days30Array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinMonth.setAdapter(monthsArray);
        spinDay.setAdapter(daysArray);
        spinYear.setAdapter(yearsArray);

        // Change Days Displayed in Spinners According to Month
        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 1:
                        spinDay.setAdapter(days29Array);
                        break;
                    case 2:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 3:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 4:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 5:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 6:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 7:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 8:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 9:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 10:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 11:
                        spinDay.setAdapter(daysArray);
                }
                setDaySpinner(game.getDate());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        setTypeSpinner(game.getType());
        setBlindsSpinner(game.getBlinds());
        setMonthSpinner(game.getDate());
        setYearSpinner(game.getDate());
    }

    /*
    // Action When Off Game Form Page
    public void onPause() {
        super.onPause();
        finish();
    }*/

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Displays Current Game Type Entry
    public void setTypeSpinner(String type){
        String[] typeArray = getResources().getStringArray(R.array.game_types);
        int position = Arrays.asList(typeArray).indexOf(type);
        spinType.setSelection(position);
    }

    //Displays Current Blinds Entry
    public void setBlindsSpinner(String blinds){
        String[] blindsArray = getResources().getStringArray(R.array.game_blinds);
        int position = Arrays.asList(blindsArray).indexOf(blinds);
        spinBlinds.setSelection(position);
    }

    // Displays Current Day Entry
    public void setDaySpinner(String date){
        String month = (String) date.subSequence(3,5);
        int position = Integer.parseInt(month) - 1;
        spinDay.setSelection(position);
    }

    // Displays Current Month Entry
    public void setMonthSpinner(String date){
        String month = (String) date.subSequence(0,2);
        int position = Integer.parseInt(month) - 1;
        spinMonth.setSelection(position);
    }

    // Displays Current Year Entry
    public void setYearSpinner(String date){
        String month = (String) date.subSequence(6,10);
        String[] monthArray = getResources().getStringArray(R.array.years);
        int position = Arrays.asList(monthArray).indexOf(month);
        spinYear.setSelection(position);
    }

    // Submit Edited Game Session
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

        // Get Input of Date and Format into String
        Spinner spinMonth = (Spinner) findViewById(R.id.spinnerGameMonth);
        String month = spinMonth.getSelectedItem().toString();
        Spinner spinDay = (Spinner) findViewById(R.id.spinnerGameDay);
        String day = spinDay.getSelectedItem().toString();
        Spinner spinYear = (Spinner) findViewById(R.id.spinnerGameYear);
        String year = spinYear.getSelectedItem().toString();

        String date = "";
        String date2 = "";
        String[] dayMonthYearDateDate2 = {day, month, year, date, date2};

        // Append Day, Month, Year to Format - Date: MM/DD/YYYY Date2: YYYY/MM/DD
        MainActivity.appendDates(dayMonthYearDateDate2);
        date = dayMonthYearDateDate2[3];
        date2 = dayMonthYearDateDate2[4];

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

        // Cash Out as Double
        double cashOut = Double.parseDouble(editCashOut.getText().toString());

        // Set Entries into DB
        game.setAll(gameId, type, blinds, location, date, date2, time, buyIn, cashOut);

        db.editGame(game);

        finish();
    }
}
