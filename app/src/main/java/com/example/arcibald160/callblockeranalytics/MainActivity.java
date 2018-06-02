package com.example.arcibald160.callblockeranalytics;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add hamburger menu to the navbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set hamburger menu icon
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        BarChart chart = (BarChart) findViewById(R.id.barChart);

        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        String[] daysOfTheWeek = getResources().getStringArray(R.array.days_array);
        Integer[] daysOfTheWeekValues = getNumberOfCallsPerDay();

        int currColor = 0;
        for (int i=0; i<daysOfTheWeek.length; i++) {

            // turn your data into Entry objects
            List<BarEntry> entries = new ArrayList<BarEntry>();
            entries.add(new BarEntry(i+1, daysOfTheWeekValues[i]));
            BarDataSet dataSet = new BarDataSet(entries, daysOfTheWeek[i]);

            dataSet.setColor(ColorTemplate.COLORFUL_COLORS[currColor]);
            dataSets.add(dataSet);
            currColor = ((currColor +1 ) >= ColorTemplate.COLORFUL_COLORS.length) ? 0: currColor + 1;
        }
        BarData barData = new BarData(dataSets);
        chart.getDescription().setEnabled(false);
        chart.setData(barData);
        chart.fitScreen();

        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();

    }

    private Integer[] getNumberOfCallsPerDay() {

        Map<String, Integer> daysMap = new HashMap<String, Integer>();
        String[] daysOfTheWeek = getResources().getStringArray(R.array.days_array);

        // init weeks array
        for(int i=0; i<daysOfTheWeek.length; i++) {
            daysMap.put(daysOfTheWeek[i], 0);
        }

        Uri kUri = Uri.parse("content://com.example.arcibald160.callblocker/calls");

        Cursor c = getContentResolver().query(kUri, null, null, null, null);
        while (c.moveToNext()) {
            String num = c.getString(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat formatDay = new SimpleDateFormat("E");
//            SimpleDateFormat formatHour = new SimpleDateFormat("k");
            try {
                Date dateTime = format.parse(c.getString(4) + " " + c.getString(3));

                String day = formatDay.format(dateTime);
                daysMap.put(day, daysMap.get(day) + 1);
//                String hour = formatHour.format(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return daysMap.values().toArray(new Integer[daysMap.size()]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // open drawer of a hamburger menu
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
