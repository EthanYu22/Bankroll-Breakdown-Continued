package com.example.ethan.pokerjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ethan on 3/12/2016.
 */
public class BankArrayAdapter extends ArrayAdapter<Bank> {
    public BankArrayAdapter(Context context, List<Bank> banksList) {
        super(context, 0, banksList);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bank bank = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_item, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        TextView description = (TextView) convertView.findViewById(R.id.itemDescription);

        double total = bank.getAmount();
        String dw = bank.getDw();

        String msg = "Action:" + dw + " $" + total;
        date.setText("" + bank.getAmount());
        description.setText(msg);

        return convertView;
    }
}
