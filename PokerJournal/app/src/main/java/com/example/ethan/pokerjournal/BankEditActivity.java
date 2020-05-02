package com.example.ethan.pokerjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

// Edits Bank Transaction Entries
public class BankEditActivity extends AppCompatActivity
{

    DatabaseHelper db;
    Bank bank;
    int bankId;
    Spinner spinType;
    Spinner spinMonth;
    Spinner spinDay;
    Spinner spinYear;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_form);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        bankId = MainActivity.bankId;
        bank = db.getBank(bankId);

        final TextView displayAmount = (TextView) findViewById(R.id.editAmount);
        displayAmount.setText(Integer.toString(bank.getAmount()));

        // Set Up Type Spinner
        spinType = (Spinner) findViewById(R.id.spinnerType);

        // Set Up Date Spinners
        spinMonth = (Spinner) findViewById(R.id.spinnerBankMonth);
        spinDay = (Spinner) findViewById(R.id.spinnerBankDay);
        spinYear = (Spinner) findViewById(R.id.spinnerBankYear);

        final ArrayAdapter<CharSequence> monthsArray = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> days29Array = ArrayAdapter.createFromResource(this, R.array.days29, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> days30Array = ArrayAdapter.createFromResource(this, R.array.days30, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> daysArray = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> yearsArray = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_dropdown_item);

        monthsArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days29Array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days30Array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinMonth.setAdapter(monthsArray);
        spinDay.setAdapter(daysArray);
        spinYear.setAdapter(yearsArray);

        // Change Days Displayed in Spinners According to Month
        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 1:
                        spinDay.setAdapter(days29Array);
                        break;
                    case 2:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 3:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 4:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 5:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 6:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 7:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 8:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 9:
                        spinDay.setAdapter(daysArray);
                        break;
                    case 10:
                        spinDay.setAdapter(days30Array);
                        break;
                    case 11:
                        spinDay.setAdapter(daysArray);
                }
                setDaySpinner(bank.getDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        setTypeSpinner(bank.getType());
        setMonthSpinner(bank.getDate());
        setYearSpinner(bank.getDate());
    }

    /*
    // Action When Off Bank Form Page
    public void onPause() {
        super.onPause();
        finish();
    }*/

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
        spinType.setSelection(position);
    }

    // Displays Current Day Entry
    public void setDaySpinner(String date)
    {
        String month = (String) date.subSequence(8, 9);
        int position = Integer.parseInt(month) - 1;
        spinDay.setSelection(position);
    }

    // Displays Current Month Entry
    public void setMonthSpinner(String date)
    {
        String month = (String) date.subSequence(5, 6);
        int position = Integer.parseInt(month) - 1;
        spinMonth.setSelection(position);
    }

    // Displays Current Year Entry
    public void setYearSpinner(String date)
    {
        String month = (String) date.subSequence(0, 3);
        String[] monthArray = getResources().getStringArray(R.array.years);
        int position = Arrays.asList(monthArray).indexOf(month);
        spinYear.setSelection(position);
    }

    // Submit Edited Bank Transaction
    public void onClickBankButton(View v)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        Bank bank = new Bank();
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        // Get Input of Deposit or Withdraw
        Spinner spinType = (Spinner) findViewById(R.id.spinnerType);
        String type = spinType.getSelectedItem().toString();
        // Get Input of Date and Format into String
        Spinner spinMonth = (Spinner) findViewById(R.id.spinnerBankMonth);
        String month = spinMonth.getSelectedItem().toString();
        Spinner spinDay = (Spinner) findViewById(R.id.spinnerBankDay);
        String day = spinDay.getSelectedItem().toString();
        Spinner spinYear = (Spinner) findViewById(R.id.spinnerBankYear);
        String year = spinYear.getSelectedItem().toString();

        String date = "";
        String[] dayMonthYearDate = {day, month, year, date};

        // Append Day, Month, Year to Format - Date: YYY/MM/DD
        MainActivity.appendDates(dayMonthYearDate);
        date = dayMonthYearDate[3];

        // Get Amount and Make Sure it's Valid
        EditText editAmount = (EditText) findViewById(R.id.editAmount);
        String Amount = editAmount.getText().toString();
        if (Amount.isEmpty())
        {
            toast.show();
            return;
        }

        // Amount Transaction as a Double
        int amountMoney = Integer.parseInt(editAmount.getText().toString());

        // Set Entries into DB
        bank.setAll(bankId, type, date, amountMoney);

        db.editBank(bank);

        Intent intent = new Intent(BankEditActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
