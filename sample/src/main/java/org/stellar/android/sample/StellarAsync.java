package org.stellar.android.sample;

import android.os.AsyncTask;
import android.util.Log;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.HttpResponseException;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import java.io.IOException;
import java.net.URL;

import static org.stellar.android.sample.MainActivity.*;

public class StellarAsync extends AsyncTask<URL, String, String> {

    public static KeyPair source = KeyPair.fromSecretSeed("SCZANGBA5YHTNYVVV4C3U252E2B6P6F5T3U6MM63WBSBZATAQI3EBTQ4");
    public static KeyPair destination = KeyPair.fromAccountId("GA2C5RFPE6GCKMY3US5PAB6UZLKIGSPIUKSLRB6Q723BM2OARMDUYEJ5");

    static AccountResponse sourceAccount;
    static Transaction transaction;
    static SubmitTransactionResponse response;


    @Override
    protected String doInBackground(URL... urls) {
        KeyPair pair = KeyPair.fromSecretSeed(secretKey);

        Server server = new Server("https://horizon-testnet.stellar.org");

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

        try {
            server.accounts().account(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If there was no error, load up-to-date information on your account.
        sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source);
            // Start building the transaction.
            transaction = new Transaction.Builder(sourceAccount)
                    .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "10").build())
                    // A memo allows you to add your own metadata to a transaction. It's
                    // optional and does not affect how Stellar treats the transaction.
                    .addMemo(Memo.text("Test Transaction"))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

                // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        // And finally, send it off to Stellar!

        try {
            response = server.submitTransaction(transaction);
            System.out.println("Success!");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
            // If the result is unknown (no response body, timeout etc.) we simply resubmit
            // already built transaction:
            // SubmitTransactionResponse response = server.submitTransaction(transaction);
        }
        if (lastToken != null) {
            paymentsRequest.cursor(lastToken);
        }
        // `stream` will send each recorded payment, one by one, then keep the
        // connection open and continue to send you new payments as they occur.
        paymentsRequest.stream(new EventListener<OperationResponse>() {
            @Override
            public void onEvent(OperationResponse payment) {
                // Record the paging token so we can start from here next time.
                savePagingToken(payment.getPagingToken());

                // The payments stream includes both sent and received payments. We only
                // want to process received payments here.
                if (payment instanceof PaymentOperationResponse) {
                    if (((PaymentOperationResponse) payment).getTo().equals(account)) {
                        return;
                    }

                    String amount = ((PaymentOperationResponse) payment).getAmount();

                    Asset asset = ((PaymentOperationResponse) payment).getAsset();
                    String assetName;
                    if (asset.equals(new AssetTypeNative())) {
                        assetName = "lumens";
                    } else {
                        StringBuilder assetNameBuilder = new StringBuilder();
                        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
                        assetNameBuilder.append(":");
                        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId());
                        assetName = assetNameBuilder.toString();
                    }

                    StringBuilder output = new StringBuilder();
                    output.append(amount);
                    output.append(" ");
                    output.append(assetName);
                    output.append(" from ");
                    output.append(((PaymentOperationResponse) payment).getFrom().getAccountId());
                    System.out.println(output.toString());
                }

            }

            private void savePagingToken(String pagingToken) {
            }
        });
    }
    //https://www.stellar.org/developers/guides/get-started/transactions.html
}
