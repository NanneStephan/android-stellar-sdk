package org.stellar.android.sample;

import android.os.AsyncTask;
import android.util.Log;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.HttpResponseException;

import java.io.IOException;
import java.net.URL;

import static org.stellar.android.sample.MainActivity.*;

public class StellarAsync extends AsyncTask<URL, String, String> {
    @Override
    protected String doInBackground(URL... urls) {
        KeyPair pair = KeyPair.fromSecretSeed(secretKey);

        Server server = new Server("https://horizon-testnet.stellar.org");

        AccountResponse account = null;
        try {
            account = server.accounts().account(pair);
            Log.i(TAG, "Balances for account " + pair.getAccountId());
            for (AccountResponse.Balance balance : account.getBalances()) {
                Log.i(TAG, String.format(
                        "Type: %s, Code: %s, Balance: %s",
                        balance.getAssetType(),
                        balance.getAssetCode(),
                        balance.getBalance()));
            }
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
