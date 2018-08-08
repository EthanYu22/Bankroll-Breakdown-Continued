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
                altdial.setMessage("Do you want to clear your session history?").setCancelable(false)
                        .setPositiveButton("Clear Sessions", new DialogInterface.OnClickListener() {
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
                alert.setTitle("Clear Session History");
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
                altdial.setMessage("Do you want to clear your bankroll transactions?").setCancelable(false)
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
                alert.setTitle("Clear Bankroll Transactions");
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

    // Append Day, Month, Year to Format Date: MM/DD/YYYY Date2: YYYY/MM/DD
    public static String[] appendDates(String[] dayMonthYearDateDate2){
        if(dayMonthYearDateDate2[1].equals("January")){dayMonthYearDateDate2[1] = "01";}
        else if(dayMonthYearDateDate2[1].equals("February")){dayMonthYearDateDate2[1] = "02";}
        else if(dayMonthYearDateDate2[1].equals("March")){dayMonthYearDateDate2[1] = "03";}
        else if(dayMonthYearDateDate2[1].equals("April")){dayMonthYearDateDate2[1] = "04";}
        else if(dayMonthYearDateDate2[1].equals("May")){dayMonthYearDateDate2[1] = "05";}
        else if(dayMonthYearDateDate2[1].equals("June")){dayMonthYearDateDate2[1] = "06";}
        else if(dayMonthYearDateDate2[1].equals("July")){dayMonthYearDateDate2[1] = "07";}
        else if(dayMonthYearDateDate2[1].equals("August")){dayMonthYearDateDate2[1] = "08";}
        else if(dayMonthYearDateDate2[1].equals("September")){dayMonthYearDateDate2[1] = "09";}
        else if(dayMonthYearDateDate2[1].equals("October")){dayMonthYearDateDate2[1] = "10";}
        else if(dayMonthYearDateDate2[1].equals("November")){dayMonthYearDateDate2[1] = "11";}
        else{dayMonthYearDateDate2[1] = "12";}
        int intDay = Integer.parseInt(dayMonthYearDateDate2[0]);
        switch(intDay){
            case 1: dayMonthYearDateDate2[0] = "01";
                break;
            case 2: dayMonthYearDateDate2[0] = "02";
                break;
            case 3: dayMonthYearDateDate2[0] = "03";
                break;
            case 4: dayMonthYearDateDate2[0] = "04";
                break;
            case 5: dayMonthYearDateDate2[0] = "05";
                break;
            case 6: dayMonthYearDateDate2[0] = "06";
                break;
            case 7: dayMonthYearDateDate2[0] = "07";
                break;
            case 8: dayMonthYearDateDate2[0] = "08";
                break;
            case 9: dayMonthYearDateDate2[0] = "09";
        }
        dayMonthYearDateDate2[3] = dayMonthYearDateDate2[1] + "/" + dayMonthYearDateDate2[0] + "/" + dayMonthYearDateDate2[2];
        dayMonthYearDateDate2[4] = dayMonthYearDateDate2[2] + "/" + dayMonthYearDateDate2[1] + "/" + dayMonthYearDateDate2[0];

        return dayMonthYearDateDate2;
    }
}
