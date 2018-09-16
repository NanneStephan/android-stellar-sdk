package org.stellar.android.sample.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


import org.stellar.android.sample.data.AccountContract.*;

public class AccountProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = AccountHelper.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the accounts table
     */
    private static final int ACCOUNTS = 100;
    /**
     * URI matcher code for the content URI for a single account in the account table
     */
    private static final int ACCOUNTS_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        // The content URI of the form "content://org.stellar.android.sample/accounts" will map to the
        // integer code {@link #ACCOUNTS}. This URI is used to provide access to MULTIPLE rows
        // of the account table.
        sUriMatcher.addURI(AccountContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNTS,
                ACCOUNTS);
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://org.stellar.android.sample/accounts/3" matches, but
        // "content://org.stellar.android.sample/accounts" (without a number at the end) doesn't match.
        sUriMatcher.addURI(AccountContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNTS
                + "/#", ACCOUNTS_ID);
    }

    /**
     * Database helper object
     */
    private AccountHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AccountHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS:
                cursor = database.query(AccountEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ACCOUNTS_ID:
                selection = AccountEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(AccountEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS:
                return insertAccount(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a account into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertAccount(Uri uri, ContentValues values) {
        String publicKey = values.getAsString(AccountEntry.COLUMN_PUBLIC_KEY);
        if (publicKey == null) {
            throw new IllegalArgumentException("No public Key Found");
        }
        String secretKey = values.getAsString(AccountEntry.COLUMN_SECRET_KEY);
        if (secretKey == null) {
            throw new IllegalArgumentException("No Secret Key Found");
        }
        String accountName = values.getAsString(AccountEntry.COLUMN_ACCOUNT_NAME);
        if (accountName == null) {
            throw new IllegalArgumentException("No Account Name Found");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert net Values
        long id = database.insert(AccountEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case ACCOUNTS:
                return update(uri, contentValues, selection, selectionArgs);

            case ACCOUNTS_ID:
                selection = AccountEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAccount(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update accounts in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more accounts).
     * Return the number of rows that were successfully updated.
     */
    private int updateAccount(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(AccountEntry.COLUMN_PUBLIC_KEY)) {
            String publicKey = values.getAsString(AccountEntry.COLUMN_PUBLIC_KEY);
            if (publicKey == null) {
                throw new IllegalArgumentException("No public Key Found");
            }
        }
        if (values.containsKey(AccountEntry.COLUMN_SECRET_KEY)) {
            String secretKey = values.getAsString(AccountEntry.COLUMN_SECRET_KEY);
            if (secretKey == null) {
                throw new IllegalArgumentException("No Secret Key Found");
            }
        }
        if (values.containsKey(AccountEntry.COLUMN_ACCOUNT_NAME)) {
            String accountName = values.getAsString(AccountEntry.COLUMN_ACCOUNT_NAME);
            if (accountName == null) {
                throw new IllegalArgumentException("No Account Name Found");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(AccountEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS:
                rowsDeleted = database.delete(AccountEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNTS_ID:
                selection = AccountEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AccountEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS:
                return AccountEntry.CONTENT_LIST_TYPE;
            case ACCOUNTS_ID:
                return AccountEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
