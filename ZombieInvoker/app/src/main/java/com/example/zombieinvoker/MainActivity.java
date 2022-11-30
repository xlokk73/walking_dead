package com.example.zombieinvoker;

import androidx.appcompat.app.AppCompatActivity;

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

        // Redirect to deeplink via chrome
        String urlString = "http://192.168.1.134:1312";
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
        }
    }
}