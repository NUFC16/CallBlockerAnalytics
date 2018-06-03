package com.example.arcibald160.callblocker.tools;

import android.database.Cursor;

import com.example.arcibald160.callblocker.data.BlockListContract;

public class CursorTimetableHelper {

    private Cursor mCursor;
    private int idIndex, timeFromIndex, timeUntilIndex, isActivatedIndex;

    public int id;
    public String timeFrom, timeUntil;

    // you must move cursor to position yourself
    public CursorTimetableHelper(Cursor cursor) {
        mCursor = cursor;
        idIndex = mCursor.getColumnIndex(BlockListContract.BlockedTimetable._ID);
        timeFromIndex = mCursor.getColumnIndex(BlockListContract.BlockedTimetable.COLUMN_TIME_FROM);
        timeUntilIndex = mCursor.getColumnIndex(BlockListContract.BlockedTimetable.COLUMN_TIME_UNTIL);
        isActivatedIndex = mCursor.getColumnIndex(BlockListContract.BlockedTimetable.COLUMN_IS_ACTIVATED);

        // public values
        id = mCursor.getInt(idIndex);
        timeFrom = mCursor.getString(timeFromIndex);
        timeUntil = mCursor.getString(timeUntilIndex);
    }

    public boolean is_activated() {
        return mCursor.getString(isActivatedIndex).equals("1");
    }

    public int[] getDaysOfWeekIndices() {
        final String [] daysOfWeekColumns = BlockListContract.BlockedTimetable.getDaysOfWeekColumns();
        final int daysLength = daysOfWeekColumns.length;
        int [] daysOfWeekIndices = new int[daysLength];

        // get position of columns
        for(int i=0; i<daysLength; i++) {
            daysOfWeekIndices[i] = mCursor.getColumnIndex(daysOfWeekColumns[i]);
        }
        return daysOfWeekIndices;
    }
}
