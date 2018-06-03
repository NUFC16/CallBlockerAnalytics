package com.example.arcibald160.callblocker.tab_adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.data.BlockListContract;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tab1CustomAdapter extends RecyclerView.Adapter<Tab1CustomAdapter.ListBlockedViewHolder> {
    private Context mContext;
    // Class variables for the Cursor that holds list of blocked calls data
    private Cursor mCursor;

    public Tab1CustomAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ListBlockedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.blockedcalls_view, parent, false);
        return new ListBlockedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListBlockedViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived._ID);
        int numberIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived.COLUMN_NUMBER);
        int dateIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived.COLUMN_DATE);
        int timeIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived.COLUMN_TIME);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        // get number
        String number = mCursor.getString(numberIndex);
        String datetime = this.showRightTimestamp(dateIndex, timeIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.blockedNumberView.setText(number);
        holder.blockedDateTimeView.setText(datetime);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor updateData(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    private String showRightTimestamp(int dateIdx, int timeIdx) {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
        Date callDate;
        Time callTime;
        try {
            callDate = df.parse(mCursor.getString(dateIdx));
            callTime = new Time(tf.parse(mCursor.getString(timeIdx)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        // if date is same as today show hours else show date
        if (df.format(today).equals(df.format(callDate))) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            return timeFormat.format(callTime);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
            return dateFormat.format(callDate);
        }

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ListBlockedViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView blockedNumberView;
        TextView blockedDateTimeView;

        public ListBlockedViewHolder(View itemView) {
            super(itemView);

            blockedDateTimeView = (TextView) itemView.findViewById(R.id.call_datetime_id);
            blockedNumberView = (TextView) itemView.findViewById(R.id.blocked_call_id);
        }
    }
}
