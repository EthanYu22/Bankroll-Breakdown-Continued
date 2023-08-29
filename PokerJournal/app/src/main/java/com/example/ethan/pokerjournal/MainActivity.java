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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity<REQUEST_CODE> extends AppCompatActivity
{

    public static int sessionId; // Used to Hold Session View ID
    public static int bankId; // Used to Hold Bank View ID
    public final static int REQUEST_CODE_BANK_TRANSACTIONS = 1000;
    public final static int REQUEST_CODE_POKER_SESSIONS = 2000;
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
    MenuItem toggleLoginPageMenuItem;

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

        // Set up Preferences Login Page Toggle Title
        editor.putString("PreferencesLoginPageTitle","");
        editor.commit();



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

        // Set up Preferences - toggleLoginPage MenuItem Title
        toggleLoginPageMenuItem = (MenuItem) menu.findItem(R.id.toggleLoginPage);
        if(prefs.getBoolean("EnabledLoginPage", true)){
            editor.putString("PreferencesLoginPageTitle","Disable Login Page");
            editor.commit();
            toggleLoginPageMenuItem.setTitle(prefs.getString("PreferencesLoginPageTitle","Disable Login Page"));
        } else {
            editor.putString("PreferencesLoginPageTitle","Enable Login Page");
            editor.commit();
            toggleLoginPageMenuItem.setTitle(prefs.getString("PreferencesLoginPageTitle","Enable Login Page"));
        }
        return true;
    }

    // Functionality of Toolbar Menu Items
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        toggleLoginPageMenuItem.setTitle(prefs.getString("PreferencesLoginPageTitle","Enable Login Page"));
        switch(menuItem.getItemId())
        {
            case android.R.id.home:
                if(viewPager.getCurrentItem() != 0){
                    tabLayout.getTabAt(viewPager.getCurrentItem() - 1).select();
                }
                return true;

            case R.id.toggleLoginPage:
                onClickToggleLoginPage();
                return true;

            case R.id.exportBankTransactions:
                onClickExportBankTransactions();
                return true;

            case R.id.exportPokerSessions:
                onClickExportPokerSessions();
                return true;

            case R.id.importBankTransactions:
                onClickImportBankTransactions();
                return true;

            case R.id.importPokerSessions:
                onClickImportPokerSessions();
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

    public void onClickExportBankTransactions()
    {
        bankList = db.getAllBanks();

        StringBuilder bankTransactionListData = new StringBuilder();
        bankTransactionListData.append("Date,Transaction Type,Amount");
        for(int i = 0; i < bankList.size(); i++)
        {
            bankTransactionListData.append("\n" + bankList.get(i).date + "," + bankList.get(i).type + "," + bankList.get(i).amount);
        }

        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate today = LocalDate.now();
            String exportDate = today.format(formatter);
            String fileName = "bankTransactionList " + exportDate + ".csv";

            FileOutputStream bankTransactionOut = openFileOutput(fileName, Context.MODE_PRIVATE);
            bankTransactionOut.write(bankTransactionListData.toString().getBytes());
            bankTransactionOut.close();

            Context context = getApplicationContext();
            File bankTransactionFileLocation = new File(getFilesDir(), fileName);
            Uri bankTransactionPath = FileProvider.getUriForFile(context, "com.example.ethan.pokerjournal.fileprovider", bankTransactionFileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bankroll Breakdown - Bank Transaction Data (" + exportDate + ")");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, bankTransactionPath);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClickImportBankTransactions()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("text/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, REQUEST_CODE_BANK_TRANSACTIONS);
    }

    public void importBankTransactions(String path)
    {
        // Will want to add a check that the file is correct before going further, otherwise return
        try
        {
            DatabaseHelper db = new DatabaseHelper(this);

            File importedBankTransactions = new File(path);
            FileInputStream bankTransactionIn = new FileInputStream(importedBankTransactions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(bankTransactionIn));
            String bankTransaction = "";

            // Check header is correct
            bankTransaction = reader.readLine();
            if(!bankTransaction.equals("Date,Transaction Type,Amount"))
            {
                Toast.makeText(MainActivity.this, "Unauthorized File: Incorrect File Header", Toast.LENGTH_LONG).show();
                bankTransactionIn.close();
                reader.close();
                return;
            }

            while((bankTransaction = reader.readLine()) != null)
            {
                // Date, Transaction Type, Amount
                String[] split = bankTransaction.split(",");

                String inputDate = split[0];
                String inputType = split[1];
                String inputAmount = split[2];

                try
                {
                    Bank bank = new Bank();
                    bank.setEntries(inputType, inputDate, Integer.parseInt(inputAmount));
                    db.createBank(bank);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Bank Transactions Failed to Import!", Toast.LENGTH_LONG).show();
                    deleteTempFile();
                    bankTransactionIn.close();
                    reader.close();
                    return;
                }
            }
            bankTransactionIn.close();
            reader.close();
            deleteTempFile();
            Toast.makeText(MainActivity.this, "Bank Transactions Imported Successfully!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickExportPokerSessions()
    {
        int profit = 0;
        sessionList = db.getAllSessions();

        StringBuilder sessionListData = new StringBuilder();
        sessionListData.append("Date,Net Profit ($),Buy In ($),Cash Out ($),Session Duration (Minutes),Blinds,Location,Session Type");

        for(int i = 0; i < sessionList.size(); i++)
        {
            sessionListData.append("\n" + sessionList.get(i).date + "," + sessionList.get(i).getProfit() + "," + sessionList.get(i).buyIn + "," + sessionList.get(i).cashOut + "," + sessionList.get(i).time  + "," + sessionList.get(i).blinds + "," + sessionList.get(i).location + "," + sessionList.get(i).type);
            profit += sessionList.get(i).getProfit();
        }

        // Provide Net Profit
        sessionListData.append("\n" + "Total Net Profit: $" + profit);

        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate today = LocalDate.now();
            String exportDate = today.format(formatter);
            String fileName = "Poker Session Report - (" + exportDate + ").csv";

            FileOutputStream sessionOut = openFileOutput(fileName, Context.MODE_PRIVATE);
            sessionOut.write(sessionListData.toString().getBytes());
            sessionOut.close();

            Context context = getApplicationContext();
            File sessionFileLocation = new File(getFilesDir(), fileName);
            Uri sessionPath = FileProvider.getUriForFile(context, "com.example.ethan.pokerjournal.fileprovider", sessionFileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bankroll Breakdown - Poker Session Data (" + exportDate + ")");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, sessionPath);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClickImportPokerSessions()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("text/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, REQUEST_CODE_POKER_SESSIONS);
    }

    public void importPokerSessions(String path)
    {
        // Will want to add a check that the file is correct before going further, otherwise return
        try
        {
            DatabaseHelper db = new DatabaseHelper(this);

            File importedPokerSessions = new File(path);
            FileInputStream pokerSessionsIn = new FileInputStream(importedPokerSessions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(pokerSessionsIn));
            String pokerSession = "";

            // Check header is correct
            pokerSession = reader.readLine();
            if(!pokerSession.equals("Date,Net Profit ($),Buy In ($),Cash Out ($),Session Duration (Minutes),Blinds,Location,Session Type"))
            {
                Toast.makeText(MainActivity.this, "Unauthorized File: Incorrect File Header", Toast.LENGTH_LONG).show();
                pokerSessionsIn.close();
                reader.close();
                return;
            }

            while((pokerSession = reader.readLine()) != null)
            {
                // Date, Profits, Buy In, Cash Out, Session Time, Blinds, Location, Session Type
                String[] split = pokerSession.split(",");

                String inputDate = split[0];
                String inputBuyIn = split[2];
                String inputCashOut = split[3];
                String inputTime = split[4];
                String inputBlinds = split[5];
                String inputLocation = split[6];
                String inputType = split[7];


                try
                {
                    Session session = new Session();
                    session.setEntries(inputType, inputBlinds, inputLocation, inputDate, Integer.parseInt(inputTime), Integer.parseInt(inputBuyIn), Integer.parseInt(inputCashOut));
                    db.createSession(session);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Poker Sessions Failed to Import", Toast.LENGTH_LONG).show();
                    deleteTempFile();
                    pokerSessionsIn.close();
                    reader.close();
                    return;
                }
            }
            pokerSessionsIn.close();
            reader.close();
            deleteTempFile();
            Toast.makeText(MainActivity.this, "Poker Sessions Imported Successfully", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_CODE_BANK_TRANSACTIONS:
                if(resultCode == RESULT_OK)
                {
                    if(data != null)
                    {
                        final Uri uri = data.getData();
                        try
                        {
                            Context context = this;
                            final String path = RealPathUtil.getRealPath(context, uri);
                            importBankTransactions(path);
                        } catch (Exception e)
                        {
                            Log.e("FileSelectorActivity", "File select error", e);
                        }
                    }
                }
                break;

            case REQUEST_CODE_POKER_SESSIONS:
                if(resultCode == RESULT_OK)
                {
                    if(data != null)
                    {
                        final Uri uri = data.getData();
                        try
                        {
                            Context context = this;
                            final String path = RealPathUtil.getRealPath(context, uri);
                            importPokerSessions(path);
                        } catch (Exception e)
                        {
                            Log.e("FileSelectorActivity", "File select error", e);
                        }
                    }
                }
               break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteTempFile() {
        final File[] files = this.getCacheDir().listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.getName().contains("TEMP_FILE")) {
                    file.delete();
                }
            }
        }
    }

    private void onClickToggleLoginPage(){
        if(prefs.getBoolean("EnabledLoginPage", true)){
            Toast.makeText(this, "Login Page: Disabled", Toast.LENGTH_SHORT).show();
            editor.putBoolean("EnabledLoginPage", false);
            editor.putString("PreferencesLoginPageTitle","Enable Login Page");

        } else {
            Toast.makeText(this, "Login Page: Enabled", Toast.LENGTH_SHORT).show();
            editor.putBoolean("EnabledLoginPage", true);
            editor.putString("PreferencesLoginPageTitle","Disable Login Page");
        }
        editor.commit();
    }
}
