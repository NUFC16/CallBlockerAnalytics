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
import android.widget.SearchView;

import com.example.arcibald160.callblocker.AddContactToBlockedList;
import com.example.arcibald160.callblocker.tab_adapters.Tab2CustomAdapter;
import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.data.BlockListContract;


public class Tab2Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private static final int LOADER_ID = 1200;

    private RecyclerView mRecyclerView;
    private Tab2CustomAdapter mAdapter;
    private SearchView mSearchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);

        // needed for querying existing data
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        // floating action button for adding new number on block list
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_blocked_id);

        // Get the SearchView and set the searchable configuration
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addContactToBlockedList = new Intent(getContext(), AddContactToBlockedList.class);
                startActivity(addContactToBlockedList);
            }
        });

        // Recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_tab2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // specify an adapter (see also next example)
        mAdapter = new Tab2CustomAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // CursorLoader for displaying data from content provider
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;

        if (args != null) {
            selection = BlockListContract.BlockListEntry.COLUMN_NAME + " LIKE ? OR " +
                    BlockListContract.BlockListEntry.COLUMN_NUMBER + " LIKE ?";
            String userInput = '%' + args.getString("search_string") + '%';
            selectionArgs = new String[] { userInput, userInput};
        }
        return new CursorLoader(
                getContext(),
                BlockListContract.BlockListEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateData(null);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("search_string", s);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("search_string", s);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
        return true;
    }
}
