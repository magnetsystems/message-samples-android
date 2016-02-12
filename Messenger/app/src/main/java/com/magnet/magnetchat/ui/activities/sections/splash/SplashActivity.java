package com.magnet.magnetchat.ui.activities.sections.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.activities.sections.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DURATION);
    }
}
