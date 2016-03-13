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

    public void onPause() {
        super.onPause();
        finish();
    }

    public void onClickBankButton(View v) {
        DatabaseHelper db = new DatabaseHelper(this);
        Bank bank = new Bank();
        Toast toast = Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT);

       //get values + validate
        Spinner spin = (Spinner) findViewById(R.id.spinnerDW);
        String type = spin.getSelectedItem().toString();

        EditText editAmount = (EditText) findViewById(R.id.editAmount);
        String Amount = editAmount.getText().toString();
        if(Amount.isEmpty()) {
            toast.show();
            return;
        }
        double amountMoney = Double.parseDouble(editAmount.getText().toString());

        bank.setWd(type);
        bank.setAmount(amountMoney);

        db.createBank(bank);

        finish();
    }
}


