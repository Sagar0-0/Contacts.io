package com.example.android.contactsio.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String CONTENT_AUTHORITY = "com.example.android.contactsio";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CONTACTS = "contacts";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private Contract() {
    }

    /**
     * Inner class that defines constant values for the contacts database table.
     * Each entry in the table represents a single contact.
     */
    public static final class ContactEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of contacts.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;



        /** Name of database table for contacts
         *  */
        public final static String TABLE_NAME = "contacts";



        /**
         * Unique ID number for the contact (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Name of the contact.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CONTACT_NAME ="name";


        /**
         * Number of the contact.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CONTACT_NUMBER="number";



        /**
         * Task for the contact.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CONTACT_TASK="task";

        public final static String COLUMN_CONTACT_PROFILE_PIC="pics";




    }
}