package com.example.smszombie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class WebViewActivity extends AppCompatActivity {


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        // Some WebView settings
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.getSettings().setDomStorageEnabled(true);

        // Add JavaScript interface to enable calling Java code from web
        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, "JSInterface");

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

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
        else {
            myWebView.loadUrl("https://www.wikipedia.org");
        }
    }
}
