package org.stellar.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Stellar";
    public static KeyPair pair;
    public static Server server;

    public static String secretKey = "SAO6SPWEBUHXKMDOPZ4JZTETXC4H4ZTLJYWXQCGR6WYN2DCDR26LTK3I";

    String strBalance;

    TextView successTextView;
    TextView balanceTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = new Server("https://horizon-testnet.stellar.org");

        //pair keys
        pair = KeyPair.fromSecretSeed(secretKey);

        successTextView.setText("Balances for account " + pair.getAccountId());
        balanceTextView.setText(strBalance);
        // getting StellarAsync
        new AccountAsync().execute();
        new SendPaymentsAsync().execute();
        new ReceivePaymentsAsync().execute();

        }
}