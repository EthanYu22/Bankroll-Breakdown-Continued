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

    private static final String LIVE_SESSION_ACTIVE = "liveSessionActive";
    private static final String LIVE_SESSION_TIME_STARTED = "liveSessionTimeStarted";
    private static final String LIVE_SESSION_TIME_RUNNING = "liveSessionTimeRunning";
    private static final String LIVE_SESSION_TIMER_BASE = "liveSessionTimerBase";
    private static final String LIVE_SESSION_PAUSE_OFFSET = "liveSessionPauseOffset";
    private static final String NOTIF_TITLE = "Live Session Time";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Chronometer timer;
    private long pauseOffset;
    private long runningTimerBase;
    private boolean timerRunning;
    private String sessionTimeStr;

    private NotificationManager notificationManager;
    private int notificationID = 25;
    private final IBinder mBinder = new LocalBinder();

    private NotificationCompat.Builder runningNotification;
    private NotificationCompat.Builder pausedNotification;
    private NotificationCompat.Builder newNotification;

    private Intent pauseTimer;
    private Intent resumeTimer;
    private Intent resetTimer;

    private PendingIntent contentIntent;
    private PendingIntent pauseActionIntent;
    private PendingIntent resumeActionIntent;
    private PendingIntent resetActionIntent;

    private TimerTask task;
    private Timer timerNotifUpdate;
    private boolean isTimerTaskRunning;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("Chronometer Service", "Chronometer Service onCreate Method Called");

        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();

        timer = new Chronometer(this);
        timer.setBase(SystemClock.elapsedRealtime());

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Service Intent Calls
        pauseTimer = new Intent(this, LiveSessionChronometerService.class);
        pauseTimer.putExtra("pauseTimer", true);
        resumeTimer = new Intent(this, LiveSessionChronometerService.class);
        resumeTimer.putExtra("resumeTimer", true);
        resetTimer = new Intent(this, LiveSessionChronometerService.class);
        resetTimer.putExtra("resetTimer", true);

        // The PendingIntent to launch our activity if the user selects this notification
        contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LiveSessionTracker.class), 0);
        pauseActionIntent = PendingIntent.getService(this, 1, pauseTimer, PendingIntent.FLAG_UPDATE_CURRENT);
        resumeActionIntent = PendingIntent.getService(this, 2, resumeTimer, PendingIntent.FLAG_UPDATE_CURRENT);
        resetActionIntent = PendingIntent.getService(this, 3, resetTimer, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        runningNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Pause", pauseActionIntent).addAction(R.mipmap.ic_launcher, "Reset", resetActionIntent).setOnlyAlertOnce(true);

        // Set the info for the views that show in the notification panel.
        pausedNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Resume", resumeActionIntent).addAction(R.mipmap.ic_launcher, "Reset", resetActionIntent).setOnlyAlertOnce(true);

        // Set the info for the views that show in the notification panel.
        newNotification = new NotificationCompat.Builder(this, LIVE_SESSION_ID).setSmallIcon(R.drawable.icon_larger)  // the status icon
                .setContentTitle(NOTIF_TITLE)  // the label of the entry
                .setShowWhen(false).setColor(getResources().getColor(R.color.colorPrimaryDark)).setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .addAction(R.mipmap.ic_launcher, "Start", resumeActionIntent).setOnlyAlertOnce(true);

        if (prefs.getBoolean(LIVE_SESSION_TIME_RUNNING, true))
        {
            runningTimerBase = prefs.getLong(LIVE_SESSION_TIMER_BASE, 0);
            timer.setBase(runningTimerBase);
            timerRunning = true;
        }
        else
        {
            runningTimerBase = prefs.getLong(LIVE_SESSION_TIMER_BASE, 0);
            pauseOffset = prefs.getLong(LIVE_SESSION_PAUSE_OFFSET, 0);
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("Chronometer Service", "Chronometer Service onStartCommand Method Called");

        sessionTimeStr = "00:00";
        newNotification.setContentText(sessionTimeStr);
        startForeground(notificationID, newNotification.build());

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

        if (!isTimerTaskRunning)
        {
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

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d("Chronometer Service", "Chronometer Service onDestroy Method Called");
        super.onDestroy();
        stopUpdates();
        stopSelf(); // Calls for service to close
    }

    private void stopUpdates()
    {
        if (isTimerTaskRunning)
        {
            task.cancel();
            timerNotifUpdate.cancel();
            timerNotifUpdate.purge();
            timerNotifUpdate = null;
            isTimerTaskRunning = false;
        }
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

    private void showNotification()
    {
        if (((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000) > 0)
        {
            sessionTimeStr = Integer.toString((int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 60000 / 1000));
        }
        else
        {
            sessionTimeStr = String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 60000 / 1000));
        }
        // Set the info for the views that show in the notification panel.
        runningNotification.setContentText(sessionTimeStr);
        notificationManager.notify(notificationID, runningNotification.build());
    }

    public void startTimer()
    {
        if (!timerRunning)
        {
            timerRunning = true;
            runningTimerBase = SystemClock.elapsedRealtime() - pauseOffset;
            timer.setBase(runningTimerBase);
            pauseOffset = 0;
        }
        if (((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000) > 0)
        {
            sessionTimeStr = Integer.toString((int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 60000 / 1000));
        }
        else
        {
            sessionTimeStr = String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((SystemClock.elapsedRealtime() - timer.getBase()) % 60000 / 1000));
        }
        runningNotification.setContentText(sessionTimeStr);
        notificationManager.notify(notificationID, runningNotification.build());
    }

    public void pauseTimer()
    {
        if (timerRunning)
        {
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase(); // time passed since timer started and timer paused
            timerRunning = false;
        }
        if (((SystemClock.elapsedRealtime() - timer.getBase()) / 3600000) > 0)
        {
            sessionTimeStr = Integer.toString((int) (pauseOffset / 3600000)) + ":" + String.format("%02d", (int) ((pauseOffset) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((pauseOffset) % 60000 / 1000));
        }
        else
        {
            sessionTimeStr = String.format("%02d", (int) ((pauseOffset) % 3600000 / 60000)) + ":" + String.format("%02d", (int) ((pauseOffset) % 60000 / 1000));
        }
        pausedNotification.setContentText(sessionTimeStr);
        notificationManager.notify(notificationID, pausedNotification.build());
    }

    public void resetTimer()
    {
        runningTimerBase = SystemClock.elapsedRealtime();
        timer.setBase(runningTimerBase);
        pauseOffset = 0;
        timerRunning = false;
        sessionTimeStr = "00:00";
        newNotification.setContentText(sessionTimeStr);
        notificationManager.notify(notificationID, newNotification.build());
    }

}
