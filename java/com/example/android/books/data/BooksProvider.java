package com.example.android.books.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.books.R;
import com.example.android.books.data.BooksContract.BooksEntry;

/**
 * Created by Gabriela on 8/6/2018.
 */

public class BooksProvider extends ContentProvider {

        public static final String LOG_TAG = BooksProvider.class.getSimpleName();

        private BooksDbHelper mDbHelper;

        private static final int BOOKS = 100;
        private static final int BOOK_ID = 101;

        private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        static {
            sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);
            sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOK_ID);
                    }

        @Override
        public boolean onCreate() {
            mDbHelper = new BooksDbHelper(getContext());
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            Cursor cursor;

            int match = sUriMatcher.match(uri);
            switch (match){
                case BOOKS:
                    cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case BOOK_ID:
                    selection = BooksEntry._ID + "=?";
                    selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                    cursor = database.query(BooksContract.BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query your URI" + uri);
            }
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
        }

        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return insertBook(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

    private Uri insertBook (Uri uri, ContentValues values) {

        String title = values.getAsString(BooksEntry.COLUMN_BOOK_TITLE);
        if (title == null){
            throw  new IllegalArgumentException("Title cannot be empty");
        }

        Integer quantity = values.getAsInteger(BooksEntry.COLUMN_BOOK_QUANTITY);
        if (quantity < 0 ){
            throw new IllegalArgumentException("There can not be negative quantity values");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BooksEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert data" +uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

        @Override
        public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
           final int match = sUriMatcher.match(uri);
           switch (match){
               case BOOKS:
                   return updateBook(uri, contentValues, selection, selectionArgs);
               case BOOK_ID:
                   selection = BooksEntry._ID + "=?";
                   selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                   return updateBook(uri, contentValues, selection, selectionArgs);
                   default:
                       throw new IllegalArgumentException("Update is not supported for " + uri);
           }
        }

        private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs){
            if (values.containsKey(BooksEntry.COLUMN_BOOK_TITLE)){
                String title = values.getAsString(BooksEntry.COLUMN_BOOK_TITLE);
                if (title == null){
                    throw  new IllegalArgumentException("Title cannot be empty");
                }
            }
            if (values.containsKey(BooksEntry.COLUMN_BOOK_QUANTITY)){
                Integer quantity = values.getAsInteger(BooksEntry.COLUMN_BOOK_QUANTITY);
                if (quantity < 0 ){
                    throw new IllegalArgumentException("There can not be negative quantity values");
                }
            }
            if (values.size() ==0){
                return 0;
            }
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            int rowsUpdated = database.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);
            if (rowsUpdated !=0){
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {

            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            int rowsDeleted;
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case BOOK_ID:
                    selection = BooksEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Deletion is not supported");
            }
            if ( rowsDeleted != 0){
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        }

        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return BooksEntry.CONTENT_LIST_TYPE;
                case BOOK_ID:
                    return BooksEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }}
