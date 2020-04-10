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

// Displays Bank Transaction Details on a Separate Page
public class BankDetailActivity extends AppCompatActivity
{

    DatabaseHelper db;
    Bank bank;
    int bankId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        bankId = MainActivity.bankId;
        bank = db.getBank(bankId);
    }

    // Action When On Bank Detail Page
    public void onResume()
    {
        super.onResume();
        displayDetails();
    }

    /*
    // Action When Off Bank Detail Page
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

    // Displays Bank Details
    public void displayDetails()
    {
        TextView date = (TextView) findViewById(R.id.textBankDate);
        TextView type = (TextView) findViewById(R.id.textBankType);
        TextView amount = (TextView) findViewById(R.id.textBankAmount);

        // Set Significant Figures to 2 for Amount Deposited or Withdrawn
        String amt = String.format("%.2f", bank.getAmount());

        date.setText("Date: " + bank.getConvertedDateMMddyyyy());
        type.setText("Deposit/Withdraw: " + bank.getType());
        amount.setText("Amount: $" + amt);
    }

    // Edit Bank Entries
    public void onClickEditBank(View v)
    {
        bankId = v.getId();
        Intent intent = new Intent(BankDetailActivity.this, BankEditActivity.class);
        startActivity(intent);
    }

    // Deletes Bank Transaction
    public void onClickDeleteBank(View v)
    {

        // Confirmation Delete Transaction Alert
        AlertDialog.Builder altdial = new AlertDialog.Builder(BankDetailActivity.this);
        altdial.setMessage("Do you want to delete this transaction?").setCancelable(false).setPositiveButton("Delete Transaction", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                db.deleteBank(bankId);
                Toast toast = Toast.makeText(getApplication(), "Transaction Deleted", Toast.LENGTH_SHORT);
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
        alert.setTitle("Delete Transaction");
        alert.show();
    }
}
