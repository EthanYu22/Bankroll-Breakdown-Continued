package com.example.ethan.pokerjournal;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity
{
    private Chronometer timer;
    private long pauseOffset;
    private boolean running;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timer = (Chronometer) findViewById(R.id.timer);
        output = (TextView) findViewById(R.id.output);
    }

    public void startTimer(View v)
    {
        if (!running)
        {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            output.setText(Long.toString((SystemClock.elapsedRealtime() - timer.getBase()) / 1000));
            timer.start();
            running = true;
        }
    }

    public void pauseTimer(View v)
    {
        if(running)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
        }
    }

    public void resetTimer(View v)
    {
        timer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

}
