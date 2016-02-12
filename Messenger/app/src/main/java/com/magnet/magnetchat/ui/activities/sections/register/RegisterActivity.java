package com.magnet.magnetchat.ui.activities.sections.register;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.MagnetError;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;
import com.magnet.magnetchat.ui.custom.FEditText;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.max.android.ApiError;

import butterknife.InjectView;

public class RegisterActivity extends BaseActivity {

    @InjectView(R.id.registerFirstName)
    FEditText editFirstName;
    @InjectView(R.id.registerLastName)
    FEditText editLastName;
    @InjectView(R.id.registerEmail)
    FEditText editEmail;
    @InjectView(R.id.registerPassword)
    FEditText editPassword;
    @InjectView(R.id.registerRepeatPassword)
    FEditText editRepeatPassword;
    @InjectView(R.id.viewProgress)
    View viewProgress;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_register;
    }

    @Override
    protected int getBaseViewID() {
        return R.id.main_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListeners(R.id.registerSaveBtn, R.id.buttonBack);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.registerSaveBtn:
                onRegister();
                break;
            case R.id.buttonBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private final UserHelper.OnRegisterListener onRegisterListener = new UserHelper.OnRegisterListener() {
        @Override
        public void onFailedRegistration(ApiError apiError) {
            try {
                MagnetError error = new Gson().fromJson(apiError.getMessage(), MagnetError.class);
                String message = null;
                if (error != null) {
                    message = error.getErrorMessage();
                }
                showMessage("Can't create account", message);
                changeLoginMode(false);
            } catch (Exception ex) {
                AppLogger.error(this, ex.toString());
                changeLoginMode(false);
                showMessage("Error while user creating.\nPlease try again.");
            } finally {
                changeLoginMode(false);
            }
        }

        @Override
        public void onSuccess() {
            changeLoginMode(false);
            startActivity(EditProfileActivity.class, true);
        }

        @Override
        public void onFailedLogin(ApiError apiError) {
            try {
                MagnetError error = new Gson().fromJson(apiError.getMessage(), MagnetError.class);
                String message = null;
                if (error != null) {
                    message = error.getErrorMessage();
                }
                showMessage("Can't login", message);
                changeLoginMode(false);
            } catch (Exception e) {
                AppLogger.error(this, e.toString());
                changeLoginMode(false);
                showMessage("Error while user creating.\nPlease try again.");
            } finally {
                changeLoginMode(false);
            }
        }
    };

    private void changeLoginMode(boolean runRegister) {
        hideKeyboard();
        if (runRegister) {
            viewProgress.setVisibility(View.VISIBLE);
        } else {
            viewProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Method which provide the user registration
     */
    private void onRegister() {
        String firstName = editFirstName.getStringValue();
        String lastName = editLastName.getStringValue();
        String email = editEmail.getStringValue();
        String password = editPassword.getStringValue();
        String passwordRepeat = editRepeatPassword.getStringValue();

        if (!checkStrings(firstName, lastName, email, password, passwordRepeat)) {
            showMessage("Input all fields");
            return;
        }
        if (!UserHelper.isEmail(email)) {
            showMessage("Invalid email", "Please enter the valid email");
            return;
        }
        if (!password.equals(passwordRepeat)) {
            showMessage("Passwords do not match", "Please enter your password and verify your password again");
            return;
        }
        if (password.length() < 6) {
            showMessage("Password is too short", "Password should be at least 6 characters");
            return;
        }
        changeLoginMode(true);
        UserHelper.register(firstName, lastName, email, password, onRegisterListener);
    }

}
