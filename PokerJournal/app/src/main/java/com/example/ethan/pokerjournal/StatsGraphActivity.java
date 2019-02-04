package com.example.ethan.pokerjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StatsGraphActivity extends AppCompatActivity
{

    DatabaseHelper db;
    private Toolbar toolbar;
    List<Game> gameList;
    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_stats_graph);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(getApplicationContext());
        gameList = db.getAllGames();


        GameArrayAdapter adapter = new GameArrayAdapter(getApplicationContext(), gameList);

        // Sorts Games by Date2
        adapter.sort(new Comparator<Game>()
        {
            public int compare(Game arg0, Game arg1)
            {
                return arg0.date2.compareTo(arg1.date2);
            }
        });

        // generate Dates
        Date[] dates = new Date[gameList.size()];
        for (int i = 0; i < gameList.size() - 1; i++)
        {
            dates[i] = new Date(gameList.get(i).getDate());
        }

        GraphView graph = (GraphView) findViewById(R.id.statsGraph);

        DataPoint[] dataPoints = new DataPoint[gameList.size()];

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        Double netBankroll = 0.0;


        for (int i = 0; i < gameList.size() - 1; i++)
        {
            netBankroll += (gameList.get(i).getCashOut() - gameList.get(i).getBuyIn());
            dataPoints[i] = new DataPoint(dates[i], netBankroll);
            series.appendData(dataPoints[i], true, 100);
        }


        graph.addSeries(series);


// you can directly pass Date objects to DataPoint-Constructor


// this will convert the Date to double via Date#getTime()





        /*double y,x;
        x = -5.0;

        GraphView graph = (GraphView) findViewById(R.id.statsGraph);
        series = new LineGraphSeries<DataPoint>();
        for(int i = 0; i<500;i++) {
            x = x + 0.1;
            y = Math.sin(x);
            series.appendData(new DataPoint(x, y), true, 500);
        }
        graph.addSeries(series);*/

        /*
        // generate Dates
        Date[] dates = new Date[gameList.size()];
        for(int i = 0; i < gameList.size() - 1; i++){
            dates[i] = new Date(gameList.get(i).getDate());
        }

        GraphView graph = (GraphView) findViewById(R.id.statsGraph);

// you can directly pass Date objects to DataPoint-Constructor


// this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(dates[0], (gameList.get(0).getCashOut() - gameList.get(0).getBuyIn())),
                new DataPoint(dates[1], (gameList.get(1).getCashOut() - gameList.get(1).getBuyIn())),
                new DataPoint(dates[2], (gameList.get(2).getCashOut() - gameList.get(2).getBuyIn()))
        });

        */


// set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");

// set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-15000);
        graph.getViewport().setMaxY(15000);

        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        Date dMin = new Date(gameList.get(0).getDate());
        Date dMax = new Date(gameList.get(gameList.size() / 2 - 1).getDate());
// set manual x bounds to have nice steps
        graph.getViewport().setMinX(dMin.getTime());
        graph.getViewport().setMaxX(d1.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling

        graph.getGridLabelRenderer().setNumVerticalLabels(21);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Bankroll");

        graph.setTitle("Bankroll Breakdown");
        graph.setTitleTextSize(120);
        graph.setTitleColor(R.color.colorPrimaryDark);

    }

    // Functionality of Toolbar Back Arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


}
