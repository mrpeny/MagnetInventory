package com.example.mrpeny.magnetinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API contract for the Magnet Inventory app.
 */

public final class MagnetContract {

    final static String CONTENT_AUTHORITY = "com.example.mrpeny.magnetinventory";

    final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_MAGNETS = "magnets";

    // Empty constructor in order to prevent someone to instantiate this class
    private MagnetContract() {}

    /**
     * Inner class that defines constant values for magnets database table.
     * Each entry in the table represents a single magnet.
     */
    public static final class MagnetEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MAGNETS);

        /**
         * MIME type for a list pf magnets .
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAGNETS;

        /**
         * MIME type for a single magnet row.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_MAGNETS + "/" + PATH_MAGNETS;

        public static final String TABLE_NAME = "magnets";

        /**
         * Unique ID number for a magnet
         *
         * TYPE: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Product name of a magnet
         *
         * TYPE: INTEGER
         */
        public final static String NAME = "name";

        /**
         * The price of each magnet
         *
         * TYPE: INTEGER
         */
        public final static String PRICE = "price";

        /**
         * The quantity of each magnet
         *
         * TYPE: INTEGER
         */
        public final static String QUANTITY = "quantity";

        /**
         * The phone number of the supplier of the current magnet
         *
         * TYPE: INTEGER
         */
        public final static String SUPPLIER_PHONE = "supplier_phone";

        /**
         * The picture of each magnet taken by the user
         *
         * TYPE: BLOB
         */
        public final static String IMAGE = "image";
    }
}
