package com.magnet.magnetchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.InternetConnection;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.debug("SessionStatus", User.getSessionStatus());
        if(User.SessionStatus.LoggedIn == User.getSessionStatus()) {
            goToHomeActivity();
        } else if(User.SessionStatus.CanResume == User.getSessionStatus()) {
            User.resumeSession(new ApiCallback<Boolean>() {
                @Override public void success(Boolean aBoolean) {
                    if(aBoolean) {
                        goToHomeActivity();
                    } else {
                        handleError("");
                    }
                }

                @Override public void failure(ApiError apiError) {
                    handleError(apiError.getMessage());
                }

                private void handleError(String errorMessage) {
                    Logger.debug(TAG, "Failed to resume session due to ", errorMessage);
                    setupView();
                }
            });
        } else {
            setupView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getCurrentUser() != null) {
            finish();
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
                runLoginFromFields();
                break;
        }
    }

    private void setupView() {
        setContentView(R.layout.activity_login);
        setOnClickListeners(R.id.loginCreateAccountBtn, R.id.loginForgotPaswordBtn, R.id.loginSignInBtn);
        remember = (CheckBox) findViewById(R.id.loginRemember);
    }

    private void runLoginFromFields() {
        if (InternetConnection.getInstance().isAnyConnectionAvailable()) {
            final String email = getFieldText(R.id.loginEmail);
            final String password = getFieldText(R.id.loginPassword);
            boolean shouldRemember = remember.isChecked();
            if (checkStrings(email, password)) {
                changeLoginMode(true);
                UserHelper.getInstance().login(email, password, shouldRemember, loginListener);
            } else {
                showLoginFailed();
            }
        } else {
            showNoConnection();
        }
    }

    private void showLoginFailed() {
        showInfoDialog("Email or password is incorrect", "Please check your information and try again");
    }

    private void showLoginErrorCause(String cause) {
        showInfoDialog(null, cause + " Please try again");
    }

    private void showNoConnection() {
        showInfoDialog("No connection", "Please check your Internet connection and try again");
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

    private void goToHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
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
            changeLoginMode(false);
            if (apiError.getMessage().contains(MMX.FailureCode.BAD_REQUEST.getDescription())) {
                showLoginErrorCause("A bad request submitted to the server.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_AUTH_FAILED.getDescription())) {
                showLoginErrorCause("Server authentication failure.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_CONCURRENT_LOGIN.getDescription())) {
                showLoginErrorCause("Concurrent logins are attempted.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.DEVICE_ERROR.getDescription())) {
                showLoginErrorCause("A client error.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVER_ERROR.getDescription())) {
                showLoginErrorCause("A server error.");
            } else if (apiError.getMessage().contains(MMX.FailureCode.SERVICE_UNAVAILABLE.getDescription())) {
                showLoginErrorCause("The MMX service is not available due to network issue or server issue.");
            } else {
                showLoginFailed();
            }
        }
    };

}
