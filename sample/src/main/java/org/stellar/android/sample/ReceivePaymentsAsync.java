package org.stellar.android.sample;

import android.os.AsyncTask;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;


public class ReceivePaymentsAsync extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {

        Server server = new Server("https://horizon-testnet.stellar.org");
        final KeyPair account = KeyPair.fromAccountId("GC2BKLYOOYPDEFJKLKY6FNNRQMGFLVHJKQRGNSSRRGSMPGF32LHCQVGF");

        // Create an API call to query payments involving the account.
        PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account);

        // If some payments have already been handled, start the results from the
        // last seen payment. (See below in `handlePayment` where it gets saved.)
        String lastToken = loadLastPagingToken();
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
        });
        return null;
    }

    private void savePagingToken(String pagingToken) {
        pagingToken = "";
    }

    private String loadLastPagingToken() {
        return null;
    }
}
