package com.example.arcibald160.callblocker.tab_adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arcibald160.callblocker.AddNewBlockedTimetable;
import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.data.BlockListContract;
import com.example.arcibald160.callblocker.tools.CursorTimetableHelper;


public class Tab3CustomAdapter extends RecyclerView.Adapter<Tab3CustomAdapter.BlockAllTimetableHolder> {
    private Context mContext;
    private Cursor mCursor;

    public Tab3CustomAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Tab3CustomAdapter.BlockAllTimetableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blockall_timetable_view, parent, false);
        return new Tab3CustomAdapter.BlockAllTimetableHolder(view);
    }

    @Override
    public void onBindViewHolder(Tab3CustomAdapter.BlockAllTimetableHolder holder, int position) {
        mCursor.moveToPosition(position);
        final CursorTimetableHelper cHelper = new CursorTimetableHelper(mCursor);

        //Set values
        holder.itemView.setTag(cHelper.id);
        holder.blockAllTimeFrom.setText(cHelper.timeFrom);
        holder.blockAllTimeUntil.setText(cHelper.timeUntil);
        holder.switchIsActive.setChecked(cHelper.is_activated());

        final Uri uriWithId = BlockListContract.BlockedTimetable.CONTENT_URI.buildUpon().appendPath(Integer.toString(cHelper.id)).build();

        holder.switchIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Toast.makeText(mContext, "Block time table is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();

                // Defines an object to contain the new values to insert
                ContentValues dbContentValues = new ContentValues();
                int booleanParse = isChecked ? 1:0;
                dbContentValues.put(BlockListContract.BlockedTimetable.COLUMN_IS_ACTIVATED, booleanParse);

                // update row's switch state
                int returnVal = mContext.getContentResolver().update(
                        uriWithId,
                        dbContentValues,
                        null,
                        null
                );
            }
        });

        // update
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddNewBlockedTimetable.class);
                intent.putExtra(BlockListContract.BlockedTimetable._ID, cHelper.id);
                mContext.startActivity(intent);
            }
        });

        // delete
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }

                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.getContentResolver().delete(uriWithId, null, null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();

                return true;
            }
        });
    }

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

    public class BlockAllTimetableHolder extends RecyclerView.ViewHolder {

        TextView blockAllTimeFrom, blockAllTimeUntil;
        Switch switchIsActive;
        LinearLayout parentLayout;

        public BlockAllTimetableHolder(View itemView) {
            super(itemView);

            blockAllTimeFrom = (TextView) itemView.findViewById(R.id.blocktime_from_id);
            blockAllTimeUntil = (TextView) itemView.findViewById(R.id.blocktime_until_id);
            switchIsActive = (Switch) itemView.findViewById(R.id.blockall_toggle_id);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.timetable_layout_id);
        }
    }
}
