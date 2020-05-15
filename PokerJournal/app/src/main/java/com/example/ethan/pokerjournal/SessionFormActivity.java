package com.example.ethan.pokerjournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


// Session Entry Form
public class SessionFormActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    Spinner spinSessionType;
    Spinner spinSessionBlinds;
    EditText etLocation;
    EditText etTime;
    EditText etBuyIn;
    EditText etCashOut;
    TextView selectDate;
    private DatePickerDialog.OnDateSetListener dateSelectListener;
    String inputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_form);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Up Widgets
        etLocation = (EditText) findViewById(R.id.etLocation);
        etTime = (EditText) findViewById(R.id.etTime);
        etBuyIn = (EditText) findViewById(R.id.etBuyIn);
        etCashOut = (EditText) findViewById(R.id.etCashOut);

        spinSessionType = (Spinner) findViewById(R.id.spinnerSessionType);
        spinSessionBlinds = (Spinner) findViewById(R.id.spinnerSessionBlinds);
        spinSessionBlinds.setSelection(4);

        selectDate = (TextView) findViewById(R.id.tvSelectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SessionFormActivity.this,
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

    // Submit Poker Session
    public void onClickSessionButton(View v)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();
        Toast fillOutFields = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        if (etLocation.getText().toString().isEmpty() || etTime.getText().toString().isEmpty() || etBuyIn.getText().toString().isEmpty() || etCashOut.getText().toString().isEmpty())
        {
            fillOutFields.show();
            return;
        }

        String inputType = spinSessionType.getSelectedItem().toString();
        String inputBlinds = spinSessionBlinds.getSelectedItem().toString();
        String inputLocation = etLocation.getText().toString();
        int inputTime = Integer.parseInt(etTime.getText().toString());
        int inputBuyIn = Integer.parseInt(etBuyIn.getText().toString());
        int inputCashOut = Integer.parseInt(etCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(inputType, inputBlinds, inputLocation, inputDate, inputTime, inputBuyIn, inputCashOut);
        db.createSession(session);
        finish();
    }
}