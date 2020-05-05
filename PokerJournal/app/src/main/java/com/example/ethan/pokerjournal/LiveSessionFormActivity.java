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

public class LiveSessionFormActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    TextView inputLocation;
    TextView inputBuyIn;
    Spinner spinType;
    Spinner spinBlinds;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session_form);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputLocation = (TextView) findViewById(R.id.editLiveLocation);
        inputBuyIn = (TextView) findViewById(R.id.editLiveBuyIn);

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
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        EditText editLocation = (EditText) findViewById(R.id.editLiveLocation);
        if (editLocation.getText().toString().isEmpty())
        {
            toast.show();
            return;
        }

        EditText editBuyIn = (EditText) findViewById(R.id.editLiveBuyIn);
        if (editBuyIn.getText().toString().isEmpty())
        {
            toast.show();
            return;
        }

        String location = inputLocation.getText().toString();
        String buyIn = inputBuyIn.getText().toString();
        String sessionType = spinType.getSelectedItem().toString();
        String sessionBlinds = spinBlinds.getSelectedItem().toString();

        Intent intent = new Intent(this, LiveSessionTracker.class);
        intent.putExtra("location",location);
        intent.putExtra("buyIn",buyIn);
        intent.putExtra("sessionType", sessionType);
        intent.putExtra("sessionBlinds",sessionBlinds);
        startActivity(intent);
        finish();
    }
}
