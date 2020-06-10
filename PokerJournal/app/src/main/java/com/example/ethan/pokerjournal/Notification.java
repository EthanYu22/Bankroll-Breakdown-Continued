package com.example.ethan.pokerjournal;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class Notification extends Application {
    public static final String LIVE_SESSION_ID = "liveSession";
    public static final String TEST_ID = "testID";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel liveSession = new NotificationChannel(LIVE_SESSION_ID,
                    "Live Poker Session",
                    NotificationManager.IMPORTANCE_MAX
            );
            liveSession.setDescription("Bankroll Breakdown Live Session");

            NotificationChannel testSession = new NotificationChannel(TEST_ID,
                    "Test ID",
                    NotificationManager.IMPORTANCE_MAX
            );
            liveSession.setDescription("TEST ID");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(liveSession);
            manager.createNotificationChannel(testSession);
        }
    }
}