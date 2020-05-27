package com.example.ethan.pokerjournal;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {
    public static final String LIVE_SESSION_ID = "liveSession";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(LIVE_SESSION_ID,
                    "Live Session - Live Session Time: ",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("Bankroll Breakdown Live Session");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}