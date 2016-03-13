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

/**
 * Created by Andrew on 3/9/2016.
 */
public class BankFragment extends Fragment {

    DatabaseHelper db;
    List<Game> gameList;
    List<Bank> banksList;

    public BankFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout
        return inflater.inflate(R.layout.bank, container, false);
    }

    public void onResume() {
        super.onResume();
        displayBanks();
    }

    public void displayBanks() {
        banksList = db.getAllBanks();
        ListView lv = (ListView) getView().findViewById(R.id.listBank);
        BankArrayAdapter adapter = new BankArrayAdapter(getActivity(), banksList);
        lv.setAdapter(adapter);

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

        Bankroll = totalMoney + netProfit;

        TextView bankroll = (TextView) getView().findViewById(R.id.textTotalMoney);
        bankroll.setText("$" + Bankroll);
    }


}
