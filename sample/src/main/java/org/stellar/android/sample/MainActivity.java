package org.stellar.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Stellar";
    public static KeyPair pair;
    public static Server server;

    public static String secretKey = "SAO6SPWEBUHXKMDOPZ4JZTETXC4H4ZTLJYWXQCGR6WYN2DCDR26LTK3I";
    public static String publicKey = "GAZQJ4VPNWBUMAD7MZM3XPO5QUJEEKIJZMXKXNUIX7NBLZVTR5SCUKGI";

    public static AccountResponse account = null;

    public static PaymentsRequestBuilder paymentsRequest;

    public static String lastToken;

    String strBalance;

    TextView successTextView;
    TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        successTextView = findViewById(R.id.Success);
        balanceTextView = findViewById(R.id.Balance);

        Network.useTestNetwork();

        server = new Server("https://horizon-testnet.stellar.org");

        //pair keys
        pair = KeyPair.fromSecretSeed(secretKey);

        successTextView.setText("Balances for account " + pair.getAccountId());
        balanceTextView.setText(strBalance);
        // getting StellarAsync
        new StellarAsync().execute();
        // Create an API call to query payments involving the account.
        paymentsRequest = server.payments().forAccount(account);

    }
}