package com.example.mrpeny.magnetinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;
import com.example.mrpeny.magnetinventory.data.MagnetDbHelper;

public class CatalogActivity extends AppCompatActivity {

    MagnetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        TextView resultTextView = (TextView) findViewById(R.id.result_textView);
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
}
