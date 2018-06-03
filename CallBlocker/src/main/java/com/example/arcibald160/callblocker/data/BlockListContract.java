package com.example.arcibald160.callblocker.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class BlockListContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.arcibald160.callblocker";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "numbers" directory
    public static final String PATH_BLOCKED_NUMBERS = "numbers";
    public static final String PATH_BLOCKED_CALLS = "calls";
    public static final String PATH_BLOCKED_TIMETABLE = "timetable";

    /* BlockListEntry is an inner class that defines the contents of the blocked calls table */
    public static final class BlockListEntry implements BaseColumns {

        // BlockListEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOCKED_NUMBERS).build();

        public static final String TABLE_NAME = "blocked_numbers";

        // "_ID" column is auto-produced
        public static final String
                COLUMN_NUMBER = "number",
                COLUMN_NAME = "contact_name";
    }

    public static final class BlockedCallsReceived implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOCKED_CALLS).build();

        public static final String TABLE_NAME = "blocked_calls";

        public static final String
                COLUMN_NUMBER = "number",
                COLUMN_NAME = "contact_name",
                COLUMN_TIME = "time",
                COLUMN_DATE = "date";
    }

    public static final class BlockedTimetable implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOCKED_TIMETABLE).build();

        public static final String TABLE_NAME = "blocked_timetable";

        public static final String
                COLUMN_IS_ACTIVATED = "is_activated",
                COLUMN_TIME_FROM = "time_from",
                COLUMN_TIME_UNTIL = "time_until",
                COLUMN_MONDAY = "monday",
                COLUMN_TUESDAY = "tuesday",
                COLUMN_WEDNESDAY = "wednesday",
                COLUMN_THURSDAY = "thursday",
                COLUMN_FRIDAY = "friday",
                COLUMN_SATURDAY = "saturday",
                COLUMN_SUNDAY = "sunday";

        public static String[] getDaysOfWeekColumns() {
            return new String[] {
                    BlockListContract.BlockedTimetable.COLUMN_MONDAY,
                    BlockListContract.BlockedTimetable.COLUMN_TUESDAY,
                    BlockListContract.BlockedTimetable.COLUMN_WEDNESDAY,
                    BlockListContract.BlockedTimetable.COLUMN_THURSDAY,
                    BlockListContract.BlockedTimetable.COLUMN_FRIDAY,
                    BlockListContract.BlockedTimetable.COLUMN_SATURDAY,
                    BlockListContract.BlockedTimetable.COLUMN_SUNDAY
            };
        }
    }
}
