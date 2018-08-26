package com.example.android.books;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.books.data.BooksContract.BooksEntry;

import org.w3c.dom.Text;

/**
 * Created by Gabriela on 8/7/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    private final Context mContext;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView titelTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        int titleColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_QUANTITY);

        String title = cursor.getString(titleColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        String quantityString = Integer.toString(quantity);

        titelTextView.setText(title);
        priceTextView.setText(price);
        quantityTextView.setText(quantityString);

        final Button saleButton = view.findViewById(R.id.saleButton);
        int columnIndex = cursor.getColumnIndex(BooksEntry._ID);
        int id = cursor.getInt(columnIndex);
        final Uri currentBookUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int decreasedQuantity = quantity - 1;
                if (decreasedQuantity < 1) {
                    decreasedQuantity=0;}
                ContentValues values = new ContentValues();
                values.put(BooksEntry.COLUMN_BOOK_QUANTITY, decreasedQuantity);
                context.getContentResolver().update(currentBookUri, values, null, null);
            }});
    }
    }


