package org.stellar.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.AccountResponse;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Stellar";
    public static KeyPair pair;
    public static AccountResponse.Balance balance;
    public static String secretKey = "SAO6SPWEBUHXKMDOPZ4JZTETXC4H4ZTLJYWXQCGR6WYN2DCDR26LTK3I";
    public static String publicKey = "GAZQJ4VPNWBUMAD7MZM3XPO5QUJEEKIJZMXKXNUIX7NBLZVTR5SCUKGI";

    String strBalance;

    TextView successTextView;
    TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        successTextView = findViewById(R.id.Success);
        balanceTextView = findViewById(R.id.Balance);

        pair = KeyPair.fromSecretSeed(secretKey);
        successTextView.setText("Balances for account " + pair.getAccountId());
        balanceTextView.setText(strBalance);

        new StellarAsync().execute();


    }
}
