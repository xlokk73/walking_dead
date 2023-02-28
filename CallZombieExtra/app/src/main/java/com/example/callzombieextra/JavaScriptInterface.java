package com.example.callzombieextra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.Date;

public class JavaScriptInterface {
    public static String TAG = "CALL_ZOMBIE";
    private final Activity activity;

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void JsDumpCallLog() {
        Log.i(TAG, "Dumping Call Log");
        final String unknown = "Unknown";
        final String outgoing = "OUTGOING";
        final String incoming = "INCOMING";
        final String missed = "MISSED";

        Cursor cur = activity.getContentResolver()
                .query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cur.getColumnIndex(CallLog.Calls.TYPE);
        int date = cur.getColumnIndex(CallLog.Calls.DATE);
        int duration = cur.getColumnIndex(CallLog.Calls.DURATION);
        int name = cur.getColumnIndex(CallLog.Calls.CACHED_NAME);


        while (cur.moveToNext()) {

            Log.i(TAG, cur.getString(name));
            Log.i(TAG, cur.getString(number));
            Log.i(TAG, cur.getString(duration));

            String callDate = cur.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));

            Log.i(TAG,  callDayTime.toString());

            String callType = cur.getString(type);
            String dir = unknown;

            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = outgoing;
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = incoming;
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = missed;
                    break;
            }
            Log.i(TAG, dir);
        }

        cur.close();
    }

    @SuppressLint("Range")
    @JavascriptInterface
    public void JsDumpContacts() {
        Log.i(TAG, "Dumping Contacts");
        final String _id = "_id";
        final String displayName = "display_name";
        final String contactId = "contact_id";
        final String data1 = "data1";

        ContentResolver cr = activity.getContentResolver();

        if (Integer.parseInt(Build.VERSION.RELEASE.substring(0, 1)) >= 2) {
            Uri ContactUri = ContactsContract.Contacts.CONTENT_URI;
            Uri PhoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Uri EmailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            Cursor cur = cr.query(ContactUri, null, null, null, null);

            while (cur.moveToNext()) {
                @SuppressLint("Range") String id = cur.getString(cur.getColumnIndex(_id));

                // Name
                Log.i(TAG, cur.getString(cur.getColumnIndex(displayName)));

                // Number
                Cursor pCur = cr.query(PhoneUri, null, contactId + " = ?",
                        new String[]{id}, null);
                while (pCur.moveToNext()) {
                    Log.i(TAG, pCur.getString(pCur.getColumnIndex(data1)));
                }
                pCur.close();

                // Email
                Cursor emailCur = cr.query(EmailUri, null, contactId
                        + " = ?", new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    Log.i(TAG, emailCur
                            .getString(emailCur.getColumnIndex(data1)));
                }
                emailCur.close();
            }

            cur.close();
        }
    }

    @JavascriptInterface
    public void JsCloseApp() {
        activity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}