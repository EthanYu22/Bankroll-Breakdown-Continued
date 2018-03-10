package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class BankFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_form);
    }

    // Action When Off Bank Form Page
    public void onPause() {
        super.onPause();
        finish();
    }

    // Submit Bank Transaction
    public void onClickBankButton(View v) {
        DatabaseHelper db = new DatabaseHelper(this);
        Bank bank = new Bank();
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        // Get Input of Deposit or Withdraw
        Spinner spinType = (Spinner) findViewById(R.id.spinnerType);
        String type = spinType.getSelectedItem().toString();

        // Get Input of Date
        Spinner spinMonth = (Spinner) findViewById(R.id.spinnerBankMonth);
        String month = spinMonth.getSelectedItem().toString();
        Spinner spinDay = (Spinner) findViewById(R.id.spinnerBankDay);
        String day = spinDay.getSelectedItem().toString();
        Spinner spinYear = (Spinner) findViewById(R.id.spinnerBankYear);
        String year = spinYear.getSelectedItem().toString();
        String date = month + "/" + day + "/" + year;

        // Get Amount and Make Sure it's Valid
        EditText editAmount = (EditText) findViewById(R.id.editAmount);
        String Amount = editAmount.getText().toString();
        if(Amount.isEmpty()) {
            toast.show();
            return;
        }

        // Amount Transaction
        double amountMoney = Double.parseDouble(editAmount.getText().toString());

        // Set Entries into DB
        bank.setEntries(type, date, amountMoney);

        db.createBank(bank);

        finish();
    }
}