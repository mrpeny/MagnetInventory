package com.example.mrpeny.magnetinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

/**
 * Content Provider that manages CRUD methods in magnet_inventory database
 */

public class MagnetProvider extends ContentProvider {

    private final static int MAGNETS = 100;

    private final static int MAGNET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MagnetContract.CONTENT_AUTHORITY, MagnetContract.PATH_MAGNETS, MAGNETS);

        sUriMatcher.addURI(MagnetContract.CONTENT_AUTHORITY, MagnetContract.PATH_MAGNETS + "/#",
                MAGNET_ID);
    }

    private MagnetDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MagnetDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MAGNETS:
                cursor = database.query(MagnetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                break;
            case MAGNET_ID:
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MagnetEntry.TABLE_NAME, projection,
                        MagnetEntry._ID + "=?", selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri.toString());
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
