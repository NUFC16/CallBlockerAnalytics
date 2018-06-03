package com.example.arcibald160.callblocker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlockListDbHelper extends SQLiteOpenHelper {
    // The name of the database
    private static final String DATABASE_NAME = "blockedNumbersDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 4;


    // Constructor
    BlockListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_BLOCKED_NUMBERS = "CREATE TABLE "  + BlockListContract.BlockListEntry.TABLE_NAME + " (" +
                BlockListContract.BlockedCallsReceived._ID + " INTEGER PRIMARY KEY, " +
                BlockListContract.BlockedCallsReceived.COLUMN_NUMBER + " TEXT NOT NULL, " +
                BlockListContract.BlockedCallsReceived.COLUMN_NAME    + " TEXT);";

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE_BLOCKED_CALLS = "CREATE TABLE "  + BlockListContract.BlockedCallsReceived.TABLE_NAME + " (" +
                BlockListContract.BlockedCallsReceived._ID + " INTEGER PRIMARY KEY, " +
                BlockListContract.BlockedCallsReceived.COLUMN_NUMBER + " TEXT NOT NULL, " +
                BlockListContract.BlockedCallsReceived.COLUMN_NAME    + " TEXT ," +
                BlockListContract.BlockedCallsReceived.COLUMN_TIME + " TIME NOT NULL, " +
                BlockListContract.BlockedCallsReceived.COLUMN_DATE + " DATE NOT NULL);";

        final String CREATE_TABLE_BLOCKED_TIMETABLE = "CREATE TABLE "  + BlockListContract.BlockedTimetable.TABLE_NAME + " (" +
                BlockListContract.BlockedTimetable._ID + " INTEGER PRIMARY KEY, " +
                BlockListContract.BlockedTimetable.COLUMN_IS_ACTIVATED + " BOOLEAN NOT NULL DEFAULT 0," +
                BlockListContract.BlockedTimetable.COLUMN_TIME_FROM + " TIME NOT NULL, " +
                BlockListContract.BlockedTimetable.COLUMN_TIME_UNTIL    + " TIME NOT NULL ," +
                BlockListContract.BlockedTimetable.COLUMN_MONDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_TUESDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_WEDNESDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_THURSDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_FRIDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_SATURDAY + " BOOLEAN NOT NULL DEFAULT 0, " +
                BlockListContract.BlockedTimetable.COLUMN_SUNDAY + " BOOLEAN NOT NULL DEFAULT 0);";


        db.execSQL(CREATE_TABLE_BLOCKED_NUMBERS);
        db.execSQL(CREATE_TABLE_BLOCKED_CALLS);
        db.execSQL(CREATE_TABLE_BLOCKED_TIMETABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BlockListContract.BlockListEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BlockListContract.BlockedCallsReceived.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BlockListContract.BlockedTimetable.TABLE_NAME);
        onCreate(db);
    }
}
