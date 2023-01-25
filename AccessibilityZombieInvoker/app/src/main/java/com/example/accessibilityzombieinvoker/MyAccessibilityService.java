package com.example.accessibilityzombieinvoker;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Stack;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    public boolean redirected = false;

    // This method waits for webview to be opened and then calls redirectURL
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName()!= null && event.getPackageName().equals("org.chromium.webview_shell") && !redirected) {
            redirectURL("www.wikipedia.org");
        }
    }

    // This method enters the URL into the search bar public void redirectURL(String url) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("org.chromium.webview_shell:id/url_field");
            if (nodes != null && nodes.size() > 0) {
                AccessibilityNodeInfo node = nodes.get(0);
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, url);
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                loadUrl();
            }
        }
    }

    // This method loads the url by clicking the load url button
    void loadUrl(){
        AccessibilityNodeInfo node = getRootInActiveWindow().getChild(2);
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        redirected = true;
    }

    // this method starts the webview shell
    void startWebView() {
        Intent intent = new Intent();
        intent.setClassName("org.chromium.webview_shell", "org.chromium.webview_shell.WebViewBrowserActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {
        // Do nothing
    }
}
