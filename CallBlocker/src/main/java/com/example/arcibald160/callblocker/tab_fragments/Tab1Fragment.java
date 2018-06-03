package com.example.arcibald160.callblocker.tab_fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.tab_adapters.Tab1CustomAdapter;
import com.example.arcibald160.callblocker.data.BlockListContract;

public class Tab1Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1100;

    private RecyclerView mRecyclerViewTab1;
    private Tab1CustomAdapter mAdapterTab1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        // needed for querying existing data
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        // Recycler view
        mRecyclerViewTab1 = (RecyclerView) view.findViewById(R.id.recycler_view_tab1);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewTab1.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerViewTab1.setLayoutManager(new LinearLayoutManager(getContext()));

        // specify an adapter (see also next example)
        mAdapterTab1 = new Tab1CustomAdapter(getContext());
        mRecyclerViewTab1.setAdapter(mAdapterTab1);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                BlockListContract.BlockedCallsReceived.CONTENT_URI,
                null,
                null,
                null,
                BlockListContract.BlockedCallsReceived.COLUMN_DATE + " DESC, " +
                         BlockListContract.BlockedCallsReceived.COLUMN_TIME + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)  {
        mAdapterTab1.updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)  {
        mAdapterTab1.updateData(null);
    }
}
