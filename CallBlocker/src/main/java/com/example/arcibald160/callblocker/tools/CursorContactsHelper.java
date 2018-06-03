package com.example.arcibald160.callblocker.tools;

import android.database.Cursor;

import com.example.arcibald160.callblocker.data.BlockListContract;

public class CursorContactsHelper {
    private Cursor mCursor;
    private int idIndex, nameIndex, numberIndex;

    public String contactName, contactNumber;
    public int id;

    public CursorContactsHelper(Cursor cursor) {
        mCursor = cursor;

        idIndex = mCursor.getColumnIndex(BlockListContract.BlockListEntry._ID);
        nameIndex = mCursor.getColumnIndex(BlockListContract.BlockListEntry.COLUMN_NAME);
        numberIndex = mCursor.getColumnIndex(BlockListContract.BlockListEntry.COLUMN_NUMBER);

        // public values
        id = mCursor.getInt(idIndex);
        contactName = mCursor.getString(nameIndex);
        contactNumber = mCursor.getString(numberIndex);
    }
}
