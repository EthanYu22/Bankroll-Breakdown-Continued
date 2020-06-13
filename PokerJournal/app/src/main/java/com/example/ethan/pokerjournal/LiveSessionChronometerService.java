package com.example.ethan.pokerjournal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Chronometer;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ethan.pokerjournal.Notification.LIVE_SESSION_ID;


public class LiveSessionChronometerService extends Service
{
    // Live Session Shared Preference Constant Names
    private static final String NOTIF_TITLE = "Live Session Time";
    private static final String LIVE_SESSION_ACTIVE = "liveSessionActive";
    private static final String LIVE_SESSION_TIME_STARTED = "liveSessionTimeStarted";
    private static final String LIVE_SESSION_TIME_RUNNING = "liveSessionTimeRunning";
    private static final String LIVE_SESSION_TIMER_BASE = "liveSessionTimerBase";
    private static final String LIVE_SESSION_TIME_CURRENTLY_LOGGED = "liveSessionTimeCurrentlyLogged";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Chronometer Timer variables
    private Chronometer timerDisplay;
    private long timeCurrentlyLogged;
    private long runningTimerBase;
    private boolean timerStarted;
    private boolean timerRunning;
    private String sessionTime;

    // Notification Components
    private NotificationManager notificationManager;
    private static final int notificationID = 25;
    private Intent pauseTimer;
    private Intent resumeTimer;
    private Intent resetTimer;
    private PendingIntent contentIntent;
    private PendingIntent pauseActionIntent;
    private PendingIntent resumeActionIntent;
    private PendingIntent resetActionIntent;
    private NotificationCompat.Builder runningNotification;
    private NotificationCompat.Builder pausedNotification;
    private NotificationCompat.Builder newNotification;

    // Threading tasks
    private TimerTask task;
    private Timer timerNotifUpdate;
    private boolean isTimerTaskRunning;

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("Chronometer Service Lifecycle", "onCreate Method Called");

        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();

        timerDisplay = new Chronometer(this);
        timerDisplay.setBase(SystemClock.elapsedRealtime());

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Service Intent Calls
        pauseTimer = new Intent(this, LiveSessionChronometerService.class);
        pauseTimer.putExtra("pauseTimer", true);
        resumeTimer = new Intent(this, LiveSessionChronometerService.class);
        resumeTimer.putExtra("resumeTimer", true);
        resetTimer = new Intent(this, LiveSessionChronometerService.class);
        resetTimer.putExtra("resetTimer", true);

        // PendingIntent to launch our activity if the user selects this notification
        contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LiveSessionTracker.class), 0);
        // PendingIntent to launch this service activity
        pauseActionIntent = PendingIntent.getService(this, 1, pauseTimer, PendingIntent.FLAG_UPDATE_CURRENT);
        resumeActionIntent = PendingIntent.getService(this, 2, resumeTimer, PendingIntent.FLAG_UPDATE_CURRENT);
        resetActionIntent = PendingIntent.getService(this, 3, resetTimer, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the configuration for the foreground notification
        runningNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Pause", pauseActionIntent).addAction(R.mipmap.ic_launcher, "Reset", resetActionIntent).setOnlyAlertOnce(true);
        pausedNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Resume", resumeActionIntent).addAction(R.mipmap.ic_launcher, "Reset", resetActionIntent).setOnlyAlertOnce(true);
        newNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Start", resumeActionIntent).setOnlyAlertOnce(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Log.d("Chronometer Service Lifecycle", "onStartCommand Method Called");

        // Start off a new foreground notification
        sessionTime = "00:00";
        newNotification.setContentText(sessionTime);
        startForeground(notificationID, newNotification.build());

        // Call servie functions based on notification button clicks
        boolean pause = intent.getBooleanExtra("pauseTimer", false);
        boolean start = intent.getBooleanExtra("resumeTimer", false);
        boolean reset = intent.getBooleanExtra("resetTimer", false);
        if (start)
        {
            startTimer();
        }
        if (pause)
        {
            pauseTimer();
        }
        if (reset)
        {
            resetTimer();
        }

        // Run update notification live session timer thread when timer running
        if (!isTimerTaskRunning)
        {

            Log.d("Chronometer Service", "Timer Task Method Called");

            isTimerTaskRunning = true;
            task = new TimerTask()
            {
                private final Handler mHandler = new Handler(Looper.getMainLooper());

                @Override
                public void run()
                {
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (timerRunning)
                            {
                                showNotification();
                            }
                        }
                    });
                }
            };
            timerNotifUpdate = new Timer();
            timerNotifUpdate.scheduleAtFixedRate(task, 0, 1000);
        }

        // If service is destroyed, restart service redelivering last sent intent
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {

        Log.d("Chronometer Service Lifecycle", "onDestroy Method Called");

        super.onDestroy();
        stopThreadUpdates();
        stopSelf();
    }

    private void stopThreadUpdates()
    {

        Log.d("Chronometer Service", "stopThreadUpdates Method Called");

        if (isTimerTaskRunning)
        {
            task.cancel();
            timerNotifUpdate.cancel();
            timerNotifUpdate.purge();
            timerNotifUpdate = null;
            isTimerTaskRunning = false;
        }
    }

    private void showNotification()
    {

        Log.d("Chronometer Service", "showNotification Method Called");

        // Display hours
        if (((SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 3600000) > 0)
        {
            sessionTime = Integer.toString((int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 60000 / 1000));
        }
        // Display minutes and seconds
        else
        {
            sessionTime = String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 60000 / 1000));
        }

        runningNotification.setContentText(sessionTime);
        notificationManager.notify(notificationID, runningNotification.build());
    }

    public void startTimer()
    {

        Log.d("Chronometer Service", "startTimer Method Called");

        if (!timerRunning)
        {
            runningTimerBase = SystemClock.elapsedRealtime() - timeCurrentlyLogged;
            timerDisplay.setBase(runningTimerBase);

            timeCurrentlyLogged = 0;
            timerStarted = true;
            timerRunning = true;
        }

        // Display hours
        if (((SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 3600000) > 0)
        {
            sessionTime = Integer.toString((int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 60000 / 1000));
        }
        // Display minutes and seconds
        else
        {
            sessionTime = String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timerDisplay.getBase()) % 60000 / 1000));
        }

        runningNotification.setContentText(sessionTime);
        notificationManager.notify(notificationID, runningNotification.build());
    }

    public void pauseTimer()
    {

        Log.d("Chronometer Service", "pauseTimer Method Called");

        if (timerRunning)
        {
            timeCurrentlyLogged = SystemClock.elapsedRealtime() - timerDisplay.getBase(); // time passed since timer started and timer paused
            timerRunning = false;
        }

        // Display hours
        if (((SystemClock.elapsedRealtime() - timerDisplay.getBase()) / 3600000) > 0)
        {
            sessionTime = Integer.toString((int) (timeCurrentlyLogged / 3600000)) + ":" + String.format("%02d", (int) ((timeCurrentlyLogged) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((timeCurrentlyLogged) % 60000 / 1000));
        }
        // Display minutes and seconds
        else
        {
            sessionTime = String.format("%02d", (int) ((timeCurrentlyLogged) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((timeCurrentlyLogged) % 60000 / 1000));
        }

        pausedNotification.setContentText(sessionTime);
        notificationManager.notify(notificationID, pausedNotification.build());
    }

    public void resetTimer()
    {

        Log.d("Chronometer Service", "resetTimer Method Called");

        runningTimerBase = SystemClock.elapsedRealtime();
        timerDisplay.setBase(runningTimerBase);

        sessionTime = "00:00";
        timeCurrentlyLogged = 0;
        timerStarted = false;
        timerRunning = false;

        newNotification.setContentText(sessionTime);
        notificationManager.notify(notificationID, newNotification.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public class LocalBinder extends Binder
    {
        LiveSessionChronometerService getService()
        {
            return LiveSessionChronometerService.this;
        }
    }

    public String sayHello()
    {
        return "Hello";
    }
}
