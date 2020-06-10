package com.example.ethan.pokerjournal;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.example.ethan.pokerjournal.Notification.TEST_ID;


public class LiveSessionChronometerService extends Service
{

    private NotificationManagerCompat notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        notificationManager = NotificationManagerCompat.from(this);
        String notifTitle = "hello";
        String notifMessage = intent.getStringExtra("liveSessionChronometer");
        Notification notification = new NotificationCompat.Builder(this, TEST_ID)
                .setSmallIcon(R.drawable.icon_larger)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setShowWhen(false)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                //.setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(2, notification);

        return START_STICKY;
    }
    /**
        handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                sendLiveSessionNotification();
                handler.postDelayed(this, delay);
            }
        }, delay);
     */

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

}
