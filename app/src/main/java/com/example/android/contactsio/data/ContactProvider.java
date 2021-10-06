package com.example.android.contactsio.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactProvider extends ContentProvider {


    /** URI matcher code for the content URI for the contacts table */
    private static final int CONTACTS = 100;

    /** URI matcher code for the content URI for a single contact in the pets table */
    private static final int CONTACT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,Contract.PATH_CONTACTS,CONTACTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,Contract.PATH_CONTACTS+"/#",CONTACT_ID);
    }


    //  **Database helper object**
    private ContactDbHelper mDbHelper;


    /** Tag for the log messages */
    public static final String LOG_TAG = ContactProvider.class.getSimpleName();








    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper=new ContactDbHelper(getContext());
        return true;
    }








    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match){
            case CONTACTS:
                // For the CONTACTS code, query the CONTACTS table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the contacts table.
                cursor = database.query(Contract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONTACT_ID:
                // For the CONTACT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.contacts/contacts/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(Contract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }












    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        if (match == CONTACTS) {
            return insertContact(uri, values);
        }else{
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a contact into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertContact(Uri uri, ContentValues values) {

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        long id=db.insert(Contract.ContactEntry.TABLE_NAME,null,values);
        if(id==-1){
            Log.e(LOG_TAG,"Failed to insert row for "+uri);
            return null;
        }


        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


















    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return updateContact(uri, values, selection, selectionArgs);
            case CONTACT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateContact(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Update contacts in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more contacts).
     * Return the number of rows that were successfully updated.
     */
    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int rows=db.update(Contract.ContactEntry.TABLE_NAME,values,selection,selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }




















    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowDeleted;
        switch (match) {
            case CONTACTS:
                // Delete all rows that match the selection and selection args
                rowDeleted=db.delete(Contract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                // Delete a single row given by the ID in the URI
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted=db.delete(Contract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }



















    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return Contract.ContactEntry.CONTENT_LIST_TYPE;
            case CONTACT_ID:
                return Contract.ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
