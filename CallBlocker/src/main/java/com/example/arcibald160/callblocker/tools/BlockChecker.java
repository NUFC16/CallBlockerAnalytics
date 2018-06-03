package com.example.arcibald160.callblocker.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.example.arcibald160.callblocker.data.BlockListContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BlockChecker {

    Context mContext;
    String phoneNumber;

    public BlockChecker(Context context, String number) {
        mContext = context;
        phoneNumber = number;
    }

    public boolean canBlock() {
        return this.isNumberBlocked() || this.isTimeBlocked() || this.isNumberUnknown() || this.isGlobalBlockOn();
    }

    private boolean isTimeBlocked() {

        // get current day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        // get current time
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String curTimeString = dateFormat.format(calendar.getTime());
        Date curTime;

        try {
            curTime = dateFormat.parse(curTimeString);
        } catch (ParseException e) {
            // parsing was not successful, don't block the call
            e.printStackTrace();
            return false;
        }

        // Get all the values in BlockedTimetable
        Uri uri = BlockListContract.BlockedTimetable.CONTENT_URI;

        Cursor allValuesCursor = mContext.getContentResolver().query(
                uri,null,null,null,null
        );

        boolean isDayBlocked = false;
        while (allValuesCursor.moveToNext()) {
            CursorTimetableHelper cHelper = new CursorTimetableHelper(allValuesCursor);

            // if timetable is not activated continue to next
            if (!cHelper.is_activated()) continue;

            int [] daysOfWeekIndices = cHelper.getDaysOfWeekIndices();
            // determines which day is to be checked (default monday)
            int index = 0;

            // see if current day matches some blocked days in the timetable
            switch (day) {
                case Calendar.MONDAY:
                    // 1 -> true, 0 -> false : true is trigger for blocking
                    index = 0;
                    break;
                case Calendar.TUESDAY:
                    index = 1;
                    break;
                case Calendar.WEDNESDAY:
                    index = 2;
                    break;
                case Calendar.THURSDAY:
                    index = 3;
                    break;
                case Calendar.FRIDAY:
                    index = 4;
                    break;
                case Calendar.SATURDAY:
                    index = 5;
                    break;
                case Calendar.SUNDAY:
                    index = 6;
                    break;
            }
            isDayBlocked = (allValuesCursor.getString(daysOfWeekIndices[index]).equals("1")) ? true:false;

            if (isDayBlocked) {
                // check if time is in timetable timeframe
                Date timeFrom, timeUntil;
                try {
                    timeFrom = dateFormat.parse(cHelper.timeFrom);
                    timeUntil = dateFormat.parse(cHelper.timeUntil);
                } catch (ParseException e) {
                    // parsing was not successful
                    e.printStackTrace();
                    continue;
                }
                int a = timeFrom.compareTo(curTime);
                int b = timeUntil.compareTo(curTime);

                // time.compareTo(time2) == -1 if time < time2
                if (timeFrom.compareTo(curTime) <= 0 && timeUntil.compareTo(curTime) >= 0) {
                    // if day is blocked exit and time is in the time frame send the signal for cancelling the call
                    allValuesCursor.close();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGlobalBlockOn() {
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(mContext);
        return prefs.isBlockAllActivated();
    }

    private boolean isNumberUnknown() {
        //TODO: write this function
        return false;
    }

    private boolean isNumberBlocked() {
        // Check, whether this is a member of "Black listed" phone numbers stored in the database
        Uri uri = BlockListContract.BlockListEntry.CONTENT_URI;

        // search only number column
        Cursor mCursor = mContext.getContentResolver().query(
                uri,
                new String[] {BlockListContract.BlockListEntry.COLUMN_NUMBER},
                BlockListContract.BlockListEntry.COLUMN_NUMBER + "=?",
                new String[]{ phoneNumber },
                null
        );

        boolean retVal = (mCursor.getCount() != 0);
        mCursor.close();

        return retVal;
    }
}
