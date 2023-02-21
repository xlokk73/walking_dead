package com.example.smszombie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;

public class WebViewActivity extends AppCompatActivity {

    public static String TAG = "SMS_ZOMBIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onResume() {
        super.onResume();

        WebView myWebView = (WebView) findViewById(R.id.webview);

        // Some WebView settings
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.getSettings().setDomStorageEnabled(true);

        // Dynamically load JavaScript interface class
        Context context = getApplicationContext();

        String path = context.getFilesDir().getAbsolutePath();
        Log.i(TAG, "PATH: " + path);

        // load the apk
        String apkPath = path + "/dynamic-code.apk";
        Log.i(TAG, "Looking for apk at: " + apkPath);

        ClassLoader classLoader = new DexClassLoader(apkPath, context.getCacheDir().getAbsolutePath(), null, this.getClass().getClassLoader());

        // load the class
        Class<?> cls = null;
        try {
            Log.i(TAG, "Loading class");
            cls = classLoader.loadClass("com.example.smszombieextra.JavaScriptInterface");
        } catch (Exception e) {
            Log.i(TAG, "Error loading class: " + e.getMessage());
            e.printStackTrace();
        }

        // create an instance of the class
        Object instance = null;
        try {
            Log.i(TAG, "Creating instance");
            Constructor<?> constructor = cls.getConstructor(Activity.class);
            instance = constructor.newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.i(TAG, "Error creating instance: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        myWebView.addJavascriptInterface(instance, "JSInterface");


        // opens ALL new links in the WenView instead of browser
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            public void onPageFinished(WebView view, String url) {
            }

        });

        // Get data from deeplink ex:
        // `adb shell am start -d "walkingdead://smszombie/?url=https://google.com"`
        Uri data = getIntent().getData();
        String url = data.getQueryParameter("url");

        // Load URL
        if (url!=null) {
            myWebView.loadUrl(url);
        }

        myWebView.reload();
    }
}
