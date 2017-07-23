package com.example.mrpeny.magnetinventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

public class DetailActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Delete if unused
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int MAGNET_DETAIL_LOADER = 0;

    // Stores the Uri of the current product. If it is null then the Activity is in "New magnet"
    // mode, because the
    private Uri mCurrentMagnetUri;

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierPhoneEditText;

    // Variable indicating whether or not the data of Magnet has been modified.
    // This variable is used for pre-condition for showing confirmation dialogs before exiting the
    // activity.
    private boolean mMagnetModified = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mMagnetModified = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Intent intent = getIntent();
        // Save the Uri sent by the starting intent. If null then the Activity is in "New magnet"
        // mode inserting a new magnet  instead of "Edit magnet" mode updating an existing magnet.
        mCurrentMagnetUri = intent.getData();

        if (mCurrentMagnetUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_magnet));
            // Options Menu has changed and should be recreated
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_magnet));
            getLoaderManager().initLoader(MAGNET_DETAIL_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit_text);

        nameEditText.setOnTouchListener(mOnTouchListener);
        priceEditText.setOnTouchListener(mOnTouchListener);
        quantityEditText.setOnTouchListener(mOnTouchListener);
        supplierPhoneEditText.setOnTouchListener(mOnTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentMagnetUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_save:
                // Check whether saving was successful or not.
                // If yes, then finish current Activity, if no, then stay in the current Activity
                if (saveMagnet()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                int rowsAffected = getContentResolver().delete(mCurrentMagnetUri, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_not_deleted),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_deleted),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveMagnet() {
        boolean magnetSaved = true;

        if (isValidMagnet()) {
            ContentValues contentValues = getContentValues();
            // Check whether it's a new Magnet to insert or an existing Magnet to update
            if (mCurrentMagnetUri == null) {
                Uri newUri = getContentResolver().insert(MagnetEntry.CONTENT_URI, contentValues);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_not_saved),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_saved),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsUpdated = getContentResolver().update(mCurrentMagnetUri, contentValues, null, null);
                if (rowsUpdated == 0) {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_update_failed),
                            Toast.LENGTH_SHORT).show();
                    magnetSaved = false;
                } else {
                    Toast.makeText(this, getString(R.string.toast_message_magnet_updated),
                            Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(this, getString(R.string.toast_message_fill_in_all_fields),
                    Toast.LENGTH_SHORT).show();
            magnetSaved = false;
        }
        return magnetSaved;
    }

    private boolean isValidMagnet() {
        boolean isValidMagnet = true;

        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(priceEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(quantityEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(supplierPhoneEditText.getText().toString().trim())) {
            isValidMagnet = false;
        }
        return isValidMagnet;
    }

    private ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        String name = nameEditText.getText().toString().trim();
        contentValues.put(MagnetEntry.NAME, name);

        String price = priceEditText.getText().toString().trim();
        contentValues.put(MagnetEntry.PRICE, price);

        String quantity = quantityEditText.getText().toString().trim();
        contentValues.put(MagnetEntry.QUANTITY, quantity);

        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        contentValues.put(MagnetEntry.SUPPLIER_PHONE, supplierPhone);

        return contentValues;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MagnetEntry.NAME,
                MagnetEntry.PRICE,
                MagnetEntry.QUANTITY,
                MagnetEntry.SUPPLIER_PHONE
        };

        return new CursorLoader(this, mCurrentMagnetUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(MagnetEntry.NAME);
            int priceColumnIndex = cursor.getColumnIndex(MagnetEntry.PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(MagnetEntry.QUANTITY);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(MagnetEntry.SUPPLIER_PHONE);

            nameEditText.setText(cursor.getString(nameColumnIndex));
            priceEditText.setText(cursor.getString(priceColumnIndex));
            quantityEditText.setText(cursor.getString(quantityColumnIndex));
            supplierPhoneEditText.setText(cursor.getString(supplierPhoneColumnIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
