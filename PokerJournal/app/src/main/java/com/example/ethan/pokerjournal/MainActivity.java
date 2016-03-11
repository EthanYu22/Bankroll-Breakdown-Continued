package com.example.ethan.pokerjournal;
/*
Using a guide from www.androidhive.info for tabs and sqlite
 */
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    List<Game> gameList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        gameList = db.getAllGames();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager upViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HistoryFragment(), "History");
        adapter.addFragment(new StatsFragment(), "Stats");
        adapter.addFragment(new BankFragment(), "Bank");
        viewPager.setAdapter(adapter);
    }

    public void onClickNewGame(View v) {
        Intent intent = new Intent(MainActivity.this, GameFormActivity.class);
        startActivity(intent);
    }

    /*
    public void onResume() {
        super.onResume();
        displayGames();
    }

    public void displayGames() {
        ListView lv = (ListView) findViewById(R.id.listGames);
        GameArrayAdapter adapter = new GameArrayAdapter(this, gameList);
        lv.setAdapter(adapter);
    }
    */
}
