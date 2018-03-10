package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BankArrayAdapter extends ArrayAdapter<Bank> {

    public BankArrayAdapter(Context context, List<Bank> banksList) {super(context, 0, banksList);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bank bank = getItem(position);

        // Display Each Deposit/Withdraw as a Transaction in a Listing
        if (convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_item, parent, false);}

        // Variables for Each Transaction Listing
        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        TextView description_1 = (TextView) convertView.findViewById(R.id.itemType);
        TextView description_2 = (TextView) convertView.findViewById(R.id.itemDescription);

        String type = bank.getType();
        String msg_1 = type;
        double amount = bank.getAmount();
        String msg_2 = "$" + amount;

        // The Display for Each Transaction Listing
        date.setText(bank.getDate());
        description_1.setText(msg_1);
        description_2.setText(msg_2);

        // Labels ViewId as BankId
        convertView.setId(bank.getId());
        return convertView;
    }
}
