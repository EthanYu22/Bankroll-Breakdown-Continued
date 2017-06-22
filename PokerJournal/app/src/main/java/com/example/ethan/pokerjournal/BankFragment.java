package com.example.ethan.pokerjournal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

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
        // Inflate Layout
        return inflater.inflate(R.layout.bank, container, false);
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
            if(bank.getDw().equals("Withdraw")){
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
        bankroll.setText("$" + Bankroll);
    }

}
