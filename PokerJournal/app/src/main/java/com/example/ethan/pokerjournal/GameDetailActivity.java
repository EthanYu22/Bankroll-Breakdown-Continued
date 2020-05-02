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

// Displays Game Details on a Separate Page
public class GameDetailActivity extends AppCompatActivity
{

    DatabaseHelper db;
    Game game;
    int gameId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gameId = MainActivity.gameId;
        db = new DatabaseHelper(this);
        game = db.getGame(gameId);
    }


    // Action When On Game Detail Page
    public void onResume()
    {
        super.onResume();
        displayDetails();
    }

    /*
    // Action When Off Game Detail Page
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

    // Displays Game Details
    public void displayDetails()
    {
        double timeInHours = game.getTime() / 60.0;
        TextView hourlyRate = (TextView) findViewById(R.id.textGameHourlyRate);
        TextView netProfit = (TextView) findViewById(R.id.textGameNetProfit);
        TextView type = (TextView) findViewById(R.id.textGameType);
        TextView blinds = (TextView) findViewById(R.id.textGameBlinds);
        TextView location = (TextView) findViewById(R.id.textGameLocation);
        TextView date = (TextView) findViewById(R.id.textGameDate);
        TextView timeMins = (TextView) findViewById(R.id.textGameTimeMins);
        TextView timeHours = (TextView) findViewById(R.id.textGameTimeHours);
        TextView buyIn = (TextView) findViewById(R.id.textGameBuyIn);
        TextView cashOut = (TextView) findViewById(R.id.textGameCashOut);

        // Calculate Net Profit and Hourly Rate
        int nProfit = game.getCashOut() - game.getBuyIn();
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
            netProfit.setText("Session Net Profit: -$" + nProfit);
        }
        else
        {
            netProfit.setText("Session Net Profit: $" + nProfit);
        }
        type.setText("Game: " + game.getType());
        blinds.setText("Blinds: " + game.getBlinds());
        location.setText("Location: " + game.getLocation());
        date.setText("Date: " + game.getConvertedDateMMddyyyy());
        timeMins.setText("Time in Minutes: " + game.getTime() + " mins");
        timeHours.setText("Time in Hours: " + String.format("%.2f", game.getTime()/60.0) + " hrs");
        buyIn.setText("Buy In: $" + game.getBuyIn());
        cashOut.setText("Cash Out: $" + game.getCashOut());
    }

    // Edit Game Entries
    public void onClickEditGame(View v)
    {
        gameId = v.getId();
        Intent intent = new Intent(GameDetailActivity.this, GameEditActivity.class);
        startActivity(intent);
    }

    // Reset Button Resets Games
    public void onClickDeleteGame(View v)
    {

        // Confirmation Delete Game Alert
        AlertDialog.Builder altdial = new AlertDialog.Builder(GameDetailActivity.this);
        altdial.setMessage("Do you want to delete this session?").setCancelable(false).setPositiveButton("Delete Session", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                db.deleteGame(gameId);
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
