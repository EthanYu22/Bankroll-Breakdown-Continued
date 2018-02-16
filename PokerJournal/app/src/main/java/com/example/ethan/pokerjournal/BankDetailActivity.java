package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Shows Bank Transaction Details
public class BankDetailActivity extends AppCompatActivity {

    DatabaseHelper db;
    Bank bank;
    int bankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail); // Runs layout.activity_bank_detail
        bankId = MainActivity.bankId; // Refers to Bank Listing ID
        db = new DatabaseHelper(this);
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

    // Displays Bank Details
    public void displayDetails() {
        TextView date = (TextView) findViewById(R.id.textBankDate);
        TextView dw = (TextView) findViewById(R.id.textBankDw);
        TextView amount = (TextView) findViewById(R.id.textBankAmount);

        date.setText("Date: " + bank.getDate());
        dw.setText("Deposit/Withdraw: " + bank.getDw());
        amount.setText("Amount: $" + bank.getAmount());
    }

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
