package com.example.zombieinvoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Redirect to deeplink via intent
//        Intent viewIntent =
//                new Intent("android.intent.action.VIEW",
//                        Uri.parse("walkingdead://smszombie/?url=http://192.168.1.134:1313"));
//        startActivity(viewIntent);

        // Redirect to deeplink via Chrome 1
//        String urlString = "http://192.168.1.134:1312";
//        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage("com.android.chrome");
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException ignored) {
//        }

        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        String url = "http://192.168.1.134:1312";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}