package com.magnet.magnetchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.MagnetError;
import com.magnet.max.android.ApiError;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setOnClickListeners(R.id.registerReturnBtn, R.id.registerSaveBtn);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.registerSaveBtn:
                String firstName = getFieldText(R.id.registerFirstName);
                String lastName = getFieldText(R.id.registerLastName);
                String email = getFieldText(R.id.registerEmail);
                String password = getFieldText(R.id.registerPassword);
                String passwordRepeat = getFieldText(R.id.registerRepeatPassword);
                if (!checkStrings(firstName, lastName, email, password, passwordRepeat)) {
                    showInfoDialog(null, "Input all fields");
                    return;
                }
                if (!UserHelper.checkEmail(email)) {
                    showInfoDialog("Invalid email", "Please enter the valid email");
                    return;
                }
                if (!password.equals(passwordRepeat)) {
                    showInfoDialog("Passwords do not match", "Please enter your password and verify your password again");
                    return;
                }
                if (password.length() < 6) {
                    showInfoDialog("Password is too short", "Password should be at least 6 characters");
                    return;
                }
                changeLoginMode(true);
                UserHelper.getInstance().registerUser(firstName, lastName, email, password, onRegisterListener);
                break;
            case R.id.registerReturnBtn:
                finish();
                break;
        }
    }

    private UserHelper.OnRegisterListener onRegisterListener = new UserHelper.OnRegisterListener() {
        @Override
        public void onFailedRegistration(ApiError apiError) {
            MagnetError error = new Gson().fromJson(apiError.getMessage(), MagnetError.class);
            String message = null;
            if (error != null) {
               message = error.getErrorMessage();
            }
            showInfoDialog("Can't create account", message);
            changeLoginMode(false);
        }

        @Override
        public void onSuccess() {
            changeLoginMode(false);
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            MagnetError error = new Gson().fromJson(apiError.getMessage(), MagnetError.class);
            String message = null;
            if (error != null) {
                message = error.getErrorMessage();
            }
            showInfoDialog("Can't login", message);
            changeLoginMode(false);
        }
    };

    private void changeLoginMode(boolean runRegister) {
        if (runRegister) {
            findViewById(R.id.registerSaveBtn).setVisibility(View.GONE);
            findViewById(R.id.registerProgress).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.registerSaveBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.registerProgress).setVisibility(View.GONE);
        }
    }

}
