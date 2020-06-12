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

    private static final String ONLY_ALLOW_ONCE = "onlyAllowOnce";
    private static final String LIVE_SESSION_ACTIVE = "liveSessionActive";
    private static final String LIVE_SESSION_TIME_STARTED = "liveSessionTimeStarted";
    private static final String LIVE_SESSION_TIME_RUNNING = "liveSessionTimeRunning";
    private static final String LIVE_SESSION_TIMER_BASE = "liveSessionTimerBase";
    private static final String LIVE_SESSION_TYPE = "liveSessionType";
    private static final String LIVE_SESSION_BLINDS = "liveSessionBlinds";
    private static final String LIVE_SESSION_LOCATION = "liveSessionLocation";
    private static final String LIVE_SESSION_BUY_IN = "liveSessionBuyIn";
    private static final String LIVE_SESSION_PAUSE_OFFSET = "liveSessionPauseOffset";
    private static final String LIVE_SESSION_DATE = "liveSessionDate";
    private static final String NOTIF_TITLE = "Live Session Time";

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
    private boolean timerStarted;
    private boolean timerRunning;
    long sessionTime;

    private boolean mShouldUnbind;
    private boolean serviceStarted;
    private LiveSessionChronometerService mBoundService; // To invoke the bound service, first make sure that this value is not null.
    private ServiceConnection mConnection;

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

        if (inputBuyIn == null)
        {
            totalBuyIn = Integer.parseInt(prefs.getString("liveSessionBuyIn", "0"));
        }
        else
        {
            totalBuyIn = Integer.parseInt(inputBuyIn);

        }
        tvCurrTotalBuyIn.setText("Current Buy In Total: $" + totalBuyIn);

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
        editor.putString(LIVE_SESSION_BUY_IN, Integer.toString(totalBuyIn));
        editor.commit();

        Log.d("LiveSessionTracker", "LiveSessionTracker onCreate Method Called");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("LiveSessionTracker", "LiveSessionTracker onStart Method Called");
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
        if (prefs.getBoolean(LIVE_SESSION_ACTIVE, false))
        {
            inputType = prefs.getString(LIVE_SESSION_TYPE, "");
            inputBlinds = prefs.getString(LIVE_SESSION_BLINDS, "");
            inputLocation = prefs.getString(LIVE_SESSION_LOCATION, "");
            inputDate = prefs.getString(LIVE_SESSION_DATE, "");
            if (prefs.getBoolean(LIVE_SESSION_TIME_RUNNING, true))
            {
                runningTimerBase = prefs.getLong(LIVE_SESSION_TIMER_BASE, 0);
                timer.setBase(runningTimerBase);
                timer.start();
                startBtn.setText("Running");
                startBtn.setTextColor(getResources().getColor(R.color.white));
                startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                timerRunning = true;
                timerStarted = true;

                sessionTime = SystemClock.elapsedRealtime() - runningTimerBase;
            }
            else
            {
                runningTimerBase = prefs.getLong(LIVE_SESSION_TIMER_BASE, 0);
                pauseOffset = prefs.getLong(LIVE_SESSION_PAUSE_OFFSET, 0);
                timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                timerStarted = prefs.getBoolean(LIVE_SESSION_TIME_STARTED, false);
                sessionTime = SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - pauseOffset);

                if (timerStarted)
                {
                    pauseBtn.setText("Paused");
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
        editor.putBoolean(LIVE_SESSION_ACTIVE, true);
        editor.commit();

        sessionTime = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (timerRunning)
        {
            editor.putLong(LIVE_SESSION_PAUSE_OFFSET, 0);
        }
        else
        {
            editor.putLong(LIVE_SESSION_PAUSE_OFFSET, pauseOffset);
        }
        editor.commit();

        Log.d("LiveSessionTracker", "LiveSessionTracker onPause Method Called");
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
        if (timerRunning)
        {
            editor.putLong(LIVE_SESSION_PAUSE_OFFSET, 0);
        }
        else
        {
            editor.putLong(LIVE_SESSION_PAUSE_OFFSET, pauseOffset);
        }
        editor.commit();

        Log.d("LiveSessionTracker", "LiveSessionTracker onDestroy Method Called");
        if (mShouldUnbind)
        {
            Log.d("onDestroy", "Unbinding Service");
            doUnbindService();
        }
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

    void doBindService()
    {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        Log.d("BIND SERVICE", "Chronometer Service is Bound");
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
        if (mShouldUnbind)
        {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    public void startService()
    {
        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        startService(serviceIntent);
        serviceStarted = true;
    }

    public void stopService()
    {
        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        stopService(serviceIntent);
        serviceStarted = false;
    }

    public void startTimer(View v)
    {
        mBoundService.startTimer();
        if (!timerRunning)
        {
            runningTimerBase = SystemClock.elapsedRealtime() - pauseOffset;
            timer.setBase(runningTimerBase);
            timer.start();
            pauseOffset = 0;
            startBtn.setText("Running");
            startBtn.setTextColor(getResources().getColor(R.color.white));
            startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            pauseBtn.setText("Pause");
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
            pauseBtn.setTextColor(getResources().getColor(R.color.black));

            timerStarted = true;
            timerRunning = true;
            editor.putLong(LIVE_SESSION_TIMER_BASE, runningTimerBase);
            editor.putBoolean(LIVE_SESSION_TIME_STARTED, timerStarted);
            editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
            editor.commit();
        }
    }

    public void pauseTimer(View v)
    {
        mBoundService.pauseTimer();
        if (timerRunning)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase(); // time passed since timer started and timer paused
            pauseBtn.setText("Paused");
            pauseBtn.setTextColor(getResources().getColor(R.color.white));
            pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            startBtn.setText("Resume");
            startBtn.setTextColor(getResources().getColor(R.color.black));
            startBtn.setBackgroundColor(getResources().getColor(R.color.green));

            timerRunning = false;
            editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
            editor.commit();
        }
    }

    public void resetTimer(View v)
    {
        mBoundService.resetTimer();
        timer.stop();
        runningTimerBase = SystemClock.elapsedRealtime();
        timer.setBase(runningTimerBase);
        pauseOffset = 0;
        pauseBtn.setText("Pause");
        pauseBtn.setTextColor(getResources().getColor(R.color.black));
        pauseBtn.setBackgroundColor(getResources().getColor(R.color.blue));
        startBtn.setText("Start");
        startBtn.setTextColor(getResources().getColor(R.color.black));
        startBtn.setBackgroundColor(getResources().getColor(R.color.green));

        timerStarted = false;
        timerRunning = false;
        editor.putLong(LIVE_SESSION_TIMER_BASE, runningTimerBase);
        editor.putBoolean(LIVE_SESSION_TIME_STARTED, timerStarted);
        editor.putBoolean(LIVE_SESSION_TIME_RUNNING, timerRunning);
        editor.commit();
    }

    // Edit Session Entries
    public void onClickAddOnRebuy(View v)
    {
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
        // Confirmation Delete Session Alert
        AlertDialog.Builder altdial = new AlertDialog.Builder(LiveSessionTracker.this);
        altdial.setMessage("Do you want to delete this live session?").setCancelable(false).setPositiveButton("Delete Session", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
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
                editor.putLong(LIVE_SESSION_PAUSE_OFFSET, 0);
                editor.commit();

                Log.d("LiveSessionTracker", "LiveSessionTracker onClickDeleteLiveSession Method Called");
                if (mShouldUnbind)
                {
                    Log.d("onDestroy", "Unbinding Service");
                    doUnbindService();
                }
                Log.d("onDestroy", "Stopping Service");
                stopService();

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
        sessionTime = (SystemClock.elapsedRealtime() - timer.getBase()) / 60000;

        Toast zeroMinutes = Toast.makeText(getApplication(), "Session can't be less than a minute!", Toast.LENGTH_SHORT);
        Toast timerNotStarted = Toast.makeText(getApplication(), "Timer was not started!", Toast.LENGTH_SHORT);
        Toast noCashOutEntry = Toast.makeText(getApplication(), "Please fill in the \"Cash Out ($)\" field", Toast.LENGTH_SHORT);

        // Make Sure Cash Out Field is Filled, Timer is Started, Time is Over a Minute Long
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
        else if ((int) sessionTime == 0)
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
        editor.putLong(LIVE_SESSION_PAUSE_OFFSET, 0);
        editor.commit();

        Log.d("LiveSessionTracker", "LiveSessionTracker onClickSubmitLiveSession Method Called");
        if (mShouldUnbind)
        {
            Log.d("onDestroy", "Unbinding Service");
            doUnbindService();
        }
        Log.d("onDestroy", "Stopping Service");
        stopService();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}