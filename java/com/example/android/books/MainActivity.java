package com.example.android.books;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.Toast;

import com.example.android.books.data.BooksContract.BooksEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        ListView bookListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_title_text);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }
            @Override
            protected void onStart() {
                super.onStart();
            }

            private void insertBook() {

                ContentValues values = new ContentValues();
                values.put(BooksEntry.COLUMN_BOOK_TITLE, "Lean In");
                values.put(BooksEntry.COLUMN_BOOK_PRICE, 39);
                values.put(BooksEntry.COLUMN_BOOK_QUANTITY, 5);
                values.put(BooksEntry.COLUMN_SUPPLIER, "Smith");
                values.put(BooksEntry.COLUMN_SUPPLIER_PHONE, "1234");

                getContentResolver().insert(BooksEntry.CONTENT_URI, values);

            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu options from the res/menu/menu_catalog.xml file.
                // This adds menu items to the app bar.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // User clicked on a menu option in the app bar overflow menu
                switch (item.getItemId()) {
                    // Respond to a click on the "Insert dummy data" menu option
                    case R.id.action_insert_default_data:
                        insertBook();
                        return true;
                    case R.id.action_delete_all_data:
                        deleteAllData();
                        return true;
                }
                return super.onOptionsItemSelected(item);
            }

            private void deleteAllData() {
                int rowsDeleted = getContentResolver().delete(BooksEntry.CONTENT_URI, null, null);
                if (rowsDeleted == 0) {
                    Toast.makeText(this, R.string.delete_books_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.delete_books_successful, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                String[] projection = {
                        BooksEntry._ID,
                        BooksEntry.COLUMN_BOOK_TITLE,
                        BooksEntry.COLUMN_BOOK_PRICE,
                        BooksEntry.COLUMN_BOOK_QUANTITY};
                return new CursorLoader(this,
                        BooksEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mCursorAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mCursorAdapter.swapCursor(null);
            }
        }

