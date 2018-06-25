package com.example.ethan.pokerjournal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

// Displays Each Bank Transaction as a List Under Bankroll Tab & Displays Current Bankroll Amount
public class BankFragment extends Fragment {

    DatabaseHelper db;
    List<Game> gameList;
    List<Bank> banksList;

    public BankFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bank, container, false); // Inflate Layout
    }

    // Action When On Bank Page
    public void onResume() {
        super.onResume();
        displayBanks();
    }

    // Displays All Bank Transactions + Net Bankroll Amount
    public void displayBanks() {
        // Displays Bank Transactions
        banksList = db.getAllBanks();
        ListView lv = (ListView) getView().findViewById(R.id.listBank);
        BankArrayAdapter adapter = new BankArrayAdapter(getActivity(), banksList);
        lv.setAdapter(adapter);

        // Sorts Bank Transactions by Date
        adapter.sort(new Comparator<Bank>() {
            public int compare(Bank arg0, Bank arg1) {
                return arg0.date.compareTo(arg1.date);
            }
        });

        // Arbitrary Code for Sorting + Displaying Adapter
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Calculate Net Profit from Poker Sessions
        Game game;
        gameList = db.getAllGames();
        int netProfit;
        int buyIn = 0;
        int cashOut = 0;
        for (int i = 0; i < gameList.size(); i++) {
            game = gameList.get(i);
            buyIn += game.getBuyIn();
            cashOut += game.getCashOut();
        }
        netProfit = cashOut - buyIn;

        // Calculate Net Bank Transactions
        Bank bank;
        banksList = db.getAllBanks();
        double totalMoney = 0;
        double Bankroll = 0;
        double WithdrawDeposit = 0;
        for (int i = 0; i < banksList.size(); i++) {
            bank = banksList.get(i);
            if(bank.getType().equals("Withdraw")){
                WithdrawDeposit = (bank.getAmount() * -1);
            }
            else{
                WithdrawDeposit = bank.getAmount();
            }
            totalMoney += WithdrawDeposit;
        }

        // Net Bankroll Amount
        Bankroll = totalMoney + netProfit;

        // Displays Net Bankroll Amount
        TextView bankroll = (TextView) getView().findViewById(R.id.textTotalMoney);
        if(Bankroll < 0){
            bankroll.setText("-$" + -Bankroll);
        }else {
            bankroll.setText("$" + Bankroll);
        }
    }

}
