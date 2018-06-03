package com.example.arcibald160.callblocker;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.arcibald160.callblocker.data.BlockListContract;
import com.example.arcibald160.callblocker.tools.CursorTimetableHelper;
import com.example.arcibald160.callblocker.tools.TimePickerFragment;

import java.util.ArrayList;
import java.util.List;

public class AddNewBlockedTimetable extends AppCompatActivity {

    EditText mEditTimeFrom, mEditTimeUntil;
    Switch mIsActivated;
    Button mSubmitButton;

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_view);

        mEditTimeFrom = (EditText) findViewById(R.id.edit_time_from);
        mEditTimeUntil = (EditText) findViewById(R.id.edit_time_until);
        mIsActivated = (Switch)  findViewById(R.id.timetable_switch);
        mSubmitButton = (Button) findViewById(R.id.submit_button);

        // update / add
        if (getIntent().hasExtra(BlockListContract.BlockedTimetable._ID)) {
            populateWithExistingData();
        }

        // trigger only on one click
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        // time from time picker
        mEditTimeFrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    DialogFragment newFragment = new TimePickerFragment(mEditTimeFrom);
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                    return true;
                }
                return false;
            }
        });

        // time until time picker
        mEditTimeUntil.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    DialogFragment newFragment = new TimePickerFragment(mEditTimeUntil);
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                    return true;
                }
                return false;
            }
        });

        // add or update data
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEditTimeFrom.getText().toString()) || TextUtils.isEmpty(mEditTimeFrom.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_number), Toast.LENGTH_LONG).show();
                    return;
                }

                // update / add
                if (getIntent().hasExtra(BlockListContract.BlockedTimetable._ID)) {
                    updateBlockedTimetable();
                } else {
                    addDataToBlockedTimetable();
                }
                finish();
            }
        });

        // back navigation arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // prevent timepicker from being called twice (bug)
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    private void populateWithExistingData() {
        int id = getIntent().getIntExtra(BlockListContract.BlockedTimetable._ID, 0);
        Cursor result = getApplicationContext().getContentResolver().query(
                BlockListContract.BlockedTimetable.CONTENT_URI, null,
                "_id=?",
                new String[]{Integer.toString(id)},
                null
        );

        if (result != null) {

            result.moveToFirst();
            CursorTimetableHelper cHelper = new CursorTimetableHelper(result);

            // set text for time from and until
            mEditTimeFrom.setText(cHelper.timeFrom);
            mEditTimeUntil.setText(cHelper.timeUntil);

            // set state of toggle buttons
            boolean checkedState;
            ArrayList<ToggleButton> allToggleButtons = getToggleButtons();
            int [] daysOfWeekIndices = cHelper.getDaysOfWeekIndices();

            for(int i=0; i<daysOfWeekIndices.length; i++) {
                checkedState = (result.getString(daysOfWeekIndices[i]).equals("1")) ? true:false;
                allToggleButtons.get(i).setChecked(checkedState);
            }

            // set state of is_active switch
            mIsActivated.setChecked(cHelper.is_activated());
        }
    }

    private ContentValues getDbValues() {
        // Defines an object to contain the new values to insert
        ContentValues dbContentValues = new ContentValues();

        // insert timeframe
        dbContentValues.put(BlockListContract.BlockedTimetable.COLUMN_TIME_FROM, mEditTimeFrom.getText().toString());
        dbContentValues.put(BlockListContract.BlockedTimetable.COLUMN_TIME_UNTIL, mEditTimeUntil.getText().toString());

        // insert toggle buttons state

        // get all elements
        String [] contentProviderAttributes = BlockListContract.BlockedTimetable.getDaysOfWeekColumns();

        // here must be exactly 7 toggle buttons -> like 7 days
        ArrayList<ToggleButton> allToggleButtons = getToggleButtons();

        if (allToggleButtons.size() != contentProviderAttributes.length) {
            throw new RuntimeException("Toggle buttons and database data are not the same length!");
        }

        int booleanParse;
        for (int i = 0; i < allToggleButtons.size(); i++) {
            booleanParse = (allToggleButtons.get(i)).isChecked() ? 1:0;
            dbContentValues.put(contentProviderAttributes[i], Integer.toString(booleanParse));
        }

        booleanParse = mIsActivated.isChecked() ? 1:0;
        dbContentValues.put(BlockListContract.BlockedTimetable.COLUMN_IS_ACTIVATED, booleanParse);
        return dbContentValues;
    }

    private void updateBlockedTimetable() {
        // insert data in content provider
        int id = getIntent().getIntExtra(BlockListContract.BlockedTimetable._ID, 0);
        Uri uri = BlockListContract.BlockedTimetable.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        int returnValue = getApplicationContext().getContentResolver().update(
                uri,
                getDbValues(),  // the values to update
                null,
                null
        );
    }

    // find all monday-sunday toggle buttons
    private void addDataToBlockedTimetable(){
        // insert data in content provider
        Uri returnUri = getApplicationContext().getContentResolver().insert(
                BlockListContract.BlockedTimetable.CONTENT_URI,
                getDbValues() // the values to insert
        );
    }

    // find whole hierarchy tree
    private List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }

    private ArrayList<ToggleButton> getToggleButtons() {
        List<View> allElements = getAllChildrenBFS(findViewById(R.id.timetable_root_id));
        ArrayList<ToggleButton> buttonList = new ArrayList<>();

        for (int i = 0; i < allElements.size(); i++) {
            final View currElement = allElements.get(i);
            if (currElement instanceof ToggleButton) {
                buttonList.add((ToggleButton) currElement);
            }
        }
        return buttonList;
    }


    //    used for navigating back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
