package com.example.arcibald160.callblocker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.arcibald160.callblocker.tab_adapters.SectionsPageAdapter;
import com.example.arcibald160.callblocker.tools.SharedPreferencesHelper;
import com.example.arcibald160.callblocker.tab_fragments.Tab1Fragment;
import com.example.arcibald160.callblocker.tab_fragments.Tab2Fragment;
import com.example.arcibald160.callblocker.tab_fragments.Tab3Fragment;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private Menu menu;
    private static final int TAB_NUMBER = 3;
    SharedPreferencesHelper sharedPrefsHelper;
    Intent myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefsHelper = new SharedPreferencesHelper(this);

        // control tabs
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(TAB_NUMBER);
        setupViewPager(mViewPager);

        if (getIntent().hasExtra(getString(R.string.default_fragment))) {
            int position = getIntent().getIntExtra(getString(R.string.default_fragment), 0);
            mViewPager.setCurrentItem(position);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        int PERMISSION_ALL = 0;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    // check if app has permissions so we dont spam the user
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), getString(R.string.recent_blocked_numbers_label));
        adapter.addFragment(new Tab2Fragment(), getString(R.string.blocked_numbers_label));
        adapter.addFragment(new Tab3Fragment(), getString(R.string.busy_label));
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem busyIcon = menu.findItem(R.id.busy_mode_icon_id);

        if (sharedPrefsHelper.isBlockAllActivated()) {
            busyIcon.setIcon(R.mipmap.turn_on_round);
            menu.findItem(R.id.busy_mode_id).setChecked(true);
            showForegroundNotification();
        } else {
            busyIcon.setIcon(R.mipmap.turn_off_round);
            menu.findItem(R.id.busy_mode_id).setChecked(false);
            stopForegroundNotification();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.busy_mode_id:
                MenuItem busyIcon = menu.findItem(R.id.busy_mode_icon_id);

                boolean isBlockActivated;

                if (item.isChecked()) {
                    isBlockActivated = false;
                } else {
                    isBlockActivated = true;
                }
                item.setChecked(isBlockActivated);

                sharedPrefsHelper.setBlockAllState(isBlockActivated);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showForegroundNotification() {
        myService = new Intent(this, BlockAllNotification.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myService);
        } else {
            startService(myService);
        }
    }

    private void stopForegroundNotification() {
        if (myService != null) {
            myService.putExtra(getString(R.string.stop_blockall), true);
            startService(myService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
         sharedPrefsHelper.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        sharedPrefsHelper.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        invalidateOptionsMenu();
    }
}
