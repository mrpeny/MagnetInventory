package com.example.mrpeny.magnetinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;
import com.example.mrpeny.magnetinventory.data.MagnetDbHelper;

import static android.R.id.list;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    MagnetDbHelper mDbHelper;
    MagnetCursorAdapter mMagnetCursorAdapter;

    private static final int MAGNET_LOADER = 0;

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

        getLoaderManager().initLoader(MAGNET_LOADER, null, this);

        /*
        insertDummyMagnet();
        insertDummyMagnet();

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        String[] projection = new String[] {
                MagnetEntry._ID,
                MagnetEntry.NAME,
                MagnetEntry.PRICE,
                MagnetEntry.QUANTITY };

        Cursor cursor = database.query(MagnetEntry.TABLE_NAME, projection, null, null, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(MagnetEntry.NAME);


        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumnIndex);
                resultTextView.append("\nName: " + name);
            }
        } finally {
            cursor.close();
        }
        */
    }

    private void insertDummyMagnet() {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MagnetEntry.NAME, "NEOMICRO 200x300");
        values.put(MagnetEntry.PRICE, 3000);
        values.put(MagnetEntry.QUANTITY, 18);
        values.put(MagnetEntry.SUPPLIER_PHONE, "+36203803295");

        database.insert(MagnetEntry.TABLE_NAME, null, values);
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
