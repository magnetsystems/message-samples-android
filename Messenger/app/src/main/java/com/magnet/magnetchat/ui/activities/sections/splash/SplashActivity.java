package com.magnet.magnetchat.ui.activities.sections.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.activities.sections.home.HomeActivity;
import com.magnet.magnetchat.ui.activities.sections.login.LoginActivity;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

public class SplashActivity extends BaseActivity {
    private final static String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DURATION = 3000;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.debug("SessionStatus", User.getSessionStatus());

        if (User.SessionStatus.LoggedIn == User.getSessionStatus()) {
            goToHomeActivity();
        } else if (User.SessionStatus.CanResume == User.getSessionStatus()) {
            User.resumeSession(new ApiCallback<Boolean>() {
                @Override
                public void success(Boolean aBoolean) {
                    if (aBoolean) {
                        goToHomeActivity();
                    } else {
                        handleError("");
                    }
                }

                @Override
                public void failure(ApiError apiError) {
                    handleError(apiError.getMessage());
                }

                private void handleError(String errorMessage) {
                    Logger.debug(TAG, "Failed to resume session due to ", errorMessage);
                    goToLoginActivity();
                }
            });
        } else {
            goToLoginActivity();
        }

    }

    @Override
    public void onClick(View v) {

    }

    private void goToHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void goToLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DURATION);
    }

}
