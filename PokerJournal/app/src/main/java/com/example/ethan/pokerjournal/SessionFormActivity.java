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
import android.widget.Toast;


// Session Entry Form
public class SessionFormActivity extends AppCompatActivity
{

    private Toolbar toolbar;

    Spinner spinSessionType;
    Spinner spinSessionBlinds;
    Spinner spinMonth;
    Spinner spinDay;
    Spinner spinYear;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_form);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Up Spinners
        spinSessionType = (Spinner) findViewById(R.id.spinnerSessionType);
        spinSessionBlinds = (Spinner) findViewById(R.id.spinnerSessionBlinds);
        spinSessionBlinds.setSelection(4);
        spinMonth = (Spinner) findViewById(R.id.spinnerSessionMonth);
        spinDay = (Spinner) findViewById(R.id.spinnerSessionDay);
        spinYear = (Spinner) findViewById(R.id.spinnerSessionYear);

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
        spinYear.setSelection(2);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    /*
    // Action When Off Session Form Page
    public void onPause() {
        super.onPause();
        finish();
    }
    */

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

    // Submit Session Transaction
    public void onClickSessionButton(View v)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        Session session = new Session();
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

        // ~ Get Entries and Validate ~
        // Get Input for Poker Variation Type
        String type = spinSessionType.getSelectedItem().toString();

        // Get Input for Blind Amount
        String blinds = spinSessionBlinds.getSelectedItem().toString();

        // Get Location and Make Sure it's Valid
        EditText editLocation = (EditText) findViewById(R.id.editLocation);
        String location = editLocation.getText().toString();
        if (location.isEmpty())
        {
            toast.show();
            return;
        }

        // Get Input of Date
        String month = spinMonth.getSelectedItem().toString();
        String day = spinDay.getSelectedItem().toString();
        String year = spinYear.getSelectedItem().toString();

        String date = "";
        String[] dayMonthYearDate = {day, month, year, date};

        // Append Day, Month, Year to Format - Date: YYYY/MM/DD
        MainActivity.appendDates(dayMonthYearDate);
        date = dayMonthYearDate[3];

        // Get Session Duration and Make Sure it's Valid
        EditText editTime = (EditText) findViewById(R.id.editTime);
        if (editTime.getText().toString().isEmpty())
        {
            toast.show();
            return;
        }
        // Input Session Duration Entry
        int time = Integer.parseInt(editTime.getText().toString());

        // Get Buy In and Make Sure it's Valid
        EditText editBuyIn = (EditText) findViewById(R.id.editBuyIn);
        if (editBuyIn.getText().toString().isEmpty())
        {
            toast.show();
            return;
        }
        // Input Buy In Entry
        int buyIn = Integer.parseInt(editBuyIn.getText().toString());

        // Get Cash Out and Make Sure it's Valid
        EditText editCashOut = (EditText) findViewById(R.id.editCashOut);
        if (editCashOut.getText().toString().isEmpty())
        {
            toast.show();
            return;
        }
        // Input Cash Out Entry
        int cashOut = Integer.parseInt(editCashOut.getText().toString());

        // Set Entries into DB
        session.setEntries(type, blinds, location, date, time, buyIn, cashOut);

        db.createSession(session);

        finish();
    }
}