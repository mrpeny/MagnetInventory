package com.example.mrpeny.magnetinventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;
import com.example.mrpeny.magnetinventory.data.MagnetDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Unique ID used during initialization of the Loader.
    private static final int MAGNET_LOADER = 0;
    MagnetDbHelper mDbHelper;
    MagnetCursorAdapter mMagnetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //TextView resultTextView = (TextView) findViewById(R.id.result_textView);
        mDbHelper = new MagnetDbHelper(this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_magnet_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        ListView catalogListView = (ListView) findViewById(R.id.catalog_list_view);

        View emptyView = findViewById(R.id.empty_view);
        catalogListView.setEmptyView(emptyView);

        mMagnetCursorAdapter = new MagnetCursorAdapter(this, null, 0);
        catalogListView.setAdapter(mMagnetCursorAdapter);

        // Start loader to retrieve database table
        getLoaderManager().initLoader(MAGNET_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MagnetEntry._ID,
                MagnetEntry.NAME,
                MagnetEntry.PRICE,
                MagnetEntry.QUANTITY
        };

        CursorLoader cursorLoader = new CursorLoader(
                this,
                MagnetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMagnetCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMagnetCursorAdapter.swapCursor(null);
    }
}
