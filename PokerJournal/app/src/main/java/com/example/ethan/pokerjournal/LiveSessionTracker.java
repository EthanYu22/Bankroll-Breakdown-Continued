package com.example.ethan.pokerjournal;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.ethan.pokerjournal.Notification.LIVE_SESSION_ID;
import static java.lang.Long.*;

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
    private static final String LIVE_SESSION_CHRONOMETER = "liveSessionChronometer";

    private static final String notifTitle = "Live Session Time";
    private static String notifMessage = "Hello";
    private static long elapsedTime;

    private Toolbar toolbar;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private NotificationManagerCompat notificationManager;
    private Handler handler;

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
    long sessionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_tracker);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationManager = NotificationManagerCompat.from(this);
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

        Log.d("WWW:ETHAN:YU","onCreateCalled");
        handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                sendLiveSessionNotification();
                handler.postDelayed(this, delay);
            }
        }, delay);

        startService();
    }

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
                startBtn.setTextColor(getResources().getColor(R.color.white));
                startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                running = true;
                started = true;

                sessionTime = SystemClock.elapsedRealtime() - runningTimerBase;
                notifMessage =  String.format("%02d", sessionTime/60) + ":" + String.format("%02d", sessionTime % 60);
                elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();

                sendLiveSessionNotification();
            }
            else
            {
                runningTimerBase = prefs.getLong(LIVE_TIMER_BASE, 0);
                pauseOffset = prefs.getLong(LIVE_PAUSE_OFFSET, 0);
                timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                started = prefs.getBoolean(LIVE_STARTED, false);
                sessionTime = SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - pauseOffset);
                notifMessage =  String.format("%02d", sessionTime/60) + ":" + String.format("%02d", sessionTime % 60);
                elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();

                if(started)
                {
                    pauseBtn.setText("Paused");
                    pauseBtn.setTextColor(getResources().getColor(R.color.white));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                    startBtn.setText("Resume");
                    startBtn.setTextColor(getResources().getColor(R.color.black));
                    startBtn.setBackgroundColor(getResources().getColor(R.color.green));
                    sendLiveSessionNotification();
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

        sessionTime = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;
        notifMessage =  String.format("%02d", sessionTime/60) + ":" + String.format("%02d", sessionTime % 60);
        elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();

        sendLiveSessionNotification();
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
        editor.putString(LIVE_DATE, inputDate);
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
        editor.putString(LIVE_TYPE, inputType);
        editor.putString(LIVE_BLINDS, inputBlinds);
        editor.putString(LIVE_LOCATION, inputLocation);
        editor.putString(LIVE_DATE, inputDate);
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

    public void startService()
    {
        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        serviceIntent.putExtra("LIVE_SESSION_CHRONOMETER", "Whatever");
        startService(serviceIntent);
    }

    public void stopStervice()
    {
        Intent serviceIntent = new Intent(this, LiveSessionChronometerService.class);
        stopService(serviceIntent);
    }

    public void sendLiveSessionNotification()
    {
        Intent activityIntent = new Intent(this, LiveSessionTracker.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activityIntent.putExtra("sessionType", prefs.getString("liveSessionType",""));
        activityIntent.putExtra("sessionBlinds",prefs.getString("liveSessionBlinds",""));
        activityIntent.putExtra("location", prefs.getString("liveSessionLocation", ""));
        activityIntent.putExtra("buyIn",prefs.getString("liveSessionBuyIn","0"));
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Intent activityIntent = new Intent(this, LiveSessionTracker.class);
         *         activityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
         *         activityIntent.putExtra("sessionType", prefs.getString("liveSessionType",""));
         *         activityIntent.putExtra("sessionBlinds",prefs.getString("liveSessionBlinds",""));
         *         activityIntent.putExtra("location", prefs.getString("liveSessionLocation", ""));
         *         activityIntent.putExtra("buyIn",prefs.getString("liveSessionBuyIn","0"));
         *         TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
         *         stackBuilder.addNextIntentWithParentStack(activityIntent);
         *         PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", "Hello World!");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(running){
            notifMessage = Integer.toString((int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 60000 / 1000));

        }else{
            notifMessage = Integer.toString((int) ((SystemClock.elapsedRealtime() - SystemClock.elapsedRealtime()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - SystemClock.elapsedRealtime()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - SystemClock.elapsedRealtime()) % 60000 / 1000));
        }

        Notification notification = new NotificationCompat.Builder(this, LIVE_SESSION_ID)
                .setSmallIcon(R.drawable.icon_larger)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setShowWhen(false)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setContentIntent(contentIntent)
                //.setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();
        notificationManager.notify(1, notification);
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
        editor.putString(LIVE_BUY_IN, Integer.toString(totalBuyIn));
        editor.commit();
        sendLiveSessionNotification();
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
                handler.removeCallbacksAndMessages(null);
                notificationManager.cancel(1);
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

        handler.removeCallbacksAndMessages(null);
        notificationManager.cancel(1);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}