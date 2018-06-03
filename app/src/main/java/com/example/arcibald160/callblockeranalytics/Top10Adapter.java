package com.example.arcibald160.callblockeranalytics;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arcibald160.callblockeranalytics.callblocker.BlockListContract;

public class Top10Adapter extends RecyclerView.Adapter<Top10Adapter.Top10BlockedHolder> {

    private Context mContext;
    private Cursor mCursor;
    private int currentIndex = 1;

    public Top10Adapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Top10BlockedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.top10_list_element, parent, false);
        return new Top10BlockedHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Top10BlockedHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived._ID);
        int numberIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived.COLUMN_NUMBER);
        int nameIndex = mCursor.getColumnIndex(BlockListContract.BlockedCallsReceived.COLUMN_NAME);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String number = mCursor.getString(numberIndex);
        String name = mCursor.getString(nameIndex);
        int numberOfBlockedCalls = mCursor.getInt(3);

        if (name == null) {
            name = mContext.getString(R.string.unknown_name);
        }

        //Set values
        holder.itemView.setTag(id);
        holder.telNumber.setText(number);
        holder.contactName.setText(name);
        holder.orderNumber.setText(String.valueOf(currentIndex));
        holder.blockedCallsNumber.setText(String.valueOf(numberOfBlockedCalls));
        currentIndex++;
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

    public class Top10BlockedHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, telNumber, contactName, blockedCallsNumber;

        public Top10BlockedHolder(View itemView) {
            super(itemView);

            orderNumber = (TextView) itemView.findViewById(R.id.order_number);
            telNumber = (TextView) itemView.findViewById(R.id.tel_number);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            blockedCallsNumber = (TextView) itemView.findViewById(R.id.number_of_blocked_calls);
        }
    }
}
