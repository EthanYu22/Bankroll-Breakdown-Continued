package com.example.ethan.pokerjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String LIVE_DATE = "liveSessionDate";

    private Toolbar toolbar;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextView tvCurrTotalBuyIn;
    TextView tvAddOnAmount;
    Button startBtn;
    Button pauseBtn;
    Button resetBtn;

    String inputLocation;
    String inputBuyIn;
    String inputType;
    String inputBlinds;
    String inputDate;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();

        timer = (Chronometer) findViewById(R.id.timer);
        startBtn = (Button) findViewById(R.id.timerStartBtn);
        pauseBtn = (Button) findViewById(R.id.timerPauseBtn);
        resetBtn = (Button) findViewById(R.id.timerResetBtn);

        tvCurrTotalBuyIn = (TextView) findViewById(R.id.tvLiveBuyInTotal);
        tvAddOnAmount = (TextView) findViewById(R.id.etAddOn);

        Intent intent = getIntent();
        inputLocation = intent.getStringExtra("location");
        inputBuyIn = intent.getStringExtra("buyIn");
        inputType = intent.getStringExtra("sessionType");
        inputBlinds = intent.getStringExtra("sessionBlinds");
        inputDate = intent.getStringExtra("date");

        totalBuyIn = Integer.parseInt(inputBuyIn);
        tvCurrTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
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
            inputType = prefs.getString(LIVE_TYPE, "");
            inputBlinds = prefs.getString(LIVE_BLINDS, "");
            inputLocation = prefs.getString(LIVE_LOCATION, "");
            inputDate = prefs.getString(LIVE_DATE, "");
            if(prefs.getBoolean(LIVE_RUNNING, true))
            {
                runningTimerBase = prefs.getLong(LIVE_TIMER_BASE, 0);
                timer.setBase(runningTimerBase);
                timer.start();
                startBtn.setText("Running");
                timer.setTextColor(getResources().getColor(R.color.green));
                startBtn.setTextColor(getResources().getColor(R.color.white));
                startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
                    pauseBtn.setText("Paused");
                    timer.setTextColor(getResources().getColor(R.color.blue));
                    pauseBtn.setTextColor(getResources().getColor(R.color.white));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                    startBtn.setText("Resume");
                    startBtn.setTextColor(getResources().getColor(R.color.black));
                    startBtn.setBackgroundColor(getResources().getColor(R.color.green));
                }
                else
                {
                    pauseBtn.setText("Pause");
                    pauseBtn.setTextColor(getResources().getColor(R.color.black));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                    startBtn.setText("Start");
                    startBtn.setTextColor(getResources().getColor(R.color.black));
                    startBtn.setBackgroundColor(getResources().getColor(R.color.green));
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
        editor.putString(LIVE_TYPE, inputType);
        editor.putString(LIVE_BLINDS, inputBlinds);
        editor.putString(LIVE_LOCATION, inputLocation);
        editor.putString(LIVE_BUY_IN, Integer.toString(totalBuyIn));
        editor.putString(LIVE_DATE, inputDate);
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
        editor.putString(LIVE_TYPE, inputType);
        editor.putString(LIVE_BLINDS, inputBlinds);
        editor.putString(LIVE_LOCATION, inputLocation);
        editor.putString(LIVE_BUY_IN, Integer.toString(totalBuyIn));
        editor.putString(LIVE_DATE, inputDate);
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
            timer.setTextColor(getResources().getColor(R.color.green));
            startBtn.setText("Running");
            startBtn.setTextColor(getResources().getColor(R.color.white));
            startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            pauseBtn.setText("Pause");
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
            pauseBtn.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public void pauseTimer(View v)
    {
        if(running)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase(); // time passed since timer started and timer paused
            timer.setTextColor(getResources().getColor(R.color.blue));
            pauseBtn.setText("Paused");
            pauseBtn.setTextColor(getResources().getColor(R.color.white));
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            startBtn.setText("Resume");
            startBtn.setTextColor(getResources().getColor(R.color.black));
            startBtn.setBackgroundColor(getResources().getColor(R.color.green));
            running = false;
        }
    }

    public void resetTimer(View v)
    {
        timer.stop();
        runningTimerBase = SystemClock.elapsedRealtime();
        timer.setBase(runningTimerBase);
        pauseOffset = 0;
        timer.setTextColor(getResources().getColor(R.color.grey));
        pauseBtn.setText("Pause");
        pauseBtn.setTextColor(getResources().getColor(R.color.black));
        pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
        startBtn.setText("Start");
        startBtn.setTextColor(getResources().getColor(R.color.black));
        startBtn.setBackgroundColor(getResources().getColor(R.color.green));
        started = false;
        running = false;
    }

    // Edit Session Entries
    public void onClickAddOnRebuy(View v)
    {
        if(tvAddOnAmount.getText().toString().isEmpty())
        {
            Toast noRebuyEntry = Toast.makeText(getApplication(), "Please fill in the \"Add On ($)\" field", Toast.LENGTH_SHORT);
            noRebuyEntry.show();
            return;
        }
        int addOnValue = Integer.parseInt(tvAddOnAmount.getText().toString());
        totalBuyIn += addOnValue;
        tvCurrTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
        tvAddOnAmount.setText("");
    }

    public void onClickDeleteLiveSession(View  v)
    {
        // Confirmation Delete Session Alert
        AlertDialog.Builder altdial = new AlertDialog.Builder(LiveSessionTracker.this);
        altdial.setMessage("Do you want to delete this live session?").setCancelable(false).setPositiveButton("Delete Session", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                editor.putBoolean(LIVE_ACTIVE, false);
                editor.commit();
                Intent intent = new Intent(LiveSessionTracker.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("Go Back", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = altdial.create();
        alert.setTitle("Delete Live Session");
        alert.show();
    }

    // Submit Live Session
    public void onClickSubmitLiveSession(View v)
    {
        long sessionTime = (SystemClock.elapsedRealtime() - timer.getBase()) / 60000;

        Toast zeroMinutes = Toast.makeText(getApplication(), "Session can't be less than a minute!", Toast.LENGTH_SHORT);
        Toast timerNotStarted = Toast.makeText(getApplication(), "Timer was not started!", Toast.LENGTH_SHORT);
        Toast noCashOutEntry = Toast.makeText(getApplication(), "Please fill in the \"Cash Out ($)\" field", Toast.LENGTH_SHORT);

        // Make Sure Cash Out Field is Filled, Timer is Started, Time is Over a Minute Long
        EditText editCashOut = (EditText) findViewById(R.id.etLiveCashOut);
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
        else if((int) sessionTime == 0)
        {
            zeroMinutes.show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();

        int inputCashOut = Integer.parseInt(editCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(inputType, inputBlinds, inputLocation, inputDate, (int) sessionTime, totalBuyIn, inputCashOut);
        db.createSession(session);

        editor.putBoolean(LIVE_ACTIVE, false);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}