package com.example.mrpeny.magnetinventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrpeny.magnetinventory.data.MagnetContract.MagnetEntry;

import static android.R.attr.name;

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
        TextView nameTextView = (TextView) view.findViewById(R.id.name_edit_text);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);

        int idColumnIndex = cursor.getColumnIndex(MagnetEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(MagnetEntry.NAME);
        int priceColumnIndex = cursor.getColumnIndex(MagnetEntry.PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(MagnetEntry.QUANTITY);

        final int id = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(MagnetEntry.CONTENT_URI, id));
                context.startActivity(intent);
            }
        });
    }
}
