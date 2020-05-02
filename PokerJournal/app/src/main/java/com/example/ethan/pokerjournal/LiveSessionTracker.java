package com.example.ethan.pokerjournal;

import android.content.Intent;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        timer = (Chronometer) findViewById(R.id.timer);

        currentTotalBuyIn = (TextView) findViewById(R.id.textLiveBuyInTotal);
        addOnAmount = (TextView) findViewById(R.id.editAddOn);

        locationValue = intent.getStringExtra("location");
        buyInValue = intent.getStringExtra("buyIn");;
        sessionTypeValue = intent.getStringExtra("sessionType");;
        sessionBlindsValue = intent.getStringExtra("sessionBlinds");;

        totalBuyIn = Integer.parseInt(buyInValue);
        currentTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);

    }

    // Action When On Live Session Form Page
    public void onResume()
    {
        super.onResume();
    }

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            this.finish();
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
        int addOnValue = Integer.parseInt(addOnAmount.getText().toString());
        totalBuyIn += addOnValue;
        currentTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
        addOnAmount.setText("");
    }

    // Submit live Session
    public void onClickLiveSession(View v)
    {
        long time = (SystemClock.elapsedRealtime() - timer.getBase()) / 60000;
        Toast noCashOutEntry = Toast.makeText(getApplication(), "Please fill in the \"Cash Out ($)\" field", Toast.LENGTH_SHORT);
        Toast timerNotStarted = Toast.makeText(getApplication(), "Timer was not started!", Toast.LENGTH_SHORT);
        if(started == false)
        {
            timerNotStarted.show();
            return;
        }
        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String date = today.format(formatter);

        // Get Cash Out and Make Sure it's Valid
        EditText editCashOut = (EditText) findViewById(R.id.editLiveCashOut);
        if (editCashOut.getText().toString().isEmpty())
        {
            noCashOutEntry.show();
            return;
        }
        // Input Cash Out Entry
        int cashOut = Integer.parseInt(editCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(sessionTypeValue, sessionBlindsValue, locationValue, date, (int) (long) time, totalBuyIn, cashOut);

        db.createSession(session);

        Intent intent = new Intent(LiveSessionTracker.this, MainActivity.class);
        startActivity(intent);
    }
}