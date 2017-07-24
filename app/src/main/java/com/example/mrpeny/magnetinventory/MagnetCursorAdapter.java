package com.example.mrpeny.magnetinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

/**
 * CursorAdapter for magnets that populates the list view from cursor
 */

public class MagnetCursorAdapter extends CursorAdapter {

    public MagnetCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        /*
        * Store references to the views of the list item.
        */
        TextView nameTextView = (TextView) view.findViewById(R.id.name_edit_text);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        /*
        * Retrieve column indexes.
        */
        int idColumnIndex = cursor.getColumnIndex(MagnetEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(MagnetEntry.NAME);
        int priceColumnIndex = cursor.getColumnIndex(MagnetEntry.PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(MagnetEntry.QUANTITY);

        /*
        * Extract values from cursor.
        */
        final int id = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        /*
        * Update the views with the appropriate values.
        */
        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        /*
        * Set onClickListener on the list item so that it starts the DetailActivity with
        * the corresponding Uri in its Data field
        */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(MagnetEntry.CONTENT_URI, id));
                context.startActivity(intent);
            }
        });

        /*
        * Set Sale button's onClickListener so that it increments the quantity by one and updates
        * the database.
        */
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                // Check current quantity. If less than 1, no need the decrement
                if (quantity < 1) {
                    return;
                }
                // Decrement quantity by one
                int newQuantity = quantity - 1;
                // Prepare content values to store in the database
                ContentValues contentValues = new ContentValues();
                contentValues.put(MagnetEntry.QUANTITY, newQuantity);
                Uri magnetUri = ContentUris.withAppendedId(MagnetEntry.CONTENT_URI, id);
                context.getContentResolver().update(magnetUri, contentValues, null, null);
            }
        });
    }
}
