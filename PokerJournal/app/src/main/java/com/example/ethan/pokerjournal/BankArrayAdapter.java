package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Creates Each Bank Transaction as a List with Specific Content
public class BankArrayAdapter extends ArrayAdapter<Bank> {

    // Initialize BankArrayAdapater
    public BankArrayAdapter(Context context, List<Bank> banksList) {super(context, 0, banksList);}

    @Override
    // Fills Out Information to Be Displayed For Each Transaction
    public View getView(int position, View convertView, ViewGroup parent) {
        Bank bank = getItem(position);

        // Display Each Deposit/Withdraw as a Transaction in a Listing
        if (convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_item, parent, false);}

        // Line Displays for Each Transaction Listing
        TextView bankDescription = (TextView) convertView.findViewById(R.id.bankDescription);

        // Initialize The Display for Each Transaction Listing
        bankDescription.setText(bank.toString());

        // Labels ViewId as BankId
        convertView.setId(bank.getId());
        return convertView;
    }
}
