package org.stellar.android.sample;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.stellar.android.sample.data.AccountContract.*;

import static org.stellar.android.sample.AccountEditorActivity.*;
import static org.stellar.android.sample.AccountActivity.*;

public class SendPaymentActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentAccountUri;

    Button send;
    Button getReceiverAccount;

    EditText receiverAccount;

    TextView privateKeySend;
    TextView accountNameSend;
    TextView balanceTextView;
    TextView successTextView;

    private SlidingUpPanelLayout slidingLayout;
    private Button btnShow;
    private Button btnHide;
    private TextView textView;


    public static String balanceString;
    public static String privateKeyBalance;
    public static String secretKeySend;
    public static String getPrivateKeyBalance;
    public static String accountNameString;



    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.payments);
        //Buttons
        send = findViewById(R.id.sendButton);
        //EditText
        receiverAccount = findViewById(R.id.receiver);
        //TextView
        privateKeySend = findViewById(R.id.PrivateKeySender);
        accountNameSend = findViewById(R.id.accountName);
        balanceTextView = findViewById(R.id.showBalance);
        successTextView = findViewById(R.id.fundsSuccess);
        Intent intent = getIntent();
        mCurrentAccountUri = intent.getData();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnShow = (Button)findViewById(R.id.addReceiver);
        btnHide = (Button)findViewById(R.id.btn_hide);
        textView = (TextView)findViewById(R.id.text);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //some "demo" event
        slidingLayout.setPanelSlideListener(onSlideListener());
        btnHide.setOnClickListener(onHideListener());
        btnShow.setOnClickListener(onShowListener());

        ListView accountListView = findViewById(R.id.listSlide);
        accountListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(EXISTING_ACCOUNT_LOADER, null, this);


    }
    /**
     * Request show sliding layout when clicked
     * @return
     */
    private View.OnClickListener onShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show sliding layout in bottom of screen (not expand it)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                btnShow.setVisibility(View.GONE);
            }
        };
    }
    /**
     * Hide sliding layout when click button
     * @return
     */
    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide sliding layout
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                btnShow.setVisibility(View.VISIBLE);
            }
        };
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                textView.setText("panel is sliding");
            }

            @Override
            public void onPanelCollapsed(View view) {
                textView.setText("panel Collapse");
            }

            @Override
            public void onPanelExpanded(View view) {
                textView.setText("panel expand");
            }

            @Override
            public void onPanelAnchored(View view) {
                textView.setText("panel anchored");
            }

            @Override
            public void onPanelHidden(View view) {
                textView.setText("panel is Hidden");
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                AccountEntry._ID,
                AccountEntry.COLUMN_PUBLIC_KEY,
                AccountEntry.COLUMN_SECRET_KEY,
                AccountEntry.COLUMN_ACCOUNT_NAME};
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

            privateKeyBalance = cursor.getString(publicColumnIndex);
            String publicKey = cursor.getString(publicColumnIndex);
            secretKeySend = cursor.getString(secretColumnIndex);
            String nameAccount = cursor.getString(nameColumnIndex);

            privateKeySend.setText(publicKey);
            accountNameSend.setText(nameAccount);

            new AccountBalanceAsync().execute();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    balanceTextView.setText(showBalanceString);
                }
            }, 2000);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        privateKeySend.setText("");
        accountNameSend.setText("");
        privateKeyBalance = "";
        secretKeySend = "";
        balanceString = "";
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
