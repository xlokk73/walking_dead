package com.example.zombieinvoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("walkingdead://smszombie/?url=http://192.168.1.134:1313"));
        startActivity(viewIntent);
    }
}