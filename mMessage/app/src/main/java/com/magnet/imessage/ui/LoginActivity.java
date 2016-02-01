package com.magnet.imessage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.magnet.imessage.R;
import com.magnet.imessage.helpers.UserHelper;
import com.magnet.imessage.preferences.UserPreference;
import com.magnet.imessage.util.Logger;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;

public class LoginActivity extends BaseActivity {

    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setOnClickListeners(R.id.loginCreateAccountBtn, R.id.loginForgotPaswordBtn, R.id.loginSignInBtn);
        remember = (CheckBox) findViewById(R.id.loginRemember);
        if (User.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else if (UserPreference.getInstance().readCredence() != null) {
            changeLoginMode(true);
            UserHelper.getInstance().relogin(loginListener);
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.loginCreateAccountBtn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginForgotPaswordBtn:
//                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginSignInBtn:
                final String email = getFieldText(R.id.loginEmail);
                final String password = getFieldText(R.id.loginPassword);
                boolean shouldRemember = remember.isChecked();
                if (checkStrings(email, password)) {
                    changeLoginMode(true);
                    UserHelper.getInstance().login(email, password, shouldRemember, loginListener);
                } else {
                    showLoginFailed();
                }
                break;
        }
    }

    private void showLoginFailed() {
        showInfoDialog("Email or password is incorrect", "Please check your information and try again");
    }

    private void changeLoginMode(boolean runLogining) {
        if (runLogining) {
            findViewById(R.id.loginSignInBtn).setVisibility(View.GONE);
            findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.loginSignInBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.loginProgress).setVisibility(View.GONE);
        }
    }

    private UserHelper.OnLoginListener loginListener = new UserHelper.OnLoginListener() {
        @Override
        public void onSuccess() {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            changeLoginMode(false);
            finish();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            Logger.error("login", apiError);
            showLoginFailed();
            changeLoginMode(false);
        }
    };

}
