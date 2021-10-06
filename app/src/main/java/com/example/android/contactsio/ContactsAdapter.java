package com.example.android.contactsio;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.example.android.contactsio.data.Contract;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends CursorAdapter {


    public ContactsAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name=view.findViewById(R.id.contact_name);
        TextView num=view.findViewById(R.id.contact_number);
        CircleImageView profile=view.findViewById(R.id.contact_profile);

        String contactname=cursor.getString(cursor.getColumnIndexOrThrow(Contract.ContactEntry.COLUMN_CONTACT_NAME));
        String contactnumber=cursor.getString(cursor.getColumnIndexOrThrow(Contract.ContactEntry.COLUMN_CONTACT_NUMBER));
        byte[] imagebytes=cursor.getBlob(cursor.getColumnIndexOrThrow(Contract.ContactEntry.COLUMN_CONTACT_PROFILE_PIC));
        Bitmap imageBitmap= Contract.ContactEntry.convertByteArrayToBitmap(imagebytes);

        if(TextUtils.isEmpty(contactname)){
            contactname="--Name--";
        }
        if(TextUtils.isEmpty(contactnumber)){
            contactnumber="xxxxxxxxxx";
        }
        if(imagebytes.length==1){
            profile.setImageResource(R.drawable.ic_profile);
        }else{
            profile.setImageBitmap(imageBitmap);
        }
        name.setText(contactname);
        num.setText("+91" + contactnumber);
    }
}
