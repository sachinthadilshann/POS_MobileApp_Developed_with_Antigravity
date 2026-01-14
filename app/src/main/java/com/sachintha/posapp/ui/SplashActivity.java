package com.sachintha.posapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sachintha.posapp.R;
import com.sachintha.posapp.utils.SessionManager;

/**
 * Splash Screen Activity
 * Displays app logo and transitions to Login or Main activity
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animate logo
        ImageView logoImage = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView tagline = findViewById(R.id.splash_tagline);

        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1000);

        logoImage.startAnimation(fadeIn);
        appName.startAnimation(fadeIn);

        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slideUp.setDuration(1000);
        slideUp.setStartOffset(500);
        tagline.startAnimation(slideUp);

        // Navigate after splash duration
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = SessionManager.getInstance(this);
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
