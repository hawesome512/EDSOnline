package com.xseec.eds.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Administrator on 2018/10/12.
 */

public class ContentHelper {

    public static void startContact(Activity activity,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    //获取：姓名-电话
    public static String getContactInfo(Context context, Intent data) {
        Uri uri = data.getData();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                .DISPLAY_NAME));
        cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,null,null);
        cursor.moveToFirst();
        String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.NUMBER)).replaceAll("\\s+","");
        cursor.close();
        return name+"-"+phone;
    }

}
