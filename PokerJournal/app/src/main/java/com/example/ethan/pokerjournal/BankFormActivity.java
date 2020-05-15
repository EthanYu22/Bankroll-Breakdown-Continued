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

import java.util.Calendar;

// Bank Transaction Entry Form
public class BankFormActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    Spinner spinTransactionType;
    EditText etAmount;
    TextView selectDate;
    private DatePickerDialog.OnDateSetListener dateSelectListener;
    String inputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_form);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Up Widgets
        spinTransactionType = (Spinner) findViewById(R.id.spinnerTransType);
        etAmount = (EditText) findViewById(R.id.etAmount);

        selectDate = (TextView) findViewById(R.id.tvSelectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BankFormActivity.this,
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Submit Bank Transaction
    public void onClickBankButton(View v)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        Bank bank = new Bank();
        Toast fillAmount = Toast.makeText(getApplication(), "Please fill in the \"Amount ($)\" field", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        String inputType = spinTransactionType.getSelectedItem().toString();
        String inputAmount = etAmount.getText().toString();
        if (inputAmount.isEmpty())
        {
            fillAmount.show();
            return;
        }

        // Set Entries into DB
        bank.setEntries(inputType, inputDate, Integer.parseInt(inputAmount));
        db.createBank(bank);
        finish();
    }
}