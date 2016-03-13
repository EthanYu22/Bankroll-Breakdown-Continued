package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GameDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
    }

    public void onResume() {
        super.onResume();
        TextView test = (TextView) findViewById(R.id.textTest);
        test.setText("" + MainActivity.gameId);
    }

    public void onPause() {
        super.onPause();
        finish();
    }
}
