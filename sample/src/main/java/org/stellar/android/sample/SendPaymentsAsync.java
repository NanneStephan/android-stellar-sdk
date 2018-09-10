package org.stellar.android.sample;

import android.os.AsyncTask;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;

public class SendPaymentsAsync extends AsyncTask {


    Server server = new Server("https://horizon-testnet.stellar.org");

    KeyPair source = KeyPair.fromSecretSeed("SCZANGBA5YHTNYVVV4C3U252E2B6P6F5T3U6MM63WBSBZATAQI3EBTQ4");
    KeyPair destination = KeyPair.fromAccountId("GA2C5RFPE6GCKMY3US5PAB6UZLKIGSPIUKSLRB6Q723BM2OARMDUYEJ5");
    @Override
    protected Object doInBackground(Object[] objects) {
        Network.useTestNetwork();

        // First, check to make sure that the destination account exists.
        // You could skip this, but if the account does not exist, you will be charged
        // the transaction fee when the transaction fails.
        // It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start building the transaction.
        Transaction transaction = new Transaction.Builder(sourceAccount)
                .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "10").build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text("Test Transaction"))
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);

        // And finally, send it off to Stellar!
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            System.out.println("Success!");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
            // If the result is unknown (no response body, timeout etc.) we simply resubmit
            // already built transaction:
            // SubmitTransactionResponse response = server.submitTransaction(transaction);
        }
        return null;
    }
}
