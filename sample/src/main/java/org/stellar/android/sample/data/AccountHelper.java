package org.stellar.android.sample.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.stellar.android.sample.data.AccountContract.AccountEntry;

public class AccountHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = AccountHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "stellarAccounts.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public AccountHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " ("
                + AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
                + AccountEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, "
                + AccountEntry.COLUMN_ACCOUNT_SUBUSE + " TEXT, "
                + AccountEntry.COLUMN_PUBLIC_KEY + " TEXT NOT NULL, "
                + AccountEntry.COLUMN_SECRET_KEY + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
