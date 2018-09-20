package org.stellar.android.sample;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.stellar.android.sample.data.AccountContract.*;

/**
 * {@link AccountCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */


public class AccountCursorAdapter extends CursorAdapter {
    private Context mContext;

    public AccountCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item_accounts,
                parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){

        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_ACCOUNT_NAME);
        int subUseColumnIndex = cursor.getColumnIndex(AccountEntry.COLUMN_ACCOUNT_SUBUSE);

        String AccountName = cursor.getString(nameColumnIndex);
        String AccountSubUse= cursor.getString(subUseColumnIndex);

        if(TextUtils.isEmpty(AccountSubUse)){
            AccountSubUse = context.getString(R.string.no_subuse_text);

        }
        nameTextView.setText(AccountName);
        summaryTextView.setText(AccountSubUse);

    }
}
