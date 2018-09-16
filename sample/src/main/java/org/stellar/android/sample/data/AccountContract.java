package org.stellar.android.sample.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AccountContract {

    private AccountContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "org.stellar.android.sample";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at account data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_ACCOUNTS = "accounts";

    /**
     * Inner class that defines constant values for the accounts database table.
     * Each entry in the table represents a single account .
     */
    public static final class AccountEntry implements BaseColumns {

        /** The content URI to access the account data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ACCOUNTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single account.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;

        /** Name of database table for accounts */
        public final static String TABLE_NAME = "accounts";


        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_ACCOUNT_NAME ="name";


        public final static String COLUMN_ACCOUNT_SUBUSE = "subuse";


        public final static String COLUMN_PUBLIC_KEY = "publickey";


        public final static String COLUMN_SECRET_KEY = "secretkey";

    }

}
