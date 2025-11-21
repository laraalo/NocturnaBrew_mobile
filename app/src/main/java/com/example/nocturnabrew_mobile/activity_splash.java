package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class activity_splash extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(()->{
            Intent intent = new Intent(activity_splash.this, WelcomeActivity.class );
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
