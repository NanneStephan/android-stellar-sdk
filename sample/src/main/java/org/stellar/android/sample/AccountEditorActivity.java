package org.stellar.android.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.stellar.android.sample.data.AccountContract.*;


public class AccountEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the account data loader
     */
    private static final int EXISING_ACCOUNT_LOADER = 0;

    private Uri mCurrentAccountUri;

    private EditText mPrivateKeyEditText;

    private EditText mSecretKeyEditText;

    private EditText mNameAccountEditText;

    private EditText mSubUseEditText;


    private boolean mAccountHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mAccountHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.addkeyaccount);
        // Examine the intent that was used to launch this activity

        Intent intent = getIntent();
        mCurrentAccountUri = intent.getData();
        // If the intent DOES NOT contain a account content URI, then we know that we are
        // creating a new account.
        if (mCurrentAccountUri == null) {
            setTitle("Add Account");

            invalidateOptionsMenu();
        } else {
            setTitle("Edit Account");

            getLoaderManager().initLoader(EXISING_ACCOUNT_LOADER, null, this);
        }

        mNameAccountEditText = findViewById(R.id.accountName);
        mSubUseEditText = findViewById(R.id.useCase);
        mPrivateKeyEditText = findViewById(R.id.PrivateKey);
        mSecretKeyEditText = findViewById(R.id.secretKey);

        mNameAccountEditText.setOnTouchListener(mTouchListener);
        mSubUseEditText.setOnTouchListener(mTouchListener);
        mSecretKeyEditText.setOnTouchListener(mTouchListener);
        mPrivateKeyEditText.setOnTouchListener(mTouchListener);

    }

    private void saveAccount() {

        if (mPrivateKeyEditText == null) {
            Toast.makeText(this, "No PrivateKey Generate One", Toast.LENGTH_SHORT).show();
        }
        String privetKeyString = mPrivateKeyEditText.getText().toString().trim();

        if (mSecretKeyEditText == null) {
            Toast.makeText(this, "No SecretKey Generate One", Toast.LENGTH_SHORT).show();
        }
        String secretKeyString = mSecretKeyEditText.getText().toString().trim();

        if (mNameAccountEditText == null) {
            Toast.makeText(this, "No account name", Toast.LENGTH_SHORT).show();
        }
        String nameAccountString = mNameAccountEditText.getText().toString().trim();

        String subUseString = mSubUseEditText.getText().toString().trim();

        if (mCurrentAccountUri == null &&
                TextUtils.isEmpty(privetKeyString) && TextUtils.isEmpty(secretKeyString) &&
                TextUtils.isEmpty(nameAccountString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_PUBLIC_KEY, privetKeyString);
        values.put(AccountEntry.COLUMN_SECRET_KEY, secretKeyString);
        values.put(AccountEntry.COLUMN_ACCOUNT_NAME, nameAccountString);
        values.put(AccountEntry.COLUMN_ACCOUNT_SUBUSE, subUseString);


        if (mCurrentAccountUri == null) {
            Uri newUri = getContentResolver().insert(AccountEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error saving Account",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(mCurrentAccountUri, values, null, null);

            if (rowAffected == 0) {
                Toast.makeText(this, "Error Updating Account",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account Updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentAccountUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveAccount();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mAccountHasChanged) {
                    NavUtils.navigateUpFromSameTask(AccountEditorActivity.this);
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AccountEditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mAccountHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                AccountEntry._ID,
                AccountEntry.COLUMN_PUBLIC_KEY,
                AccountEntry.COLUMN_SECRET_KEY,
                AccountEntry.COLUMN_ACCOUNT_NAME,
                AccountEntry.COLUMN_ACCOUNT_SUBUSE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, // Parent activity context
                mCurrentAccountUri,// Provider content URI to query
                projection,// Columns to include in the resulting Cursor
                null,// No selection clause
                null,// No selection arguments
                null);// Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int publicColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_PUBLIC_KEY);
            int secretColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_SECRET_KEY);
            int nameColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_ACCOUNT_NAME);
            int useCaseColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_ACCOUNT_SUBUSE);

            String publicKey = cursor.getString(publicColumnIndex);
            String secretKey = cursor.getString(secretColumnIndex);
            String nameAccount = cursor.getString(nameColumnIndex);
            String useCase = cursor.getString(useCaseColumnIndex);

            mPrivateKeyEditText.setText(publicKey);
            mSecretKeyEditText.setText(secretKey);
            mNameAccountEditText.setText(nameAccount);
            mSubUseEditText.setText(useCase);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPrivateKeyEditText.setText("");
        mSecretKeyEditText.setText("");
        mNameAccountEditText.setText("");
        mSubUseEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quet editing?");
        builder.setPositiveButton("discard", discardButtonClickListener);
        builder.setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this account.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the account in the database.
     */
    private void deleteAccount() {
        if (mCurrentAccountUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentAccountUri, null,
                    null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Faild to delete account",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Deleted account",
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}
