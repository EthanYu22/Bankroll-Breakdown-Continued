package com.example.ethan.pokerjournal;
/*
Uses a Guide From www.androidhive.info for Tabs and SQLite
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

    public static int sessionId; // Used to Hold Session View ID
    public static int bankId; // Used to Hold Bank View ID
    DatabaseHelper db;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    List<Session> sessionList;
    List<Bank> bankList;
    // Tabs-Fragments
    HistoryFragment hist;
    StatsFragment stats;
    BankFragment bank;
    // Formatting of App
    private Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate All
        prefs = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = prefs.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        sessionList = db.getAllSessions();
        bankList = db.getAllBanks();
        hist = new HistoryFragment();
        stats = new StatsFragment();
        bank = new BankFragment();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    // Creates Layout for Tabs/Fragments: History, Stats, Bank
    public void setupViewPager(ViewPager upViewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(hist, "History");
        adapter.addFragment(stats, "Statistics");
        adapter.addFragment(bank, "Bankroll");
        viewPager.setAdapter(adapter);
    }

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            if(viewPager.getCurrentItem() != 0){
                tabLayout.getTabAt(viewPager.getCurrentItem() - 1).select();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // New Session Button Leads to Session Form
    public void onClickNewSession(View v)
    {
        Intent intent = new Intent(this, SessionFormActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    // Live Session Button Leads to Live Session Form
    public void onClickLiveSessionForm(View v)
    {
        if(prefs.getBoolean("liveSessionActive", true))
        {
            Intent intent = new Intent(this, LiveSessionTracker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("sessionType", prefs.getString("liveSessionType",""));
            intent.putExtra("sessionBlinds",prefs.getString("liveSessionBlinds",""));
            intent.putExtra("location", prefs.getString("liveSessionLocation", ""));
            intent.putExtra("buyIn",prefs.getString("liveSessionBuyIn","0"));
            startActivity(intent);
        }else
        {
            Intent intent = new Intent(this, LiveSessionFormActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    // Deposit/Withdraw Button Leads to a Bank Form
    public void onClickDepositWithdraw(View v)
    {
        Intent intent = new Intent(this, BankFormActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    // Reset Button Resets Sessions
    public void onClickResetSession(View v)
    {

        Button btn = (Button) findViewById(R.id.buttonResetSessions);

        // Confirmation Reset Session History Alert
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
                altdial.setMessage("Do you want to clear your session history?").setCancelable(false).setPositiveButton("Clear Sessions", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        db.clearSessions();
                        hist.displaySessions();
                        stats.displayStats();
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
                alert.setTitle("Clear Session History");
                alert.show();
            }
        });
    }

    // Reset Button Resets Bank Transactions
    public void onClickResetBank(View v)
    {

        Button btn = (Button) findViewById(R.id.buttonResetBank);

        // Confirmation Reset Bank Transaction History Alert
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
                altdial.setMessage("Do you want to clear your bankroll transactions?").setCancelable(false).setPositiveButton("Clear Transactions", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        db.clearBank();
                        bank.displayBanks();
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
                alert.setTitle("Clear Bankroll Transactions");
                alert.show();
            }
        });
    }

    // Clicking on Session Listing Leads to Session Details Page
    public void onClickSession(View v)
    {
        sessionId = v.getId();
        Intent intent = new Intent(this, SessionDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    // Clicking on Bank Listing Leads to Bank Details Page
    public void onClickBank(View v)
    {
        bankId = v.getId();
        Intent intent = new Intent(this, BankDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onClickStatsGraph(View v)
    {
        sessionList = db.getAllSessions();
        if(sessionList.size() < 1){
            Toast noSessions = Toast.makeText(getApplication(), "Need at least one logged poker session to display.", Toast.LENGTH_SHORT);
            noSessions.setGravity(Gravity.CENTER, 0, 0);
            noSessions.show();
            return;
        }
        Intent intent = new Intent(this, StatsGraphActivity.class);
        startActivity(intent);
    }
}
