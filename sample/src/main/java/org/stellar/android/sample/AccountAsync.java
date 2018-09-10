package org.stellar.android.sample;

import android.os.AsyncTask;


import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.*;
import java.util.*;



import java.io.IOException;
import java.net.URL;

import static org.stellar.android.sample.MainActivity.*;

public class AccountAsync extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] objects) {

        String friendbotUrl = String.format(
                "https://friendbot.stellar.org/?addr=%s",
                pair.getAccountId());
        InputStream response = null;
        try {
            response = new URL(friendbotUrl).openStream();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAIL You have NO new account :)\n");
        }
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        System.out.println("SUCCESS! You have a new account :)\n" + body);

        Server server = new Server("https://horizon-testnet.stellar.org");
        AccountResponse account = null;
        try {
            account = server.accounts().account(pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Balances for account " + pair.getAccountId());
        for (AccountResponse.Balance balance : account.getBalances()) {
            System.out.println(String.format(
                    "Type: %s, Code: %s, Balance: %s",
                    balance.getAssetType(),
                    balance.getAssetCode(),
                    balance.getBalance()));
        }
        return null;
    }

}
//https://www.stellar.org/developers/guides/get-started/transactions.html

