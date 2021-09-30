package com.example.android.contactsio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.contactsio.data.Contract;

@SuppressLint("ClickableViewAccessibility")
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Uri mCurrentUri;


    private boolean mHasChanged = false;

    private final View.OnTouchListener mTouchListener;
    {
        mTouchListener = (v, event) -> {
            mHasChanged = true;
            return false;
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        Intent intent=getIntent();
        mCurrentUri=intent.getData();
        if(mCurrentUri==null){
            setTitle("Add a Contact");
            invalidateOptionsMenu();
        }else{
            setTitle("Edit Contact");
            LoaderManager.getInstance(this).initLoader(0,null,this);
        }

    }





    @Override
    public void onBackPressed() {
        if(!mHasChanged){
            super.onBackPressed();
            return;
        }
        showUnsavedChangesDialog();
    }
    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, (dialog,id)->{
            finish();
        });
        builder.setNegativeButton(R.string.keep_editing, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }














    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete,(dialog, id) -> {
            deleteContact();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // and continue editing the pet.
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteContact() {
        if(mCurrentUri!=null){
            int n=getContentResolver().delete(mCurrentUri,null,null);
            if(n>-1){
                Toast.makeText(this, getString(R.string.editor_delete_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.editor_delete_contact_failed),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_options,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.more_editing_options);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

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

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}