package com.example.smszombie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    private final Activity activity;

    // needs SMS permission to be enabled (does not prompt for it)
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void JsSendSMS(String phoneNo, String msg){
        if (!(phoneNo.isEmpty()  || msg.isEmpty())) {
            sendSMS(phoneNo, msg);
        }
    }
}