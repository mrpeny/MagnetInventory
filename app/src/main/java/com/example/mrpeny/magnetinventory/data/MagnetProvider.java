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
import android.util.Log;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static android.R.attr.name;
import static android.os.Build.VERSION_CODES.M;

/**
 * Content Provider that manages CRUD methods in magnet_inventory database
 */

public class MagnetProvider extends ContentProvider {
    private static final String LOG_TAG = MagnetProvider.class.getSimpleName();

    private final static int MAGNETS = 100;
    private final static int MAGNET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private MagnetDbHelper mDbHelper;

    static {
        sUriMatcher.addURI(MagnetContract.CONTENT_AUTHORITY, MagnetContract.PATH_MAGNETS, MAGNETS);

        sUriMatcher.addURI(MagnetContract.CONTENT_AUTHORITY, MagnetContract.PATH_MAGNETS + "/#",
                MAGNET_ID);
    }

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
        final int match = sUriMatcher.match(uri);
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
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MAGNETS:
                return insertMagnet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMagnet(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(MagnetEntry.NAME);
        if (name == null) {
            throw new IllegalArgumentException("No name value added to magnet.");
        }

        Integer price = contentValues.getAsInteger(MagnetEntry.PRICE);
        if (price != null && price < 0){
            throw new IllegalArgumentException("No valid price added to magnet.");
        }

        Integer quantity = contentValues.getAsInteger(MagnetEntry.QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("No valid quantity added to magnet.");
        }

        String supplierPhone = contentValues.getAsString(MagnetEntry.SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("No supplier added to magnet.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long newMagnetID = database.insert(MagnetEntry.TABLE_NAME, null, contentValues);

        if (newMagnetID == -1) {
            Log.e(LOG_TAG, "Failed to insert magnet.");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newMagnetID);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsAffected;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MAGNETS:
                rowsAffected = database.delete(MagnetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MAGNET_ID:
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsAffected = database.delete(MagnetEntry.TABLE_NAME, MagnetEntry._ID + "=?", selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MAGNETS:
                return updateMagnet(uri, contentValues, selection, selectionArgs);
            case MAGNET_ID:
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMagnet(uri, contentValues, MagnetEntry._ID + "=?", selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMagnet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }

        if (contentValues.containsKey(MagnetEntry.NAME)) {
            String name = contentValues.getAsString(MagnetEntry.NAME);
            if (name == null) {
                throw new IllegalArgumentException("No name value added to magnet.");
            }
        }

        if (contentValues.containsKey(MagnetEntry.PRICE)) {
            Integer price = contentValues.getAsInteger(MagnetEntry.PRICE);
            if (price != null && price < 0){
                throw new IllegalArgumentException("No valid price added to magnet.");
            }
        }

        if (contentValues.containsKey(MagnetEntry.QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(MagnetEntry.QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("No valid quantity added to magnet.");
            }
        }

        if (contentValues.containsKey(MagnetEntry.SUPPLIER_PHONE)) {
            String supplierPhone = contentValues.getAsString(MagnetEntry.SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("No supplier added to magnet.");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsAffected = database.update(
                MagnetEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
        );

        if (rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAffected;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MAGNETS:
                return MagnetEntry.CONTENT_LIST_TYPE;
            case MAGNET_ID:
                return MagnetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
