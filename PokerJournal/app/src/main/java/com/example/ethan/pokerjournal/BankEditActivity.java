package com.example.ethan.pokerjournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

// Edits Bank Transaction Entries
public class BankEditActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    DatabaseHelper db;
    Bank bank;
    int bankId;
    Spinner spinTransactionType;
    EditText etAmount;
    TextView selectDate;
    private DatePickerDialog.OnDateSetListener dateSelectListener;
    String inputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        bankId = MainActivity.bankId;
        bank = db.getBank(bankId);

        // Set up Widgets w/ Current Transaction Data
        spinTransactionType = (Spinner) findViewById(R.id.spinnerTransType);
        etAmount = (EditText) findViewById(R.id.etAmount);
        setTypeSpinner(bank.getType());
        etAmount.setText(Integer.toString(bank.getAmount()));

        selectDate = (TextView) findViewById(R.id.tvSelectDate);
        selectDate.setText(bank.getConvertedDateMMddyyyy());
        inputDate = bank.getDate();
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                String currDate = bank.getDate();
                int year = Integer.parseInt(currDate.substring(0,4));
                int month = Integer.parseInt(currDate.substring(5,7)) - 1; // DatePicker Month goes from 0-11
                int day = Integer.parseInt(currDate.substring(8,10));

                DatePickerDialog dialog = new DatePickerDialog(
                        BankEditActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSelectListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSelectListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String displayDate = String.format("%02d", month) + "/" + String.format("%02d", day) + "/" + year;
                selectDate.setText(displayDate);
                inputDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
            }
        };
    }

    public void onPause() {
        super.onPause();
    }

    // Functionality of Toolbar Back Arrow
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Displays Deposit/Withdraw Entry
    public void setTypeSpinner(String type)
    {
        String[] depositWithdrawArray = getResources().getStringArray(R.array.deposit_withdraw);
        int position = Arrays.asList(depositWithdrawArray).indexOf(type);
        spinTransactionType.setSelection(position);
    }

    // Submit Edited Bank Transaction
    public void onClickEditTransaction(View v)
    {
        Toast fillOutFields = Toast.makeText(getApplication(), "Please fill out all the fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        String inputType = spinTransactionType.getSelectedItem().toString();
        String inputAmount = etAmount.getText().toString();
        if (inputAmount.isEmpty())
        {
            fillOutFields.show();
            return;
        }

        // Set Entries into DB
        bank.setAll(bankId, inputType, inputDate, Integer.parseInt(inputAmount));
        db.editBank(bank);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
