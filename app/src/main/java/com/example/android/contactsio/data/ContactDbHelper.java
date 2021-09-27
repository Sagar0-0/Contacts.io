package com.example.android.contactsio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "mycontacts.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * Constructs a new instance of {@link ContactDbHelper}.
     *
     * @param context of the app
     */
    public ContactDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_CONTACTS_TABLE =  "CREATE TABLE " + Contract.ContactEntry.TABLE_NAME + " ("
                + Contract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.ContactEntry.COLUMN_CONTACT_NAME + " TEXT NOT NULL, "
                + Contract.ContactEntry.COLUMN_CONTACT_NUMBER + " TEXT NOT NULL, "
                + Contract.ContactEntry.COLUMN_CONTACT_TASK + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }


    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
