package com.example.ethan.pokerjournal;
/*
Uses a Guide From www.androidhive.info for Tabs and SQLite
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    List<Game> gameList;
    List<Bank> bankList;
    public static int gameId; // Used to Hold Game View ID
    public static int bankId; // Used to Hold Bank View ID

    // Formatting of App
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Tabs-Fragments
    HistoryFragment hist;
    StatsFragment stats;
    BankFragment bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate All
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        gameList = db.getAllGames();
        bankList = db.getAllBanks();
        hist = new HistoryFragment();
        stats = new StatsFragment();
        bank = new BankFragment();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Creates Layout for Tabs/Fragments: History, Stats, Bank
    public void setupViewPager(ViewPager upViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(hist, "History");
        adapter.addFragment(stats, "Statistics");
        adapter.addFragment(bank, "Bankroll");
        viewPager.setAdapter(adapter);
    }

    // New Game Button Leads to Game Form
    public void onClickNewGame(View v) {
        Intent intent = new Intent(MainActivity.this, GameFormActivity.class);
        startActivity(intent);
    }

    // Deposit/Withdraw Button Leads to a Bank Form
    public void onClickDepositWithdraw(View v) {
        Intent intent = new Intent(MainActivity.this, BankFormActivity.class);
        startActivity(intent);
    }

    // Reset Button Resets Games
    public void onClickResetGame(View v) {

        Button btn = (Button) findViewById(R.id.buttonResetGames);

        // Confirmation Reset Game History Alert
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
                altdial.setMessage("Do you want to reset your Game History?").setCancelable(false)
                        .setPositiveButton("Clear Games", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which){
                                db.clearGames();
                                hist.displayGames();
                                stats.displayStats();
                            }
                        })
                        .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.cancel();
                         }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Reset Game History");
                alert.show();
            }
        });
    }

    // Reset Button Resets Bank Transactions
    public void onClickResetBank(View v) {

        Button btn = (Button) findViewById(R.id.buttonResetBank);

        // Confirmation Reset Bank Transaction History Alert
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
                altdial.setMessage("Do you want to reset your Bankroll Transactions?").setCancelable(false)
                        .setPositiveButton("Clear Transactions", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which){
                                db.clearBank();
                                bank.displayBanks();
                            }
                        })
                        .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Reset Bankroll Transactions");
                alert.show();
            }
        });
    }

    // Clicking on Game Listing Leads to Game Details Page
    public void onClickGame(View v) {
        gameId = v.getId();
        Intent intent = new Intent(MainActivity.this, GameDetailActivity.class);
        startActivity(intent);
    }

    // Clicking on Bank Listing Leads to Bank Details Page
    public void onClickBank(View v) {
        bankId = v.getId();
        Intent intent = new Intent(MainActivity.this, BankDetailActivity.class);
        startActivity(intent);
    }
}
