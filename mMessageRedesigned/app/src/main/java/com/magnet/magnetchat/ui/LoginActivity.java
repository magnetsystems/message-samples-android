package com.magnet.magnetchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.InternetConnectionManager;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.ui.custom.FEditText;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;

import butterknife.InjectView;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.loginRemember)
    CheckBox remember;
    @InjectView(R.id.loginEmail)
    FEditText editEmail;
    @InjectView(R.id.loginPassword)
    FEditText editPassword;
    @InjectView(R.id.viewProgressLogin)
    View viewLoginProgress;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected int getBaseViewID() {
        return R.id.main_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListeners(R.id.loginCreateAccountBtn, R.id.loginSignInBtn);
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
                    changeLoginMode(false);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (User.getCurrentUser() != null) {
            changeLoginMode(true);
        } else {
            changeLoginMode(false);
        }

    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.loginCreateAccountBtn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginSignInBtn:
                runLoginFromFields();
                break;
        }
    }

    private void runLoginFromFields() {
        if (InternetConnectionManager.getInstance().isAnyConnectionAvailable()) {
            final String email = editEmail.getStringValue();
            final String password = editPassword.getStringValue();
            boolean shouldRemember = remember.isChecked();
            if (checkStrings(email, password)) {
                changeLoginMode(true);
                UserHelper.login(email, password, shouldRemember, loginListener);
            } else {
                showLoginFailed();
            }
        } else {
            showNoConnection();
        }
    }

    private void showLoginFailed() {
        showMessage("Email or password is incorrect", "Please check your information and try again");
        changeLoginMode(false);
    }

    private void showLoginErrorCause(String cause) {
        showMessage(cause + " Please try again");
        changeLoginMode(false);
    }

    private void showNoConnection() {
        showMessage("No connection", "Please check your Internet connection and try again");
        changeLoginMode(false);
    }

    private void changeLoginMode(boolean runLogining) {
        if (runLogining) {
            viewLoginProgress.setVisibility(View.VISIBLE);
        } else {
            viewLoginProgress.setVisibility(View.GONE);
        }
    }

    private void goToHomeActivity() {
        startActivity(HomeActivity.class, true);
    }

    private void goToHomeActivity(boolean changeLoginMode) {
        startActivity(HomeActivity.class, true);
        changeLoginMode(changeLoginMode);
    }

    private UserHelper.OnLoginListener loginListener = new UserHelper.OnLoginListener() {
        @Override
        public void onSuccess() {
            goToHomeActivity(false);
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
