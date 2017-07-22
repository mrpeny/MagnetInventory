package com.example.mrpeny.magnetinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

/**
 * Database helper class that creates and manages the magnet_inventory
 */

public class MagnetDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MagnetDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "magnets_database.db";

    private static final int DATABASE_VERSION = 1;

    public MagnetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO: change BLOB to NOT NULL
        final String SQL_CREATE_MAGNETS_TABLE = "CREATE TABLE " + MagnetEntry.TABLE_NAME + " (" +
                MagnetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MagnetEntry.NAME + " TEXT NOT NULL, " +
                MagnetEntry.PRICE + " INTEGER NOT NULL DEFAULT 0, " +
                MagnetEntry.QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                MagnetEntry.SUPPLIER_PHONE + " TEXT NOT NULL, " +
                MagnetEntry.IMAGE + " BLOB);";

        db.execSQL(SQL_CREATE_MAGNETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MagnetEntry.TABLE_NAME);
        onCreate(db);
    }
}
