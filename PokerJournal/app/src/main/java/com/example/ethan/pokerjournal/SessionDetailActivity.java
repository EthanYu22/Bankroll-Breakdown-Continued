package com.example.ethan.pokerjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Displays Session Details on a Separate Page
public class SessionDetailActivity extends AppCompatActivity
{

    DatabaseHelper db;
    Session session;
    int sessionId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        sessionId = MainActivity.sessionId;
        session = db.getSession(sessionId);
    }


    // Action When On Session Detail Page
    public void onResume()
    {
        super.onResume();
        sessionId = MainActivity.sessionId;
        session = db.getSession(sessionId);
        displayDetails();
    }

    /*
    // Action When Off Session Detail Page
    public void onPause() {
        super.onPause();
        finish();
    }*/

    // Functionality of Toolbar Back Arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Displays Session Details
    public void displayDetails()
    {
        double timeInHours = session.getTime() / 60.0;
        TextView hourlyRate = (TextView) findViewById(R.id.textSessionHourlyRate);
        TextView netProfit = (TextView) findViewById(R.id.textSessionNetProfit);
        TextView type = (TextView) findViewById(R.id.textSessionType);
        TextView blinds = (TextView) findViewById(R.id.textSessionBlinds);
        TextView location = (TextView) findViewById(R.id.textSessionLocation);
        TextView date = (TextView) findViewById(R.id.textSessionDate);
        TextView timeMins = (TextView) findViewById(R.id.textSessionTimeMins);
        TextView timeHours = (TextView) findViewById(R.id.textSessionTimeHours);
        TextView buyIn = (TextView) findViewById(R.id.textSessionBuyIn);
        TextView cashOut = (TextView) findViewById(R.id.textSessionCashOut);

        // Calculate Net Profit and Hourly Rate
        int nProfit = session.getCashOut() - session.getBuyIn();
        double hRate = nProfit / timeInHours;

        // Set Significant Figures to 2 for Net Profit, Hourly Rate, Buy In, and Cash Out
        String hR = String.format("%.2f", Math.abs(hRate));

        // Set Text for Each TextView
        if (hRate < 0)
        {
            hourlyRate.setText("Session Hourly Rate: -$" + hR);
        }
        else
        {
            hourlyRate.setText("Session Hourly Rate: $" + hR);
        }
        if (nProfit < 0)
        {
            netProfit.setText("Session Net Profit: -$" + Math.abs(nProfit));
        }
        else
        {
            netProfit.setText("Session Net Profit: $" + nProfit);
        }
        type.setText("Type: " + session.getType());
        blinds.setText("Blinds: " + session.getBlinds());
        location.setText("Location: " + session.getLocation());
        date.setText("Date: " + session.getConvertedDateMMddyyyy());
        timeMins.setText("Time in Minutes: " + session.getTime() + " mins");
        timeHours.setText("Time in Hours: " + String.format("%.2f", session.getTime()/60.0) + " hrs");
        buyIn.setText("Buy In: $" + session.getBuyIn());
        cashOut.setText("Cash Out: $" + session.getCashOut());
    }

    // Edit Session Entries
    public void onClickEditSession(View v)
    {
        sessionId = v.getId();
        Intent intent = new Intent(this, SessionEditActivity.class);
        startActivity(intent);
    }

    // Reset Button Resets Sessions
    public void onClickDeleteSession(View v)
    {

        // Confirmation Delete Session Alert
        AlertDialog.Builder altdial = new AlertDialog.Builder(SessionDetailActivity.this);
        altdial.setMessage("Do you want to delete this session?").setCancelable(false).setPositiveButton("Delete Session", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                db.deleteSession(sessionId);
                Toast toast = Toast.makeText(getApplication(), "Session Deleted", Toast.LENGTH_SHORT);
                toast.show();
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
        alert.setTitle("Delete Session");
        alert.show();

    }
}
