package com.example.smszombie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    private final Activity activity;

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void JsSendSMS(){
        Toast.makeText(activity, "JavaScript interface works!!", Toast.LENGTH_SHORT).show();
    }
}