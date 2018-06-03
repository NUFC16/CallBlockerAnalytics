package com.example.arcibald160.callblocker.tab_fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arcibald160.callblocker.AddNewBlockedTimetable;
import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.tab_adapters.Tab3CustomAdapter;
import com.example.arcibald160.callblocker.data.BlockListContract;

public class Tab3Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1300;

    private RecyclerView mRecyclerView;
    private Tab3CustomAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);

        // needed for querying existing data
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        // Recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_tab3);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_blockall_id);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timetableIntent = new Intent(getContext(), AddNewBlockedTimetable.class);
                startActivity(timetableIntent);
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // specify an adapter (see also next example)
        mAdapter = new Tab3CustomAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                BlockListContract.BlockedTimetable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {mAdapter.updateData(data);}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateData(null);
    }
}
