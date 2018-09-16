package org.stellar.android.sample;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.stellar.android.sample.data.AccountContract.*;


public class AccountActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ACCOUNT_LOADER = 0;

    AccountCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.list_acounts);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this,
                        AccountEditorActivity.class);
                startActivity(intent);
            }
        });

        ListView accountListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        accountListView.setEmptyView(emptyView);

        mCursorAdapter = new AccountCursorAdapter(this, null);
        accountListView.setAdapter(mCursorAdapter);

        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(AccountActivity.this,
                        AccountEditorActivity.class);

                Uri currentAccountUri = ContentUris.withAppendedId(AccountEntry.CONTENT_URI, id);

                intent.setData(currentAccountUri);

                startActivity(intent);
            }
        });

        //Start Loader
        getLoaderManager().initLoader(ACCOUNT_LOADER, null, this);

    }
    private void insertAccountOne(){
        //Test Account One
        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_PUBLIC_KEY, "GDSNYH2VJZZ66VSVGIAQ6GBADMNGICLUNKDHW5EOUD5FTXQJT343MSBV");
        values.put(AccountEntry.COLUMN_SECRET_KEY, "SBP4HU7O6F2CG6EHFGH6REJJKXUG4XVY57XML4T3AOJOSZTDOD5MYU6S");
        values.put(AccountEntry.COLUMN_ACCOUNT_NAME, "TestAccountOne");
        values.put(AccountEntry.COLUMN_ACCOUNT_SUBUSE, "This is Test Account 1");

        Uri newUri = getContentResolver().insert(AccountEntry.CONTENT_URI, values);

    }

    private void insertAccountTwo(){
        //Test Account One
        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_PUBLIC_KEY, "GABOK2I32IASARL5EXNEKG4KLQWNXZEF7T4ALHEDITK5GYFPYASS3XF2");
        values.put(AccountEntry.COLUMN_SECRET_KEY, "SC3HGKLXITH3QBNELIBKBUNK2PZXHDH23JVBHEHCRICUUOAZUUZ7H3A7");
        values.put(AccountEntry.COLUMN_ACCOUNT_NAME, "TestAccountTwo");
        values.put(AccountEntry.COLUMN_ACCOUNT_SUBUSE, "This is Test Account 2");

        Uri newUri = getContentResolver().insert(AccountEntry.CONTENT_URI, values);

    }
    private void insertAccountEmpty(){
        //Test Account One
        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_PUBLIC_KEY, "GCIUCIJXARHBSZJ4HAWPBPGTX6WIJRU2BR5RIMP6S6OIS5PGIBL5M5FP");
        values.put(AccountEntry.COLUMN_SECRET_KEY, "SCTYRJ736AR7RWTTPUJ7EHS3PSS6AK6VWPO5RM77UM56KH46NLFXTWNF");
        values.put(AccountEntry.COLUMN_ACCOUNT_NAME, "TestAccountEmpty");
        values.put(AccountEntry.COLUMN_ACCOUNT_SUBUSE, "This is a empty Account ");

        Uri newUri = getContentResolver().insert(AccountEntry.CONTENT_URI, values);

    }
    /**
     * Helper method to delete all pets in the database.
     * You never Know
     */
    private void deleteAllAccounts() {
        int rowsDeleted = getContentResolver().delete(AccountEntry.CONTENT_URI, null, null);
        Log.v("AccountActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_testaccounts, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_insert_account_one:
                insertAccountOne();
                return true;
            case R.id.action_insert_account_two:
                insertAccountTwo();
                return true;
            case R.id.action_insert_account_empty:
                insertAccountEmpty();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllAccounts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                AccountEntry._ID,
                AccountEntry.COLUMN_ACCOUNT_NAME,
                AccountEntry.COLUMN_ACCOUNT_SUBUSE };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, // Parent activity context
                AccountEntry.CONTENT_URI,// Provider content URI to query
                projection,// Columns to include in the resulting Cursor
                null,// No selection clause
                null,// No selection arguments
                null);// Default sort order
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link AccountCursorAdapter} with this new cursor containing updated Account data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
