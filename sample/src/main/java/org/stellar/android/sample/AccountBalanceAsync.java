package org.stellar.android.sample;

import android.os.AsyncTask;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import static org.stellar.android.sample.AccountEditorActivity.*;


public class AccountBalanceAsync extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {

        Server server = new Server("https://horizon-testnet.stellar.org");
        final KeyPair pair = KeyPair.fromAccountId(privateKey);
        AccountResponse account = null;
        try {
            account = server.accounts().account(pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (account == null) {
            showBalanceString = "no Balance";
        } else {
            System.out.println("Balances for account " + privateKey);
            for (AccountResponse.Balance balance : account.getBalances()) {
                showBalanceString = (balance.getBalance());
            }
            return null;
        }
        return null;
    }
}
