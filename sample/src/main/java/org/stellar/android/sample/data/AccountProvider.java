package org.stellar.android.sample.data;

import android.content.ContentProvider;

public class AccountProvider extends ContentProvider {



    private AccountHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AccountHelper(getContext());
        return true;
    }

}
