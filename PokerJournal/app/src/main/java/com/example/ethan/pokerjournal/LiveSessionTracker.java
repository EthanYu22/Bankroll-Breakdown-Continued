package com.example.ethan.pokerjournal;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LiveSessionTracker extends AppCompatActivity
{
    // Live Session Shared Preference Constant Names
    private static final String ONLY_ALLOW_ONCE = "onlyAllowOnce";
    private static final String LIVE_SESSION_TYPE = "liveSessionType";
    private static final String LIVE_SESSION_BLINDS = "liveSessionBlinds";
    private static final String LIVE_SESSION_LOCATION = "liveSessionLocation";
    private static final String LIVE_SESSION_BUY_IN = "liveSessionBuyIn";
    private static final String LIVE_SESSION_DATE = "liveSessionDate";
    private static final String LIVE_SESSION_ACTIVE = "liveSessionActive";
    private static final String LIVE_SESSION_TIME_STARTED = "liveSessionTimeStarted";
    private static final String LIVE_SESSION_TIME_RUNNING = "liveSessionTimeRunning";
    private static final String LIVE_SESSION_TIMER_BASE = "liveSessionTimerBase";
    private static final String LIVE_SESSION_TIME_CURRENTLY_LOGGED = "liveSessionTimeCurrentlyLogged";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // GUI components
    private Toolbar toolbar;
    private TextView tvCurrTotalBuyIn;
    private TextView tvAddOnAmount;
    private Button startBtn;
    private Button pauseBtn;
    private Button resetBtn;

    // Poker Session variables
    private String inputLocation;
    private String inputBuyIn;
    private String inputType;
    private String inputBlinds;
    private String inputDate;
    private int totalBuyIn;

    // Chronometer Timer variables
    Chronometer timerDisplay;
    private long timeCurrentlyLogged;
    private long runningTimerBase;
    private boolean timerStarted;
    private boolean timerRunning;

    // Service components
    private boolean mShouldUnbind;
    private boolean serviceStarted;
    private LiveSessionChronometerService mBoundService; // To invoke the bound service, first make sure that this value is not null.
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_tracker);

        Log.d("LiveSessionTracker Lifecycle", "LiveSessionTracker onCreate Method Called");

        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();

        // GUI components
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timerDisplay = (Chronometer) findViewById(R.id.timer);
        startBtn = (Button) findViewById(R.id.timerStartBtn);
        pauseBtn = (Button) findViewById(R.id.timerPauseBtn);
        resetBtn = (Button) findViewById(R.id.timerResetBtn);
        tvCurrTotalBuyIn = (TextView) findViewById(R.id.tvLiveBuyInTotal);
        tvAddOnAmount = (TextView) findViewById(R.id.etAddOn);

        // Get session variable data
        Intent intent = getIntent();
        inputLocation = intent.getStringExtra("location");
        inputBuyIn = intent.getStringExtra("buyIn");
        inputType = intent.getStringExtra("sessionType");
        inputBlinds = intent.getStringExtra("sessionBlinds");
        inputDate = intent.getStringExtra("date");
        if (inputBuyIn == null)
        {
            totalBuyIn = Integer.parseInt(prefs.getString("liveSessionBuyIn", "0"));
        }
        else
        {
            totalBuyIn = Integer.parseInt(inputBuyIn);

        }
        tvCurrTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
        editor.putString(LIVE_SESSION_BUY_IN, Integer.toString(totalBuyIn));

        // Only input these values into shared preferences once per live poker session
        boolean onlyAllowOnce = prefs.getBoolean("onlyAllowOnce", true);
        if (onlyAllowOnce)
        {
            onlyAllowOnce = false;
            editor.putBoolean(ONLY_ALLOW_ONCE, onlyAllowOnce);
            editor.putString(LIVE_SESSION_TYPE, inputType);
            editor.putString(LIVE_SESSION_BLINDS, inputBlinds);
            editor.putString(LIVE_SESSION_LOCATION, inputLocation);
            editor.putString(LIVE_SESSION_DATE, inputDate);
        }
        editor.commit();

        // Service
        mConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = ((LiveSessionChronometerService.LocalBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d("LiveSessionTracker Lifecycle", "LiveSessionTracker onStart Method Called");

        if (!serviceStarted)
        {
            Log.d("onStart", "Starting Service");
            startService();
        }
        if (!mShouldUnbind)
        {
            Log.d("onStart", "Binding Service");
            doBindService();
        }
    }

    // Action When On Live Session Form Page
    @Override
    public void onResume()
    {
        super.onResume();

        Log.d("LiveSessionTracker Lifecycle", "LiveSessionTracker onResume Method Called");

        // Fill in variables onResume if returning to running live session
        if (prefs.getBoolean(LIVE_SESSION_ACTIVE, false))
        {
            inputType = prefs.getString(LIVE_SESSION_TYPE, "");
            inputBlinds = prefs.getString(LIVE_SESSION_BLINDS, "");
            inputLocation = prefs.getString(LIVE_SESSION_LOCATION, "");
            inputDate = prefs.getString(LIVE_SESSION_DATE, "");

            // If session time is running
            if (prefs.getBoolean(LIVE_SESSION_TIME_RUNNING, true))
            {
                runningTimerBase = prefs.getLong(LIVE_SESSION_TIMER_BASE, 0);
                timerDisplay.setBase(runningTimerBase);
                timerDisplay.start();

                startBtn.setText("Running");
                startBtn.setTextColor(getResources().getColor(R.color.white));
                startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                timerRunning = true;
                timerStarted = true;
            }
            else
            {
                timeCurrentlyLogged = prefs.getLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, 0);
                timerDisplay.setBase(SystemClock.elapsedRealtime() - timeCurrentlyLogged);
                timerStarted = prefs.getBoolean(LIVE_SESSION_TIME_STARTED, false);

                // If session timer is paused
                if (timerStarted)
                {
                    pauseBtn.setText("Paused");
                    pauseBtn.setTextColor(getResources().getColor(R.color.white));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                    startBtn.setText("Resume");
                    startBtn.setTextColor(getResources().getColor(R.color.black));
                    startBtn.setBackgroundColor(getResources().getColor(R.color.green));
                }
                // If session timer hasn't been started
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
        editor.putBoolean(LIVE_SESSION_ACTIVE, true);
        editor.commit();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Log.d("LiveSessionTracker Lifecycle", "LiveSessionTracker onPause Method Called");

        if (mShouldUnbind)
        {
            Log.d("onPause", "Unbinding Service");
            doUnbindService();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.d("LiveSessionTracker Lifecycle", "LiveSessionTracker onDestroy Method Called");

        if (mShouldUnbind)
        {
            Log.d("onDestroy", "Unbinding Service");
            doUnbindService();
        }
    }

    @Override
    public void onBackPressed()
    {

        Log.d("LiveSessionTracker", "onBackPressed Method Called");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    // Toolbar back arrow returns to home page
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {

        Log.d("LiveSessionTracker", "Toolbar back arrow Method Called");

        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Attempts to establish a connection with the service.  We use an
    // explicit class name because we want a specific service
    // implementation that we know will be running in our own process
    // (and thus won't be supporting component replacement by other
    // applications).
    void doBindService()
    {

        Log.d("LiveSessionTracker", "doBindService Method Called");

        if (bindService(new Intent(LiveSessionTracker.this, LiveSessionChronometerService.class), mConnection, Context.BIND_AUTO_CREATE))
        {
            mShouldUnbind = true;
        }
        else
        {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " + "exist, or this client isn't allowed access to it.");
        }
    }

    void doUnbindService()
    {

        Log.d("LiveSessionTracker", "doUnbindService Method Called");

        if (mShouldUnbind)
        {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    public void startService()
    {

        Log.d("LiveSessionTracker", "startService Method Called");

        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        startService(serviceIntent);
        serviceStarted = true;
    }

    public void stopService()
    {

        Log.d("LiveSessionTracker", "stopService Method Called");

        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        stopService(serviceIntent);
        serviceStarted = false;
    }

    public void startTimer(View v)
    {

        Log.d("LiveSessionTracker", "startTimer Method Called");

        // Start service notification timer
        mBoundService.startTimer();

        if (!timerRunning)
        {
            runningTimerBase = SystemClock.elapsedRealtime() - timeCurrentlyLogged;
            timerDisplay.setBase(runningTimerBase);
            timerDisplay.start();

            startBtn.setText("Running");
            startBtn.setTextColor(getResources().getColor(R.color.white));
            startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            pauseBtn.setText("Pause");
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
            pauseBtn.setTextColor(getResources().getColor(R.color.black));

            timeCurrentlyLogged = 0;
            timerStarted = true;
            timerRunning = true;

            editor.putLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, 0);
            editor.putLong(LIVE_SESSION_TIMER_BASE, runningTimerBase);
            editor.putBoolean(LIVE_SESSION_TIME_STARTED, timerStarted);
            editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
            editor.commit();
        }
    }

    public void pauseTimer(View v)
    {

        Log.d("LiveSessionTracker", "pauseTimer Method Called");

        // Pause service notification timer
        mBoundService.pauseTimer();

        if (timerRunning)
        {
            timerDisplay.stop();

            pauseBtn.setText("Paused");
            pauseBtn.setTextColor(getResources().getColor(R.color.white));
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            startBtn.setText("Resume");
            startBtn.setTextColor(getResources().getColor(R.color.black));
            startBtn.setBackgroundColor(getResources().getColor(R.color.green));

            timeCurrentlyLogged = SystemClock.elapsedRealtime() - timerDisplay.getBase(); // time passed since timer started and timer paused
            timerRunning = false;

            editor.putLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, timeCurrentlyLogged);
            editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
            editor.commit();
        }
    }

    public void resetTimer(View v)
    {

        Log.d("LiveSessionTracker", "resetTimer Method Called");

        // Reset service notification timer
        mBoundService.resetTimer();

        timerDisplay.stop();
        runningTimerBase = SystemClock.elapsedRealtime();
        timerDisplay.setBase(runningTimerBase);

        pauseBtn.setText("Pause");
        pauseBtn.setTextColor(getResources().getColor(R.color.black));
        pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
        startBtn.setText("Start");
        startBtn.setTextColor(getResources().getColor(R.color.black));
        startBtn.setBackgroundColor(getResources().getColor(R.color.green));

        timeCurrentlyLogged = 0;
        timerStarted = false;
        timerRunning = false;

        editor.putLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, 0);
        editor.putLong(LIVE_SESSION_TIMER_BASE, runningTimerBase);
        editor.putBoolean(LIVE_SESSION_TIME_STARTED, timerStarted);
        editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
        editor.commit();
    }

    public void onClickAddOnRebuy(View v)
    {

        Log.d("LiveSessionTracker", "onClickAddOnRebuy Method Called");

        if (tvAddOnAmount.getText().toString().isEmpty())
        {
            Toast noRebuyEntry = Toast.makeText(getApplication(), "Please fill in the \"Add On ($)\" field", Toast.LENGTH_SHORT);
            noRebuyEntry.show();
            return;
        }

        int addOnValue = Integer.parseInt(tvAddOnAmount.getText().toString());
        totalBuyIn += addOnValue;

        tvCurrTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);
        tvAddOnAmount.setText("");

        editor.putString(LIVE_SESSION_BUY_IN, Integer.toString(totalBuyIn));
        editor.commit();
    }

    public void onClickDeleteLiveSession(View v)
    {

        Log.d("LiveSessionTracker", "onClickDeleteLiveSession Method Called");

        // Confirmation Delete Session Alert
        AlertDialog.Builder dialogDeleteSession = new AlertDialog.Builder(LiveSessionTracker.this);
        dialogDeleteSession.setMessage("Do you want to delete this live session?")
                .setCancelable(false)
                .setPositiveButton("Delete Session", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which)
                            {

                                Log.d("LiveSessionTracker", "Confirm onClickDeleteLiveSession Method Chosen");

                                if (mShouldUnbind)
                                {
                                    Log.d("onDestroy", "Unbinding Service");
                                    doUnbindService();
                                }
                                Log.d("onDestroy", "Stopping Service");
                                stopService();

                                // Reset all shared preferences data
                                editor.putBoolean(ONLY_ALLOW_ONCE, true);
                                editor.putBoolean(LIVE_SESSION_ACTIVE, false);
                                editor.putBoolean(LIVE_SESSION_TIME_RUNNING, false);
                                editor.putBoolean(LIVE_SESSION_TIME_STARTED, false);
                                editor.putLong(LIVE_SESSION_TIMER_BASE, 0);
                                editor.putString(LIVE_SESSION_TYPE, "");
                                editor.putString(LIVE_SESSION_BLINDS, "");
                                editor.putString(LIVE_SESSION_LOCATION, "");
                                editor.putString(LIVE_SESSION_DATE, "");
                                editor.putString(LIVE_SESSION_BUY_IN, "");
                                editor.putLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, 0);
                                editor.commit();

                                Intent intent = new Intent(LiveSessionTracker.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                        }
                ).setNegativeButton("Go Back", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {

                        Log.d("LiveSessionTracker", "Cancel onClickDeleteLiveSession Method Chosen");

                        dialogInterface.cancel();
                    }
                }
        );

        AlertDialog alert = dialogDeleteSession.create();
        alert.setTitle("Delete Live Session");
        alert.show();
    }

    public void onClickSubmitLiveSession(View v)
    {

        Log.d("LiveSessionTracker", "onClickSubmitLiveSession Method Called");

        long inputSessionTime;
        if(timerRunning)
        {
            inputSessionTime = (SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 60000;
        }
        else
        {
            inputSessionTime = (SystemClock.elapsedRealtime() - timeCurrentlyLogged) / 60000;
        }

        Toast zeroMinutes = Toast.makeText(getApplication(), "Session can't be less than a minute!", Toast.LENGTH_SHORT);
        Toast timerNotStarted = Toast.makeText(getApplication(), "Timer was not started!", Toast.LENGTH_SHORT);
        Toast noCashOutEntry = Toast.makeText(getApplication(), "Please fill in the \"Cash Out ($)\" field", Toast.LENGTH_SHORT);

        // Make sure cash out field is filled, timer is started, and time is over a minute long
        EditText editCashOut = (EditText) findViewById(R.id.etLiveCashOut);
        if (timerStarted == false)
        {
            timerNotStarted.show();
            return;
        }
        else if (editCashOut.getText().toString().isEmpty())
        {
            noCashOutEntry.show();
            return;
        }
        else if ((int) inputSessionTime == 0)
        {
            zeroMinutes.show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();

        int inputCashOut = Integer.parseInt(editCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(inputType, inputBlinds, inputLocation, inputDate, (int) inputSessionTime, totalBuyIn, inputCashOut);
        db.createSession(session);

        if (mShouldUnbind)
        {
            Log.d("onDestroy", "Unbinding Service");
            doUnbindService();
        }
        Log.d("onDestroy", "Stopping Service");
        stopService();

        editor.putBoolean(ONLY_ALLOW_ONCE, true);
        editor.putBoolean(LIVE_SESSION_ACTIVE, false);
        editor.putBoolean(LIVE_SESSION_TIME_RUNNING, false);
        editor.putBoolean(LIVE_SESSION_TIME_STARTED, false);
        editor.putLong(LIVE_SESSION_TIMER_BASE, 0);
        editor.putString(LIVE_SESSION_TYPE, "");
        editor.putString(LIVE_SESSION_BLINDS, "");
        editor.putString(LIVE_SESSION_LOCATION, "");
        editor.putString(LIVE_SESSION_DATE, "");
        editor.putString(LIVE_SESSION_BUY_IN, "");
        editor.putLong(LIVE_SESSION_TIME_CURRENTLY_LOGGED, 0);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}