package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LiveSessionFormActivity extends AppCompatActivity
{
    private Toolbar toolbar;

    Spinner spinType;
    Spinner spinBlinds;
    EditText etLocation;
    EditText etBuyIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_form);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etLocation = (EditText) findViewById(R.id.etLiveLocation);
        etBuyIn = (EditText) findViewById(R.id.etLiveBuyIn);

        spinType = (Spinner) findViewById(R.id.spinnerLiveSessionType);
        spinBlinds = (Spinner) findViewById(R.id.spinnerLiveSessionBlinds);
        spinBlinds.setSelection(4);
    }

    // Action When On Live Session Form Page
    public void onResume()
    {
        super.onResume();
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

    // Live Session Button Leads to Live Session Form
    public void onClickLiveSessionTracker(View v)
    {
        Toast fillOutFields = Toast.makeText(getApplication(), "Please fill out all the fields", Toast.LENGTH_SHORT);
        if (etLocation.getText().toString().isEmpty() || etBuyIn.getText().toString().isEmpty())
        {
            fillOutFields.show();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String inputDate = today.format(formatter);

        Intent intent = new Intent(this, LiveSessionTracker.class);
        intent.putExtra("sessionType", spinType.getSelectedItem().toString());
        intent.putExtra("sessionBlinds", spinBlinds.getSelectedItem().toString());
        intent.putExtra("location", etLocation.getText().toString());
        intent.putExtra("buyIn", etBuyIn.getText().toString());
        intent.putExtra("date", inputDate);
        startActivity(intent);
        finish();
    }
}
