package com.example.android.contactsio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.PermissionChecker;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.android.contactsio.data.Contract.ContactEntry;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("ClickableViewAccessibility")
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Uri mCurrentUri;
    private EditText nameEdit,numberEdit,taskEdit;
    private CircleImageView circleImageView;
    private FloatingActionButton fab;

    private boolean mHasChanged = false;

    private final View.OnTouchListener mTouchListener;
    {
        mTouchListener = (v, event) -> {
            mHasChanged = true;
            return false;
        };
    }

    private String contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        circleImageView=findViewById(R.id.profile_editor_view);
        nameEdit=findViewById(R.id.name_edittext);
        numberEdit=findViewById(R.id.number_edittext);
        taskEdit=findViewById(R.id.task_edittext);
        fab=findViewById(R.id.fab_img_input);

        fab.setOnTouchListener(mTouchListener);
        nameEdit.setOnTouchListener(mTouchListener);
        numberEdit.setOnTouchListener(mTouchListener);
        taskEdit.setOnTouchListener(mTouchListener);

        Intent intent=getIntent();
        mCurrentUri=intent.getData();
        if(mCurrentUri==null){
            setTitle("Add a Contact");
            invalidateOptionsMenu();
        }else{
            setTitle("Edit Contact");
            LoaderManager.getInstance(this).initLoader(0,null,this);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

















    private Bitmap convertByteArrayToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private byte[] convertImageToByteArray(CircleImageView imageView){
        Bitmap bitmap =( (BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void saveContact(){
        String nameString=nameEdit.getText().toString().trim();
        String numberString=numberEdit.getText().toString().trim();
        String taskString=taskEdit.getText().toString().trim();

        if(!ContactEntry.isValidNumber(numberString)){
            Toast.makeText(this, "Please Enter a valid 10 digit mobile Number" ,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(nameString.length()==0){
            Toast.makeText(this, "Please enter a name" ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues=new ContentValues();
        contentValues.put(ContactEntry.COLUMN_CONTACT_NAME,nameString);
        contentValues.put(ContactEntry.COLUMN_CONTACT_NUMBER,numberString);
        contentValues.put(ContactEntry.COLUMN_CONTACT_TASK,taskString);

        if(mCurrentUri==null){
            Uri newUri=getContentResolver().insert(ContactEntry.CONTENT_URI,contentValues);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_contact_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            int rowUpdated=getContentResolver().update(mCurrentUri,contentValues,null,null);
            if(rowUpdated<0){
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_contact_failed),
                        Toast.LENGTH_SHORT).show();
            }else{
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_contact_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete,(dialog, id) ->{
            deleteContact();
        } );
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // and continue editing the pet.
            if (dialog != null) {
                dialog.cancel();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteContact() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }




















    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                (dialogInterface, i) -> {
                    // User clicked "Discard" button, close the current activity.
                    finish();
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            case R.id.save_contact:
                saveContact();
                return true;
            case R.id.delete_this_contact:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.call_this_contact:
                if(!ContactEntry.isValidNumber(contactNumber)){
                    Toast.makeText(this, "No valid mobile number is saved!!" ,
                            Toast.LENGTH_SHORT).show();
                }else{
//                    call intent
                }
                return true;
            case R.id.share_contact:
                if(!ContactEntry.isValidNumber(contactNumber)){
                    Toast.makeText(this, "No valid mobile number is saved!!" ,
                            Toast.LENGTH_SHORT).show();
                }else{
//                    share contact
                }
                return true;
            case R.id.message_this_contact:
                if(!ContactEntry.isValidNumber(contactNumber)){
                    Toast.makeText(this, "No valid mobile number is saved!!" ,
                            Toast.LENGTH_SHORT).show();
                }else{
//                    sms intent
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }























    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_NAME,
                ContactEntry.COLUMN_CONTACT_NUMBER,
                ContactEntry.COLUMN_CONTACT_TASK};
        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if(data.moveToFirst()){
            String contactName=data.getString(data.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_NAME));
            contactNumber=data.getString(data.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_NUMBER));
            String contactTask=data.getString(data.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_TASK));

            nameEdit.setText(contactName);
            numberEdit.setText(contactNumber);
            taskEdit.setText(contactTask);

            data.close();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nameEdit.setText(null);
        numberEdit.setText(null);
        taskEdit.setText(null);
    }
}