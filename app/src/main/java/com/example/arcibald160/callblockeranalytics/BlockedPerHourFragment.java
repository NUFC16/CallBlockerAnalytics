package com.example.arcibald160.callblockeranalytics;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockedPerHourFragment extends Fragment {
    private static int hoursNum;

    public BlockedPerHourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_per_hour, container, false);
        hoursNum = getContext().getResources().getInteger(R.integer.hours_number);
        invokeBlockedPerHourStatistics(view);
        return view;
    }

    private void invokeBlockedPerHourStatistics(View view) {
        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.horizontalBarChartHours);

        int[] blockedCallsPerHour = getNumberOfCallsPerHour();

        // turn your data into Entry objects
        List<BarEntry> entries = new ArrayList<BarEntry>();
        String[] labels = new String[hoursNum];

        // 24 hours
        for (int i=0; i<hoursNum; i++) {
            labels[i] = Integer.toString(i);
            entries.add(new BarEntry(i+1, blockedCallsPerHour[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Blocked calls per hour");
        BarData barData = new BarData(dataSet);

        //xAxis is a inverted yAxis since the graph is horizontal
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(hoursNum, false);

        chart.getDescription().setEnabled(false);
        chart.setData(barData);

        // disable zoom
        chart.setScaleEnabled(false);

        chart.fitScreen();
        chart.getAxisLeft().setEnabled(false);
        chart.invalidate();
    }

    private int[] getNumberOfCallsPerHour() {

        int[] blockedCallsPerHour = new int[hoursNum];

        // init days array
        for(int i=0; i<hoursNum; i++) {
            blockedCallsPerHour[i] = 0;
        }

        Uri kUri = Uri.parse("content://com.example.arcibald160.callblocker/calls");

        Cursor c = getContext().getContentResolver().query(kUri, null, null, null, null);
        while (c.moveToNext()) {
            String num = c.getString(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat formatHour = new SimpleDateFormat("k");
            try {
                Date dateTime = format.parse(c.getString(4) + " " + c.getString(3));

                String hour = formatHour.format(dateTime);
                // -1 because it is 0 based
                blockedCallsPerHour[Integer.parseInt(hour)-1]++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return blockedCallsPerHour;
    }

}
