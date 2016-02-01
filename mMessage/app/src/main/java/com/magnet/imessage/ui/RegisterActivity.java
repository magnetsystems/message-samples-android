package com.magnet.imessage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.magnet.imessage.R;
import com.magnet.imessage.helpers.UserHelper;
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
                if (checkStrings(firstName, lastName, email, password, passwordRepeat)) {
                    if (password.equals(passwordRepeat)) {
                        changeLoginMode(true);
                        UserHelper.getInstance().registerUser(firstName, lastName, email, password, onRegisterListener);
                    } else {
                        showInfoDialog("Passwords do not match", "Please enter your password and verify your password again");
                    }
                } else {
                    showInfoDialog("Registration", "Enter all fields");
                }
                break;
            case R.id.registerReturnBtn:
                finish();
                break;
        }
    }

    private UserHelper.OnRegisterListener onRegisterListener = new UserHelper.OnRegisterListener() {
        @Override
        public void onFailedRegistration(ApiError apiError) {
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
