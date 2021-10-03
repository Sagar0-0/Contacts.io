package com.example.android.contactsio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android.contactsio.data.Contract;

import com.example.android.contactsio.data.Contract.ContactEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

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
            Uri currentContactUri= ContentUris.withAppendedId(ContactEntry.CONTENT_URI,id);
            intent.setData(currentContactUri);
            startActivity(intent);
        });

    }






    private byte[] convertImageToByteArray(ImageView imageView){
        Bitmap bitmap =( (BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }










    private void insertDummyData() {

        ContentValues values = new ContentValues();
        values.put(Contract.ContactEntry.COLUMN_CONTACT_NAME, "");
        values.put(Contract.ContactEntry.COLUMN_CONTACT_NUMBER, "");



        Uri uri=getContentResolver().insert(ContactEntry.CONTENT_URI,values);
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

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all Contacts?");
        builder.setPositiveButton(R.string.delete, (dialog, id) -> {
            deleteAll();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(Contract.ContactEntry.CONTENT_URI, null, null);
        if(rowsDeleted>-1){
            Toast.makeText(this,"Contacts Deleted Successfully",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"No contacts to delete",
                    Toast.LENGTH_SHORT).show();
        }
     }

    private void exitactivitydialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you really want to exit ?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }



















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dummy_data:
                insertDummyData();
                break;
            case R.id.delete_all_contacts:
                showDeleteConfirmationDialog();
                break;
            case R.id.exit:
                exitactivitydialog();
                break;
            case R.id.gmail:
                Intent mailIntent=new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"sagar.0dev@gmail.com"});
                if(mailIntent.resolveActivity(getPackageManager())!= null){
                    startActivity(mailIntent);
                }
                break;
            case R.id.github:
                String github="https://github.com/Sagar0-0";
                Intent githubIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(github));
                startActivity(githubIntent);
                break;
            case R.id.twitter:
                String twitter="https://twitter.com/sagar0_o";
                Intent twitterIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                startActivity(twitterIntent);
                break;
            case R.id.linkedin:
                String linkedin="https://www.linkedin.com/in/sagar-malhotra-7021b0204/";
                Intent linkedinIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin));
                startActivity(linkedinIntent);
                break;
            case R.id.instagram:
                String insta="https://www.instagram.com/_sagar_malhotra_/";
                Intent instaIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(insta));
                startActivity(instaIntent);
                break;
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
                ContactEntry.CONTENT_URI,
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