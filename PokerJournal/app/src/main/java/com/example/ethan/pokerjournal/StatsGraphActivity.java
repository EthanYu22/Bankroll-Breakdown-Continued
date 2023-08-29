package com.example.ethan.pokerjournal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StatsGraphActivity extends AppCompatActivity
{

    DatabaseHelper db;
    List<Session> sessionList;
    LineGraphSeries<DataPoint> series;
    SessionArrayAdapter adapter;
    private Toolbar toolbar;

    Date beginDate;
    Date endDate;
    int monthDiff;
    int yearDiff;
    int netBankroll;
    int minBankroll;
    int maxBankroll;
    double yAxisRange;
    double yBoundary;
    double verticalIncrementCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_stats_graph);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(getApplicationContext());
        sessionList = db.getAllSessions();

        final SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");
        GraphView graph = (GraphView) findViewById(R.id.statsGraph);
        adapter = new SessionArrayAdapter(getApplicationContext(), sessionList);
        adapter.sort(new Comparator<Session>()
        {
            public int compare(Session arg0, Session arg1)
            {
                return arg0.date.compareTo(arg1.date);
            }
        });

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Date[] dates = new Date[sessionList.size() + 1];
        DataPoint[] dataPoints = new DataPoint[sessionList.size() + 1];

        beginDate = new Date(sessionList.get(0).getConvertedDateMMddyyyy());
        endDate = new Date(sessionList.get(sessionList.size() - 1).getConvertedDateMMddyyyy());

        Calendar beginDateCal = Calendar.getInstance();
        beginDateCal.setTime(beginDate);
        beginDateCal.set(Calendar.DAY_OF_MONTH, beginDateCal.getActualMinimum(Calendar.DAY_OF_MONTH));
        beginDate = beginDateCal.getTime();
        Calendar cal20Plus = Calendar.getInstance();
        cal20Plus.setTime(endDate);
        cal20Plus.add(Calendar.DATE, 20);
        Calendar calEOM = Calendar.getInstance();
        calEOM.setTime(endDate);
        calEOM.set(Calendar.DATE, calEOM.getActualMaximum(Calendar.DAY_OF_MONTH));
        if(cal20Plus.compareTo(calEOM) > 0 || cal20Plus.compareTo(calEOM) == 0)
        {
            endDate = cal20Plus.getTime();
            monthDiff = cal20Plus.get(Calendar.MONTH) - beginDateCal.get(Calendar.MONTH);
            yearDiff = cal20Plus.get(Calendar.YEAR) - beginDateCal.get(Calendar.YEAR);
        }
        else
        {
            endDate = calEOM.getTime();
            monthDiff = calEOM.get(Calendar.MONTH) - beginDateCal.get(Calendar.MONTH);
            yearDiff = calEOM.get(Calendar.YEAR) - beginDateCal.get(Calendar.YEAR);
        }

        dates[0] = beginDate;
        dataPoints[0] = new DataPoint(dates[0], 0);
        series.appendData(dataPoints[0], true, 1000000);

        for (int i = 1; i < sessionList.size() + 1; i++)
        {
            dates[i] = new Date(sessionList.get(i-1).getConvertedDateMMddyyyy());
        }

        for (int i = 1; i < sessionList.size() + 1; i++)
        {
            netBankroll += (sessionList.get(i-1).getProfit());
            dataPoints[i] = new DataPoint(dates[i], netBankroll);
            series.appendData(dataPoints[i], true, 1000000);
            if(minBankroll > netBankroll) minBankroll = netBankroll;
            if(maxBankroll < netBankroll) maxBankroll = netBankroll;
        }

        if(Math.abs(minBankroll) > maxBankroll)
        {
            yAxisRange = minBankroll;
        }
        else {
            yAxisRange = maxBankroll;
        }

        graph.addSeries(series);

        // set date label formatter
        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(beginDate.getTime());
        graph.getViewport().setMaxX(endDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(yearDiff * 12 + monthDiff + 1 > 12 ? 12 : yearDiff * 12 + monthDiff + 1); // only 4 because of the space

        if( (yAxisRange/100) * 2 + 1 < 41){
            verticalIncrementCnt = Math.ceil(yAxisRange/100) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/100) * 100;
        }
        else if((yAxisRange/500) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/500) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/500) * 500;
        }
        else if((yAxisRange/1000) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/1000) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/1000) * 1000;
        }
        else if((yAxisRange/1500) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/1500) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/1500) * 1500;
        }
        else if((yAxisRange/2000) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/2000) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/2000) * 2000;
        }
        else if((yAxisRange/3000) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/3000) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/3000) * 3000;
        }
        else if((yAxisRange/4000) * 2 + 1 < 41)
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/4000) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/4000) * 4000;
        }
        else
        {
            verticalIncrementCnt = Math.ceil(yAxisRange/5000) * 2 + 1;
            yBoundary = Math.ceil(yAxisRange/5000) * 5000;
        }

        graph.getGridLabelRenderer().setNumVerticalLabels((int) verticalIncrementCnt);

        // set manual Y bounds
        graph.getViewport().setMinY(yBoundary * -1);
        graph.getViewport().setMaxY(yBoundary);
        graph.getViewport().setYAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Session Dates");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(0,128,0));
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(45);
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(65);

        graph.getGridLabelRenderer().setVerticalAxisTitle("Net Profits");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.rgb(0,128,0));
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(65);

        graph.getGridLabelRenderer().setTextSize(25);

        series.setColor(Color.rgb(0,128,0));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(7);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface)
            {
                Date pointDate = new Date((long) dataPointInterface.getX());
                String strDate = sdf.format(pointDate);
                String dataPoint;
                if(dataPointInterface.getY() > 0 || dataPointInterface.getY() == 0)
                {
                    dataPoint = "Session Date: " + strDate + "\nNet Profit: $" + (int) dataPointInterface.getY();
                }
                else
                {
                    dataPoint = "Session Date: " + strDate + "\nNet Profit: -$" + (int) Math.abs(dataPointInterface.getY());
                }
                Toast.makeText(StatsGraphActivity.this, dataPoint, Toast.LENGTH_SHORT).show();
            }
        });
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
