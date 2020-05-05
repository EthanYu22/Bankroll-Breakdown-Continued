package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LiveSessionTracker extends AppCompatActivity
{

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private Toolbar toolbar;
    TextView currentTotalBuyIn;
    TextView addOnAmount;

    String locationValue;
    String buyInValue;
    String sessionTypeValue;
    String sessionBlindsValue;

    int totalBuyIn;

    private Chronometer timer;
    private long pauseOffset;
    private boolean started = false;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_tracker);
        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();
        editor.putBoolean("liveSessionActive", true);
        editor.commit();
        Log.d("@@@@@@@@@@@@@@@@@@@@@@@@ ETHAN @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@", Boolean.toString(prefs.getBoolean("liveSessionActive", false)));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        timer = (Chronometer) findViewById(R.id.timer);

        currentTotalBuyIn = (TextView) findViewById(R.id.textLiveBuyInTotal);
        addOnAmount = (TextView) findViewById(R.id.editAddOn);

        locationValue = intent.getStringExtra("location");
        buyInValue = intent.getStringExtra("buyIn");
        sessionTypeValue = intent.getStringExtra("sessionType");
        sessionBlindsValue = intent.getStringExtra("sessionBlinds");

        totalBuyIn = Integer.parseInt(buyInValue);
        currentTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);

    }

    /*@Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            timer.setBase(savedInstanceState.getLong("savedTime"));
            timer.start();
        }
        buyInValue = Integer.toString(savedInstanceState.getInt("savedTotalBuyIn"));
        locationValue = savedInstanceState.getString("savedLocation");
        sessionTypeValue = savedInstanceState.getString("savedType");
        sessionBlindsValue = savedInstanceState.getString("savedBlinds");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("savedTime",timer.getBase());
        savedInstanceState.putInt("savedTotalBuyIn", totalBuyIn);
        savedInstanceState.putString("savedLocation", locationValue);
        savedInstanceState.putString("savedType", sessionTypeValue);
        savedInstanceState.putString("savedBlinds", sessionBlindsValue);
    }*/

    // Action When On Live Session Form Page
    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        editor.putBoolean("liveSessionActive", false);
        editor.commit();
        Log.d("@@@@@@@@@@@@@@@@@@@@@@@@ ETHAN @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@", Boolean.toString(prefs.getBoolean("liveSessionActive", false)));

    }

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void startTimer(View v)
    {
        started = true;
        if (!running)
        {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            timer.start();
            running = true;
        }
    }

    public void pauseTimer(View v)
    {
        if(running)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
        }
    }

    public void resetTimer(View v)
    {
        started = false;
        timer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        pauseTimer(v);
    }

    // Edit Session Entries
    public void onClickAddOnRebuy(View v)
    {
        if(addOnAmount.getText().toString().isEmpty())
        {
            Toast noRebuyEntry = Toast.makeText(getApplication(), "Please fill in the \"Add On ($)\" field", Toast.LENGTH_SHORT);
            noRebuyEntry.show();
            return;
        }
        int addOnValue = Integer.parseInt(addOnAmount.getText().toString());
        totalBuyIn += addOnValue;
        currentTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
        addOnAmount.setText("");
    }

    public void onClickDeleteLiveSession(View  v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    // Submit Live Session
    public void onClickSubmitLiveSession(View v)
    {
        long time = (SystemClock.elapsedRealtime() - timer.getBase()) / 60000;

        Toast zeroMinutes = Toast.makeText(getApplication(), "Session can't be less than a minute!", Toast.LENGTH_SHORT);
        Toast timerNotStarted = Toast.makeText(getApplication(), "Timer was not started!", Toast.LENGTH_SHORT);
        Toast noCashOutEntry = Toast.makeText(getApplication(), "Please fill in the \"Cash Out ($)\" field", Toast.LENGTH_SHORT);

        // Make Sure Cash Out Field is Filled, Timer is Started, Time is Over a Minute Long
        EditText editCashOut = (EditText) findViewById(R.id.editLiveCashOut);
        if(started == false)
        {
            timerNotStarted.show();
            return;
        }
        else if (editCashOut.getText().toString().isEmpty())
        {
            noCashOutEntry.show();
            return;
        }
        else if((int) time == 0)
        {
            zeroMinutes.show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String date = today.format(formatter);

        // Input Cash Out Entry
        int cashOut = Integer.parseInt(editCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(sessionTypeValue, sessionBlindsValue, locationValue, date, (int) time, totalBuyIn, cashOut);

        db.createSession(session);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}