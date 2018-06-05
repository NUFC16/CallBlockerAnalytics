package com.example.arcibald160.callblockeranalytics;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlockedPerMonthFragment extends Fragment {


    public BlockedPerMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_per_month, container, false);
        invokeBlockedPerMonthStatistics(view);
        return view;
    }

    private void invokeBlockedPerMonthStatistics(View view) {
        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.horizontalBarChartMonths);

        Integer[] blockedCallsPerMonth = getNumberOfCallsPerMonth();
        String[] monthsLabels = getContext().getResources().getStringArray(R.array.months_array);
        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        final ArrayList<String> yVals = new ArrayList<String>();

        List<BarEntry> entries = new ArrayList<BarEntry>();
        // 24 hours
        for (int i=0; i<blockedCallsPerMonth.length; i++) {
            // turn your data into Entry objects
            entries.add(new BarEntry(i, blockedCallsPerMonth[i]));

            yVals.add(i, monthsLabels[i]);

//            dataSets.add(dataSet);
        }
        BarDataSet dataSet = new BarDataSet(entries, "Blocked calls per month");
        BarData barData = new BarData(dataSet);

        //xAxis is a inverted yAxis since the graph is horizontal
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setLabelCount(blockedCallsPerMonth.length, false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return yVals.get((int)value);
            }
        });

        chart.getDescription().setEnabled(false);
        chart.setData(barData);

        // disable zoom
        chart.setScaleEnabled(false);

        chart.fitScreen();
        chart.getAxisLeft().setEnabled(false);
        chart.invalidate();
    }

    private Integer[] getNumberOfCallsPerMonth() {

        Map<String, Integer> monthsMap = new LinkedHashMap<String, Integer>();
        String[] monthsLabels = getResources().getStringArray(R.array.months_array);

        // init weeks array
        for(int i=0; i<monthsLabels.length; i++) {
            monthsMap.put(monthsLabels[i], 0);
        }

        Uri kUri = Uri.parse("content://com.example.arcibald160.callblocker/calls");

        Cursor c = getContext().getContentResolver().query(kUri, null, null, null, null);
        while (c.moveToNext()) {
            String num = c.getString(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM", Locale.US);
            try {
                Date dateTime = format.parse(c.getString(4) + " " + c.getString(3));

                String month = formatMonth.format(dateTime);
                monthsMap.put(month, monthsMap.get(month) + 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return monthsMap.values().toArray(new Integer[monthsMap.size()]);
    }

}
