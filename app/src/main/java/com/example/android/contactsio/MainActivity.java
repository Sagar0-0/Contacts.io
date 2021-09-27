package com.example.android.contactsio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.contactsio.data.Contract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ContactsAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting fab
        FloatingActionButton fab=findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,EditorActivity.class));
        });

        // finding listview
        ListView list=findViewById(R.id.list);
        //setting empty view to list
        View emptyView = findViewById(R.id.empty_view);
        list.setEmptyView(emptyView);


        mCursorAdapter=new ContactsAdapter(this,null);
        list.setAdapter(mCursorAdapter);
        LoaderManager.getInstance(this).initLoader(0,null,this);

        list.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent=new Intent(MainActivity.this,EditorActivity.class);
            Uri currentPetUri= ContentUris.withAppendedId(Contract.ContactEntry.CONTENT_URI,id);
            intent.setData(currentPetUri);
            startActivity(intent);
        });

    }

















    private void insertDummyData() {

        ContentValues values = new ContentValues();
        values.put(Contract.ContactEntry.COLUMN_CONTACT_NAME, "Sagar");
        values.put(Contract.ContactEntry.COLUMN_CONTACT_NUMBER, "7015248932");



        Uri uri=getContentResolver().insert(Contract.ContactEntry.CONTENT_URI,values);
        // Show a toast message depending on whether or not the insertion was successful
        if (uri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this,"Unexpected Error Occured",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this,"Dummy Contact added",
                    Toast.LENGTH_SHORT).show();
        }
    }















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.dummy_data){
            insertDummyData();
        }
        return super.onOptionsItemSelected(item);
    }


























    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                Contract.ContactEntry._ID,
                Contract.ContactEntry.COLUMN_CONTACT_NAME,
                Contract.ContactEntry.COLUMN_CONTACT_NUMBER,
                Contract.ContactEntry.COLUMN_CONTACT_TASK};
        return new CursorLoader(this,
                Contract.ContactEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


}