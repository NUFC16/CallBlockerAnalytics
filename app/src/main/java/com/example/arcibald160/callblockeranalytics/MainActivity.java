package com.example.arcibald160.callblockeranalytics;

import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

        // inital fragment to show
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BlockedPerMonthFragment monthFragment = new BlockedPerMonthFragment();
        fragmentTransaction.add(R.id.fragment_container, monthFragment);
        fragmentTransaction.addToBackStack("MonthFragment");
        fragmentTransaction.commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.nav_top_10:
                        fragmentManager.popBackStack();
                        return true;

                    case R.id.nav_hour:
                        fragmentManager.popBackStack();
                        BlockedPerHourFragment hourFragment = new BlockedPerHourFragment();
                        fragmentTransaction.add(R.id.fragment_container, hourFragment);
                        fragmentTransaction.addToBackStack("HourFragment");
                        fragmentTransaction.commit();
                        return true;

                    case R.id.nav_day:
                        fragmentManager.popBackStack();
                        BlockedPerDayFragment dayFragment = new BlockedPerDayFragment();
                        fragmentTransaction.add(R.id.fragment_container, dayFragment);
                        fragmentTransaction.addToBackStack("DayFragment");
                        fragmentTransaction.commit();
                        return true;

                    case R.id.nav_month:
                        fragmentManager.popBackStack();
                        BlockedPerMonthFragment monthFragment = new BlockedPerMonthFragment();
                        fragmentTransaction.add(R.id.fragment_container, monthFragment);
                        fragmentTransaction.addToBackStack("MonthFragment");
                        fragmentTransaction.commit();
                        return true;

                }

                return false;
            }
        });
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
