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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LiveSessionTracker extends AppCompatActivity
{
    private static final String LIVE_ACTIVE = "liveSessionActive";
    private static final String LIVE_STARTED = "liveSessionStarted";
    private static final String LIVE_RUNNING = "liveSessionRunning";
    private static final String LIVE_TIMER_BASE = "liveSessionTimerBase";
    private static final String LIVE_TYPE = "liveSessionType";
    private static final String LIVE_BLINDS = "liveSessionBlinds";
    private static final String LIVE_LOCATION = "liveSessionLocation";
    private static final String LIVE_BUY_IN = "liveSessionBuyIn";
    private static final String LIVE_PAUSE_OFFSET = "liveSessionPauseOffset";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private Toolbar toolbar;
    TextView currentTotalBuyIn;
    TextView addOnAmount;
    Button startButton;
    Button pauseButton;
    Button resetButton;

    String locationValue;
    String buyInValue;
    String sessionTypeValue;
    String sessionBlindsValue;

    int totalBuyIn;

    private Chronometer timer;
    private long pauseOffset;
    private long runningTimerBase;
    private boolean started = false;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_tracker);
        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        startButton = (Button) findViewById(R.id.timerStart);
        pauseButton = (Button) findViewById(R.id.timerPause);
        resetButton = (Button) findViewById(R.id.timerReset);
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
        if(prefs.getBoolean(LIVE_ACTIVE, false))
        {
            if(prefs.getBoolean(LIVE_RUNNING, true))
            {
                runningTimerBase = prefs.getLong(LIVE_TIMER_BASE, 0);
                timer.setBase(runningTimerBase);
                timer.start();
                startButton.setText("Running");
                startButton.setTextColor(getResources().getColor(R.color.white));
                startButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                running = true;
                started = true;
            }
            else
            {
                runningTimerBase = prefs.getLong(LIVE_TIMER_BASE, 0);
                pauseOffset = prefs.getLong(LIVE_PAUSE_OFFSET, 0);
                timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                started = prefs.getBoolean(LIVE_STARTED, false);
                if(started)
                {
                    pauseButton.setText("Paused");
                    pauseButton.setTextColor(getResources().getColor(R.color.white));
                    pauseButton.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                    startButton.setText("Resume");
                    startButton.setTextColor(getResources().getColor(R.color.black));
                    startButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
                else
                {
                    pauseButton.setText("Pause");
                    pauseButton.setTextColor(getResources().getColor(R.color.black));
                    pauseButton.setBackgroundColor(getResources().getColor(R.color.blue));
                    startButton.setText("Start");
                    startButton.setTextColor(getResources().getColor(R.color.black));
                    startButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        }
        editor.putBoolean(LIVE_ACTIVE, true);
        editor.commit();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        editor.putBoolean(LIVE_RUNNING, running);
        editor.putBoolean(LIVE_STARTED, started);
        editor.putLong(LIVE_TIMER_BASE, runningTimerBase);
        editor.putString(LIVE_TYPE, sessionTypeValue);
        editor.putString(LIVE_BLINDS, sessionBlindsValue);
        editor.putString(LIVE_LOCATION, locationValue);
        editor.putString(LIVE_BUY_IN, Integer.toString(totalBuyIn));
        if(running)
        {
            editor.putLong(LIVE_PAUSE_OFFSET, 0);
        }
        else
        {
            editor.putLong(LIVE_PAUSE_OFFSET, pauseOffset);
        }
        editor.commit();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        editor.putBoolean(LIVE_RUNNING, running);
        editor.putBoolean(LIVE_STARTED, started);
        editor.putLong(LIVE_TIMER_BASE, runningTimerBase);
        editor.putString(LIVE_TYPE, sessionTypeValue);
        editor.putString(LIVE_BLINDS, sessionBlindsValue);
        editor.putString(LIVE_LOCATION, locationValue);
        editor.putString(LIVE_BUY_IN, Integer.toString(totalBuyIn));
        if(running)
        {
            editor.putLong(LIVE_PAUSE_OFFSET, 0);
        }
        else
        {
            editor.putLong(LIVE_PAUSE_OFFSET, pauseOffset);
        }
        editor.commit();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
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
            running = true;
            runningTimerBase = SystemClock.elapsedRealtime() - pauseOffset;
            timer.setBase(runningTimerBase);
            timer.start();
            pauseOffset = 0;
            startButton.setText("Running");
            startButton.setTextColor(getResources().getColor(R.color.white));
            startButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            pauseButton.setText("Pause");
            pauseButton.setBackgroundColor(getResources().getColor(R.color.blue));
            pauseButton.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public void pauseTimer(View v)
    {
        if(running)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase(); // time passed since timer started and timer paused
            pauseButton.setText("Paused");
            pauseButton.setTextColor(getResources().getColor(R.color.white));
            pauseButton.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            startButton.setText("Resume");
            startButton.setTextColor(getResources().getColor(R.color.black));
            startButton.setBackgroundColor(getResources().getColor(R.color.green));
            running = false;
        }
    }

    public void resetTimer(View v)
    {
        timer.stop();
        runningTimerBase = SystemClock.elapsedRealtime();
        timer.setBase(runningTimerBase);
        pauseOffset = 0;
        pauseButton.setText("Pause");
        pauseButton.setTextColor(getResources().getColor(R.color.black));
        pauseButton.setBackgroundColor(getResources().getColor(R.color.blue));
        startButton.setText("Start");
        startButton.setTextColor(getResources().getColor(R.color.black));
        startButton.setBackgroundColor(getResources().getColor(R.color.green));
        started = false;
        running = false;
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
        editor.putBoolean(LIVE_ACTIVE, false);
        editor.commit();
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

        editor.putBoolean(LIVE_ACTIVE, false);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}