package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Displays Bank Transaction Details on a Separate Page
public class BankDetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    private Toolbar toolbar;
    Bank bank;
    int bankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        bankId = MainActivity.bankId;
        bank = db.getBank(bankId);
    }

    // Action When On Game Detail Page
    public void onResume() {
        super.onResume();
        displayDetails();
    }

    // Action When Off Game Detail Page
    public void onPause() {
        super.onPause();
        finish();
    }

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Displays Bank Details
    public void displayDetails() {
        TextView date = (TextView) findViewById(R.id.textBankDate);
        TextView type = (TextView) findViewById(R.id.textBankType);
        TextView amount = (TextView) findViewById(R.id.textBankAmount);

        // Set Significant Figures to 2 for Amount Deposited or Withdrawn
        String amt = String.format("%.2f", bank.getAmount());

        date.setText("Date: " + bank.getDate());
        type.setText("Deposit/Withdraw: " + bank.getType());
        amount.setText("Amount: $" + amt);
    }

    // Edit Bank Entries
    public void onClickEditBank(View v) {
        bankId = v.getId();
        Intent intent = new Intent(BankDetailActivity.this, BankEditActivity.class);
        startActivity(intent);
    }

    // Deletes Bank Transaction
    public void onClickDeleteBank(View v) {
        db.deleteBank(bankId);
        Toast toast = Toast.makeText(getApplication(), "Bank Transaction Deleted", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
