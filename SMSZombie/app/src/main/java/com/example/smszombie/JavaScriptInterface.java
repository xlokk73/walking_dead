package com.example.smszombie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    private final Activity activity;


    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    // needs SMS permission to be enabled (does not prompt for it)
    @JavascriptInterface
    public void JsSendSMS(String phoneNo, String msg){
        Log.i(WebViewActivity.TAG, "Received: " + phoneNo + " " + msg)                                                                                                                                                                                                              ;
        if (!(phoneNo.isEmpty()  || msg.isEmpty())) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void JsCloseApp() {
        activity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }


}