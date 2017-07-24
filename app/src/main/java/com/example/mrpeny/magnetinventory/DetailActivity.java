package com.example.mrpeny.magnetinventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

import java.io.ByteArrayOutputStream;

public class DetailActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Delete if unused
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int MAGNET_DETAIL_LOADER = 0;
    // Request code for image capture intent. It is used for checking whether returning intent
    // holds the requested result we need in onActivityResult method.
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // Stores the Uri of the current product. If it is null then the Activity is in "New magnet"
    // mode, because the
    private Uri mCurrentMagnetUri;

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierPhoneEditText;
    private ImageView magnetImageView;

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
        // TODO: Create scroll view, Add TextInputLayout
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
        magnetImageView = (ImageView) findViewById(R.id.magnet_image_view);

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
    public void onBackPressed() {
        if (!mMagnetModified) {
            super.onBackPressed();
        }

        showDeleteConfirmationDialog();
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
                // Show confirmation dialog in order to prevent accidental removal of Magnet.
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_take_photo:
                takePhoto();
                return true;
            case android.R.id.home:
                if (!mMagnetModified) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                showUnsavedChangesDialog();
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

    /*
    * Makes validation whether all required fields were filled in and photo has been taken.
    */
    private boolean isValidMagnet() {
        boolean isValidMagnet = true;

        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(priceEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(quantityEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(supplierPhoneEditText.getText().toString().trim())) {
            isValidMagnet = false;
        }

        if (magnetImageView.getDrawable() == null) {
            isValidMagnet = false;
        }
        return isValidMagnet;
    }

    /*
    * Extracts the EditText fields and taken photo from the UI.
    */
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

        /*
        * Retrieve bitmap from ImageView user has taken, compress and convert it into a byte array.
        */
        BitmapDrawable magnetBitmapDrawable = (BitmapDrawable) magnetImageView.getDrawable();
        Bitmap bitmapMagnetImage = magnetBitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapMagnetImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        contentValues.put(MagnetEntry.IMAGE, image);

        return contentValues;
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_without_saving);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_magnet);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMagnet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMagnet() {
        int rowsAffected = getContentResolver().delete(mCurrentMagnetUri, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.toast_message_magnet_not_deleted),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_message_magnet_deleted),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void takePhoto() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Check whether there is a component that can receive the Capture Image Intent
        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(captureImageIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            magnetImageView.setImageBitmap(imageBitmap);
            // Indicate that magnet has been modified since last state
            mMagnetModified = true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MagnetEntry._ID,
                MagnetEntry.NAME,
                MagnetEntry.PRICE,
                MagnetEntry.QUANTITY,
                MagnetEntry.SUPPLIER_PHONE,
                MagnetEntry.IMAGE
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
            int imageColumnIndex = cursor.getColumnIndex(MagnetEntry.IMAGE);

            nameEditText.setText(cursor.getString(nameColumnIndex));
            priceEditText.setText(cursor.getString(priceColumnIndex));
            quantityEditText.setText(cursor.getString(quantityColumnIndex));
            supplierPhoneEditText.setText(cursor.getString(supplierPhoneColumnIndex));

            byte[] imageBytes = cursor.getBlob(imageColumnIndex);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            magnetImageView.setImageBitmap(bitmapImage);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierPhoneEditText.setText("");
        magnetImageView.setImageBitmap(null);
    }

    /*
    * Decreases the quantity by one
    */
    public void decreaseQuantity(View view) {
        int quantity;
        String quantityString = quantityEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
            // Logic that prevents decrease button to go into negative range.
            if (quantity > 0) {
                quantity = quantity - 1;
                quantityEditText.setText(String.valueOf(quantity));
                mMagnetModified = true;
            }
        }
    }

    /*
    * Increases the quantity by one
    */
    public void increaseQuantity(View view) {
        int quantity;
        String quantityString = quantityEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        } else {
            quantity = 0;
        }
        quantity = quantity + 1;
        quantityEditText.setText(String.valueOf(quantity));
        mMagnetModified = true;
    }

    public void callSupplier(View view) {
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.add_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check whether there is a component that can receive the Dial Intent
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            callIntent.setData(Uri.parse("tel:" + supplierPhone));
            startActivity(callIntent);
        }
    }
}
