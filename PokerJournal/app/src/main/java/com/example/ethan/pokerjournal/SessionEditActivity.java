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

import java.util.Arrays;

// Edits Session Entries
public class SessionEditActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    DatabaseHelper db;
    Session session;
    int sessionId;
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
        setContentView(R.layout.activity_session_edit);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        sessionId = MainActivity.sessionId;
        session = db.getSession(sessionId);

        // Set up Widgets w/ Current Session Data
        etLocation = (EditText) findViewById(R.id.etLocation);
        etTime = (EditText) findViewById(R.id.etTime);
        etBuyIn = (EditText) findViewById(R.id.etBuyIn);
        etCashOut = (EditText) findViewById(R.id.etCashOut);
        etLocation.setText(session.getLocation());
        etTime.setText(Integer.toString((int)(session.getTime())));
        etBuyIn.setText(Integer.toString((int)(session.getBuyIn())));
        etCashOut.setText(Integer.toString((int)(session.getCashOut())));

        spinSessionType = (Spinner) findViewById(R.id.spinnerSessionType);
        spinSessionBlinds = (Spinner) findViewById(R.id.spinnerSessionBlinds);
        setTypeSpinner(session.getType());
        setBlindsSpinner(session.getBlinds());

        selectDate = (TextView) findViewById(R.id.tvSelectDate);
        selectDate.setText(session.getConvertedDateMMddyyyy());
        inputDate = session.getDate();
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currDate = selectDate.getText().toString();
                int year = Integer.parseInt(currDate.substring(6,10));
                int month = Integer.parseInt(currDate.substring(0,2)) - 1; // DatePicker Month goes from 0-11
                int day = Integer.parseInt(currDate.substring(3,5));

                DatePickerDialog dialog = new DatePickerDialog(
                        SessionEditActivity.this,
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

    // Displays Current Session Type Entry
    public void setTypeSpinner(String type)
    {
        String[] typeArray = getResources().getStringArray(R.array.session_types);
        int position = Arrays.asList(typeArray).indexOf(type);
        spinSessionType.setSelection(position);
    }

    //Displays Current Blinds Entry
    public void setBlindsSpinner(String blinds)
    {
        String[] blindsArray = getResources().getStringArray(R.array.session_blinds);
        int position = Arrays.asList(blindsArray).indexOf(blinds);
        spinSessionBlinds.setSelection(position);
    }

    // Submit Edited Poker Session
    public void onClickSessionButton(View v)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();
        Toast fillOutFields = Toast.makeText(getApplication(), "Please fill out all the fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        if (etLocation.getText().toString().isEmpty() || etTime.getText().toString().isEmpty() || etBuyIn.getText().toString().isEmpty() || etCashOut.getText().toString().isEmpty())
        {
            fillOutFields.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
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
        session.setAll(sessionId, inputType, inputBlinds, inputLocation, inputDate, inputTime, inputBuyIn, inputCashOut);
        db.editSession(session);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
