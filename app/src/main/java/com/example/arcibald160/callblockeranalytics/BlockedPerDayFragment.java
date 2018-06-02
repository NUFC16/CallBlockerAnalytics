package com.example.arcibald160.callblockeranalytics;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class BlockedPerDayFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_per_day, container, false);
        invokeBlockedPerDayStatistics(view);
        return view;
    }

    private void invokeBlockedPerDayStatistics(View view) {
        BarChart chart = (BarChart) view.findViewById(R.id.barChart);

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
        chart.setScaleEnabled(false);
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

        Cursor c = getContext().getContentResolver().query(kUri, null, null, null, null);
        while (c.moveToNext()) {
            String num = c.getString(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat formatDay = new SimpleDateFormat("E");
            try {
                Date dateTime = format.parse(c.getString(4) + " " + c.getString(3));

                String day = formatDay.format(dateTime);
                daysMap.put(day, daysMap.get(day) + 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return daysMap.values().toArray(new Integer[daysMap.size()]);
    }
}
