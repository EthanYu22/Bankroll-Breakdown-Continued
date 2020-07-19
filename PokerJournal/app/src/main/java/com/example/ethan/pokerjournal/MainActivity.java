package com.example.ethan.pokerjournal;
/*
Uses a Guide From www.androidhive.info for Tabs and SQLite
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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

    // Toolbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    // Functionality of Toolbar Menu Items
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case android.R.id.home:
                if(viewPager.getCurrentItem() != 0){
                    tabLayout.getTabAt(viewPager.getCurrentItem() - 1).select();
                }
                return true;

            case R.id.preferences:
                Toast.makeText(this, "Preferences Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.exportBankTransactions:
                onClickExportBankTransactions();
                return true;

            case R.id.exportPokerSessions:
                onClickExportPokerSessions();
                return true;
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

    public void onClickExportPokerSessions()
    {
        sessionList = db.getAllSessions();

        StringBuilder sessionListData = new StringBuilder();
        sessionListData.append("Session ID,Session Type,Blinds,Location,Date,Session Duration,Buy In,Cash Out");
        for(int i = 0; i < sessionList.size(); i++)
        {
            sessionListData.append("\n" + sessionList.get(i).id + "," + sessionList.get(i).type + "," + sessionList.get(i).blinds + "," + sessionList.get(i).location + "," + sessionList.get(i).date + "," + sessionList.get(i).time + "," + sessionList.get(i).buyIn + "," + sessionList.get(i).cashOut);
        }

        try
        {
            FileOutputStream sessionOut = openFileOutput("pokerSessionList.csv", Context.MODE_PRIVATE);
            sessionOut.write(sessionListData.toString().getBytes());
            sessionOut.close();

            Context context = getApplicationContext();
            File sessionFileLocation = new File(getFilesDir(), "pokerSessionList.csv");
            Uri sessionPath = FileProvider.getUriForFile(context, "com.example.ethan.pokerjournal.fileprovider", sessionFileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bankroll Breakdown - Poker Session Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, sessionPath);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClickExportBankTransactions()
    {
        bankList = db.getAllBanks();

        StringBuilder bankTransactionListData = new StringBuilder();
        bankTransactionListData.append("Transaction ID,Transaction Type,Amount,Date");
        for(int i = 0; i < bankList.size(); i++)
        {
            bankTransactionListData.append("\n" +  bankList.get(i).id + "," + bankList.get(i).type + "," + bankList.get(i).amount + "," + bankList.get(i).date);
        }

        try
        {
            FileOutputStream bankTransactionOut = openFileOutput("bankTransactionList.csv", Context.MODE_PRIVATE);
            bankTransactionOut.write(bankTransactionListData.toString().getBytes());
            bankTransactionOut.close();

            Context context = getApplicationContext();
            File bankTransactionFileLocation = new File(getFilesDir(), "bankTransactionList.csv");
            Uri bankTransactionPath = FileProvider.getUriForFile(context, "com.example.ethan.pokerjournal.fileprovider", bankTransactionFileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bankroll Breakdown - Bank Transaction Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, bankTransactionPath);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
